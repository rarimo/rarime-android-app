package com.rarilabs.rarime.api.voting

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.api.voting.models.ByteArrayDeserializer
import com.rarilabs.rarime.api.voting.models.ByteArrayListDeserializer
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.api.voting.models.PollCriteria
import com.rarilabs.rarime.api.voting.models.PollCriteriaStatus
import com.rarilabs.rarime.api.voting.models.PollResult
import com.rarilabs.rarime.api.voting.models.PollVoteOption
import com.rarilabs.rarime.api.voting.models.ProposalIndexed
import com.rarilabs.rarime.api.voting.models.ProposalStatus
import com.rarilabs.rarime.api.voting.models.Question
import com.rarilabs.rarime.api.voting.models.QuestionResult
import com.rarilabs.rarime.api.voting.models.QuestionResultOption
import com.rarilabs.rarime.api.voting.models.UserInPoll
import com.rarilabs.rarime.api.voting.models.VoteRequest
import com.rarilabs.rarime.api.voting.models.VoteRequestAttributes
import com.rarilabs.rarime.api.voting.models.VoteRequestData
import com.rarilabs.rarime.api.voting.models.VoteResponse
import com.rarilabs.rarime.api.voting.models.VotingData
import com.rarilabs.rarime.api.voting.models.uint8ToProposalStatus
import com.rarilabs.rarime.contracts.rarimo.StateKeeper
import com.rarilabs.rarime.data.ProofTxFull
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.manager.TestContractManager
import com.rarilabs.rarime.modules.passportScan.calculateAgeFromBirthDate
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.decodeHexString
import identity.CallDataBuilder
import identity.Identity
import identity.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.utils.Numeric
import java.math.BigInteger
import javax.inject.Inject
import kotlin.math.max

class VotingManager @Inject constructor(
    private val votingApiManager: VotingApiManager,
    private val votingContractManager: TestContractManager,
    private val rarimoContractManager: RarimoContractManager,
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager
) {

    private val ZERO_IN_HEX: String = "0x303030303030"

    private val _polls: MutableStateFlow<List<Poll>> = MutableStateFlow(listOf())
    val polls: StateFlow<List<Poll>>
        get() = _polls.asStateFlow()

    private var _selectedPoll = MutableStateFlow<UserInPoll?>(null)

    val selectedPoll: StateFlow<UserInPoll?>
        get() = _selectedPoll.asStateFlow()

    fun totalParticipantsOfSelectedPoll(poll: Poll?): Long {
        val total = poll?.proposalResults?.sumOf { proposalResult ->
            proposalResult.sumOf { it }
        } ?: 0
        return total
    }

    fun questionResults(selectedPoll: Poll?): List<QuestionResult> {
        val poll = selectedPoll ?: return emptyList()
        val results = mutableListOf<QuestionResult>()

        val questionListSize = poll.questionList.size
        val proposalResultsSize = poll.proposalResults.size

        for (i in 0 until minOf(questionListSize, proposalResultsSize)) {
            val question = poll.questionList[i]
            val result = poll.proposalResults[i]

            val options = result.mapIndexedNotNull { index, votes ->
                if (index < question.variants.size) {
                    QuestionResultOption(answer = question.variants[index], votes = votes.toInt())
                } else {
                    null
                }
            }
            results.add(QuestionResult(question = question.title, options = options))
        }

        return results
    }

    fun setSelectedPoll(poll: Poll?) {
        if (poll == null) {
            _selectedPoll.value = null
            return
        }

        val userStatus = getUserStatus()
        val votingData = decodeVotingData(poll)
        val nationalityList =
            votingData.citizenshipWhitelist.map { it.toByteArray().decodeToString() }

        val criteria = createPollCriteria(userStatus, votingData, nationalityList)

        _selectedPoll.value = UserInPoll(
            poll = poll,
            pollCriteriaList = criteria,
            userVerificationStatus = userStatus
        )
    }

    private fun getUserStatus(): PollCriteriaStatus {
        return if (passportManager.passportStatus.value == PassportStatus.ALLOWED) {
            PollCriteriaStatus.VERIFIED
        } else {
            PollCriteriaStatus.NEED_VERIFICATION
        }
    }

    private fun createPollCriteria(
        userStatus: PollCriteriaStatus,
        votingData: VotingData,
        nationalityList: List<String>
    ): List<PollCriteria> {
        if (userStatus == PollCriteriaStatus.NEED_VERIFICATION) {
            return listOf()
        }

        val user = passportManager.passport.value?.personDetails
        val userAge = user?.birthDate?.let { calculateAgeFromBirthDate(it) } ?: 0
        val userNationality = user?.nationality.orEmpty()

        val ageCriteria = votingData.birthDateUpperbound.toInt() <= userAge
        val nationalityCriteria = nationalityList.contains(userNationality)

        return listOf(
            PollCriteria(title = "age", accomplished = ageCriteria),
            PollCriteria(title = "nationality", accomplished = nationalityCriteria)
        )
    }

    fun getSelectedPoll(): UserInPoll? {
        return _selectedPoll.value
    }

    suspend fun loadVotePolls(isRefresh: Boolean = false): List<Poll> {
        if (_polls.value.isEmpty() || isRefresh) {
            _polls.value = loadPolls()
            return _polls.value
        }
        return _polls.value
    }

    fun isVerified(): Boolean {
        val res = passportManager.passport.value
        return res != null
    }

    private val FIRST_POLL_MAX_LIMIT: BigInteger = BigInteger.valueOf(10L)

    private suspend fun loadPolls(): List<Poll> {

        Log.i("Proposal", "load polls")

        val proposal = "0x27ABc0BDfD339d167854c7a14Cf3DDe64bbfDBc9"

        val multicall = "0xcA11bde05977b3631167028862bE2a173976CA11"

        val VOTING_RPC_URL = "https://rpc.qtestnet.org"

        val proposalsStateContract =
            votingContractManager.getProposalsStateContract(proposal)
        val pollsList: MutableList<Poll> = mutableListOf()

        withContext(Dispatchers.IO) {

            val lastProposalId = proposalsStateContract.lastProposalId().send()
            if (lastProposalId.toLong() == 0L) {
                return@withContext emptyList<Poll>()
            }

            val limit = if (FIRST_POLL_MAX_LIMIT > lastProposalId) {
                lastProposalId
            } else {
                FIRST_POLL_MAX_LIMIT
            }.toLong()



            val proposalInfosArrayRaw = Identity.getStateInfosMulticall(
                proposal,
                VOTING_RPC_URL,
                multicall,
                (lastProposalId.toLong() - limit + 1).toString(),
                lastProposalId.toString()
            )

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(ByteArray::class.java, ByteArrayDeserializer())

            gsonBuilder.registerTypeAdapter(
                object : TypeToken<List<ByteArray>>() {}.type, ByteArrayListDeserializer()
            )
            val gson = gsonBuilder.create()

            val proposalInfoListType = object : TypeToken<List<ProposalIndexed>>() {}.type

            val proposalInfos = gson.fromJson<List<ProposalIndexed>>(
                proposalInfosArrayRaw.decodeToString(), proposalInfoListType
            )

            val pollDeferredList = proposalInfos.map { proposalInfoIndexed ->
                async {
                    val proposalInfo = proposalInfoIndexed.ProposalInfo
                    val proposalStatusUint8 = proposalInfo.Status
                    val proposalStatus = uint8ToProposalStatus(proposalStatusUint8)

                    if (proposalStatus == ProposalStatus.DoNotShow) {
                        return@async null
                    }

                    val proposalEventId = proposalsStateContract
                        .getProposalEventId(BigInteger.valueOf(proposalInfoIndexed.Index.toLong()))
                        .send()

                    val votingAddresses = proposalInfo.Config.VotingWhitelist
                    val ipfsLink = proposalInfo.Config.Description

                    val rawData = votingApiManager.getIPFSData(ipfsLink)
                    val voteOptions = mutableListOf<PollVoteOption>()
                    val voteDescription = mutableListOf<String>()

                    rawData.acceptedOptions.forEachIndexed { index, option ->
                        option.variants.forEachIndexed { _index, _option ->
                            voteOptions.add(PollVoteOption(_index, _option))
                        }
                        voteDescription.add(option.title)
                    }

                    val currentDateLongUtc = System.currentTimeMillis() / 1000
                    val proposalSMT = proposalInfo.ProposalSMT

                    Poll(
                        id = proposalInfoIndexed.Index.toLong(),
                        title = rawData.title,
                        description = rawData.description!!,
                        reward = 50L,
                        voteStartDate = proposalInfo.Config.StartTimestamp,
                        isEnded =
                            proposalStatus == ProposalStatus.Ended || currentDateLongUtc > proposalInfo.Config.StartTimestamp.plus(
                                proposalInfo.Config.Duration
                            ),
                        isStarted = proposalStatus == ProposalStatus.Started || currentDateLongUtc > proposalInfo.Config.StartTimestamp,
                        voteEndDate = proposalInfo.Config.StartTimestamp.plus(proposalInfo.Config.Duration),
                        questionList = rawData.acceptedOptions.mapIndexed { index, it ->
                            Question(
                                id = index.toLong(),
                                title = it.title,
                                IsSkippable = false,
                                variants = it.variants
                            )
                        }.toList(),
                        eventId = proposalEventId,
                        votingData = proposalInfo.Config.VotingWhitelistData,
                        votingAddresses = votingAddresses,
                        proposalSMT = proposalSMT,
                        proposalStatus = proposalStatus,
                        proposalResults = proposalInfo.VotingResults,
                        imageUrl = rawData.image
                    )
                }
            }

            val polls = pollDeferredList.awaitAll().filterNotNull().sortedBy { it.id }
            Log.i("Polls", polls.map { it.id }.toString())
            pollsList.addAll(polls)
        }
        return pollsList.reversed()
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun vote(pollResults: List<PollResult>, context: Context) {
        val gson = Gson()
        val stateKeeperContract = rarimoContractManager.getStateKeeper()
        val registrationSmtContract =
            rarimoContractManager.getPoseidonSMT(BaseConfig.REGISTRATION_SMT_CONTRACT_ADDRESS)

        val registrationProof = identityManager.registrationProof.value!!

        val passportInfoKey = registrationProof.pub_signals[1]

        val proofIndex =
            Identity.calculateProofIndex(passportInfoKey, registrationProof.pub_signals[3])

        val pollResultJson = gson.toJson(pollResults)

        val eDocument = passportManager.passport.value

        val smtProofRaw = withContext(Dispatchers.IO) {
            registrationSmtContract.getProof(proofIndex).send()
        }

        val smtProof = ProofTxFull.fromContractProof(smtProofRaw)
        val smtProofJson = gson.toJson(smtProof)

        val profile = identityManager.getProfiler()

        var passportInfoKeyBytes = Identity.bigIntToBytes(passportInfoKey)

        if (passportInfoKeyBytes.size > 32) {
            passportInfoKeyBytes = ByteArray(32 - passportInfoKeyBytes.size) + passportInfoKeyBytes
        } else if (passportInfoKeyBytes.size < 32) {
            val len = 32 - passportInfoKeyBytes.size
            var tempByteArray = ByteArray(len) { 0 }
            tempByteArray += passportInfoKeyBytes
            passportInfoKeyBytes = tempByteArray
        }


        val tupleContractInfo = withContext(Dispatchers.IO) {
            stateKeeperContract.getPassportInfo(passportInfoKeyBytes).send()
        }

        val passportInfo = tupleContractInfo.component1()
        val identityInfo = tupleContractInfo.component2()

        val voteProof = generateVoteProof(
            context,
            profile,
            eDocument!!,
            smtProofJson,
            passportInfoKey,
            pollResultJson.toByteArray(),
            passportInfo,
            identityInfo
        )


        val voteProofJson = gson.toJson(voteProof)

        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildVoteCalldata(
            voteProofJson.toByteArray(),
            selectedPoll.value!!.poll.id,
            pollResultJson.toByteArray(),
            passportManager.passport.value!!.personDetails!!.nationality
        )


        val response = withContext(Dispatchers.IO) {
            sendVote(
                "0x" + callData.toHexString(),
                selectedPoll.value!!.poll.votingAddresses[0],
                selectedPoll.value!!.poll.id
            )
        }


        ErrorHandler.logDebug("Vote Response", gson.toJson(response))

        val res = rarimoContractManager.checkIsTransactionSuccessful(response.data.id)
        if (!res) {
            throw Exception("Error during checkIsTransactionSuccessful")
        }
    }


    private suspend fun generateVoteProof(
        context: Context,
        profile: Profile,
        eDocument: EDocument,
        smtProofJson: String,
        passportInfoKey: String,
        pollResultJson: ByteArray,
        passportInfo: StateKeeper.PassportInfo,
        identityInfo: StateKeeper.IdentityInfo
    ): ZkProof = withContext(Dispatchers.Default) {

        val eventData = profile.calculateVotingEventData(pollResultJson)

        val eventId = if (selectedPoll.value!!.poll.eventId.toString().isEmpty()) {
            "0"
        } else {
            "0x" + (selectedPoll.value!!.poll.eventId.toString(16))
        }

        val votingData = decodeVotingData(selectedPoll.value!!.poll)


        val queryProofInputs = profile.buildQueryIdentityInputs(
            eDocument.dg1!!.decodeHexString(),
            smtProofJson.toByteArray(Charsets.UTF_8),
            "39457",
            passportInfoKey,
            identityInfo.issueTimestamp.toString(),
            passportInfo.identityReissueCounter.toString(),
            eventId,
            Numeric.toHexString(eventData),
            "0",
            max(
                votingData.identityCreationTimestampUpperBound.toInt(),
                identityInfo.issueTimestamp.toInt()
            ).toString(),
            "0",
            votingData.identityCounterUpperBound.toString(),
            votingData.expirationDateLowerBound.toString(16),
            ZERO_IN_HEX,
            ZERO_IN_HEX,
            votingData.birthDateUpperbound.toString(16),
            "0"
        )
        val assetContext: Context = context.createPackageContext("org.freedomtool.unitedgsh", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(context, assetManager)

        ErrorHandler.logDebug("Inputs", queryProofInputs.decodeToString())


        val queryProof = zkp.generateZKP(
            "circuit_query_zkey.zkey",
            R.raw.query_identity_dat,
            queryProofInputs,
            ZkpUtil::queryIdentity
        )

        Log.i("ZkeyProof", GsonBuilder().setPrettyPrinting().create().toJson(queryProof))

        queryProof
    }

    private suspend fun sendVote(
        callData: String, destination: String, proposalId: Long
    ): VoteResponse {
        val voteRequest = VoteRequest(
            data = VoteRequestData(
                type = "vote", attributes = VoteRequestAttributes(
                    txData = callData, destination = destination, proposalId = proposalId
                )
            )
        )

        return votingApiManager.vote(voteRequest)
    }

    private fun decodeVotingData(poll: Poll): VotingData {
        val rawVotingData = identityManager.getProfiler()
            .decodeABIProposalRules(poll.votingData[0])

        val votingData = Gson().fromJson(rawVotingData.decodeToString(), VotingData::class.java)

        return votingData
    }


//    suspend fun checkIsVoted(): Boolean = withContext(Dispatchers.IO) {
//        val poseidonSMTContract =
//            votingContractManager.getPoseidonSMT(selectedPoll.value!!.proposalSMT)
//        val nullifier = identityManager.getNullifier(selectedPoll.value!!.eventId.toByteArray())
//
//        val proof = poseidonSMTContract.getProof(nullifier).send()
//
//        proof.existence
//    }

}