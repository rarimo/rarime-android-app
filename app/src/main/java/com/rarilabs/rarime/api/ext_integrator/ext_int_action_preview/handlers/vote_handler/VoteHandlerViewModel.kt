package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.vote_handler

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponseAttributes
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponseData
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.votes.QuestionAnswerVariant
import com.rarilabs.rarime.modules.votes.VoteData
import com.rarilabs.rarime.modules.votes.VoteQuestion
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class VotingWhitelistData(
    val citizenshipWhitelist: Array<Number>,
    val identityCreationTimestampUpperBound: Number,
    val identityCounterUpperBound: Number,
    val birthDateUpperbound: Number,
    val expirationDateLowerBound: Number
)

data class ProposalInfoDetailsConfigJSON(
    val start_timestamp: Long,
    val duration: Long,
    val multichoice: String,
    val accepted_options: List<String>,
    val description: String,
    val voting_whitelist: List<String>,
    val voting_whitelist_data: VotingWhitelistData
)

data class ProposalInfoDetailsJSON(
    val proposal_smt: String,
    val status: Int,
    val config: ProposalInfoDetailsConfigJSON,
    val voting_results: List<List<String>>
)

data class ProposalInfoJSON(
    val proposal_info: ProposalInfoDetailsJSON,
    val proposal_event_id: String
)

data class ProposalMetadataOption(
    val title: String,
    val variants: List<String>
)

data class ProposalMetadata(
    val title: String,
    val description: String,
    val acceptedOptions: List<ProposalMetadataOption>
)

data class VoteSelections(
    val questionIndex: Number,
    val answerIndex: Number,
)

@HiltViewModel
class VoteHandlerViewModel @Inject constructor(
    private val extIntegratorApiManager: ExtIntegratorApiManager,
    private val passportManager: PassportManager,
    private val contractManager: RarimoContractManager,
    private val identityManager: IdentityManager,
) : ViewModel() {
    private var _proposalData = MutableStateFlow<ProposalInfoJSON?>(null)
    val proposalData: StateFlow<ProposalInfoJSON?>
        get() = _proposalData.asStateFlow()

    private var _proposalMetadata = MutableStateFlow<ProposalMetadata?>(null)
    val proposalMetadata: StateFlow<ProposalMetadata?>
        get() = _proposalMetadata.asStateFlow()

    private val _voteData = MutableStateFlow<VoteData?>(null)
    val voteData: StateFlow<VoteData?> = _voteData.asStateFlow()

    suspend fun loadDetails(proposalId: String) {
        extIntegratorApiManager.loadPassportInfo()

        val proposal_infoBytes = Identity.getProposalInfo(
            "0xf4B99A3891D0a64A0bc3bB8642242E6A01e104e2",
            "https://rpc.qtestnet.org",
            proposalId
        )

        _proposalData.value = Gson().fromJson(proposal_infoBytes.decodeToString(), ProposalInfoJSON::class.java)

        println(
            _proposalData.value.toString()
        )

        val metadataStr = extIntegratorApiManager.queryIpfsData("https://ipfs.rarimo.com/ipfs/" + _proposalData.value!!.proposal_info.config.description)

        println(metadataStr)

        _proposalMetadata.value = Gson().fromJson(
            metadataStr,
            ProposalMetadata::class.java
        )

        _voteData.value = VoteData(
            title = _proposalMetadata.value!!.title,
            description = _proposalMetadata.value!!.description,
            durationMillis = _proposalData.value!!.proposal_info.config.duration,
            participantsCount = 0, // TODO: implement me
            questions = _proposalMetadata.value!!.acceptedOptions.mapIndexed { index, question ->
                VoteQuestion(
                    id = index.toString(),
                    title = question.title,
                    variants = question.variants.mapIndexed { i, variant ->
                        QuestionAnswerVariant(
                            id = i.toString(),
                            title = variant,
                            votedCount = 0.0 // TODO: implement me
                        )
                    },
                )
            },
            endDate = (_proposalData.value!!.proposal_info.config.start_timestamp + _proposalData.value!!.proposal_info.config.duration) * 1000
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun vote(context: Context, voteOptions: Map<String, String>) {
        val voteSelections = voteOptions.map {
            VoteSelections(
                questionIndex = it.key.toInt(),
                answerIndex = it.value.toInt()
            )
        }

        val queryProofParametersRequest = QueryProofGenResponse(
            data = QueryProofGenResponseData(
                id = "1",
                type = "get_proof_params",
                attributes = QueryProofGenResponseAttributes(
                    birth_date_lower_bound = "0x303030303030",
                    birth_date_upper_bound = _proposalData.value!!.proposal_info.config.voting_whitelist_data.birthDateUpperbound.toString(),
                    citizenship_mask = _proposalData.value!!.proposal_info.config.voting_whitelist_data.citizenshipWhitelist[0].toString(),
                    event_data = "0x" + Identity.calculateVotingEventData(Gson().toJson(voteSelections).toByteArray()).toHexString(),
                    event_id = _proposalData.value!!.proposal_event_id,
                    expiration_date_lower_bound = _proposalData.value!!.proposal_info.config.voting_whitelist_data.expirationDateLowerBound.toString(),
                    expiration_date_upper_bound = "0x303030303030",
                    identity_counter = 0, // leave it as 0, cuz it will be appended from passport info
                    identity_counter_lower_bound = 0,
                    identity_counter_upper_bound = _proposalData.value!!.proposal_info.config.voting_whitelist_data.identityCounterUpperBound.toInt(),
                    selector = "6657",
                    timestamp_lower_bound = "0",
                    timestamp_upper_bound = _proposalData.value!!.proposal_info.config.voting_whitelist_data.identityCreationTimestampUpperBound.toString(),
                    callback_url = "",
                )
            )
        )

        try {
            val queryProof = extIntegratorApiManager.generateQueryProof(context, queryProofParametersRequest)

            println(queryProof)
        } catch (e: Exception) {
            ErrorHandler.logError("VoteHandlerViewModel", "vote", e)
        }
    }
}