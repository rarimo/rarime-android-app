package com.rarilabs.rarime.api.voting.models

import com.google.gson.Gson
import identity.Identity
import java.math.BigInteger

enum class PollType {
    STUB_POLL
}

enum class PollCriteriaStatus {
    NEED_VERIFICATION, VERIFIED,
}

data class PollCriteria(
    val title: String,
    val accomplished: Boolean,
)

data class PollVoteOption(
    val id: Int,
    val title: String,
)

data class UserInPoll(
    val poll: Poll,

    val pollCriteriaList: List<PollCriteria>,
    val userVerificationStatus: PollCriteriaStatus,
)


data class Poll(
    val id: Long,
    val title: String,
    val description: String,
    var reward: Long,
    val voteStartDate: Long? = null,
    val voteEndDate: Long? = null,
    val isStarted: Boolean = false,
    val isEnded: Boolean = false,
    val questionList: List<Question>,
    val votingAddresses: List<String>,
    val votingData: List<ByteArray>,
    val eventId: BigInteger,
    val proposalSMT: String,
    val proposalResults: List<List<Long>>,
    val proposalStatus: ProposalStatus
) {
    fun getVotingData(): VotingData {
        val rawVotingdata = votingData[0]
        val ruleJson = Identity.decodeABIProposalRules(rawVotingdata)

        return Gson().fromJson(ruleJson.decodeToString(), VotingData::class.java)
    }
}

data class Question(
    val id: Long,
    val title: String,
    val variants: List<String>,
    val IsSkippable: Boolean,
    val isMultiAnswer: Boolean = false,
)

data class VotingData(
    val citizenshipWhitelist: List<BigInteger>,
    val identityCreationTimestampUpperBound: BigInteger,
    val identityCounterUpperBound: BigInteger,
    val birthDateUpperbound: BigInteger,
    val expirationDateLowerBound: BigInteger
)

val MOCKED_POLL_ITEM = Poll(

    id = 1,
    title = "Mocked Poll",
    description = "This is a mocked poll for testing purposes.",

    reward = 50,

    voteStartDate = 1723125259,
    voteEndDate = 1630458000000,

    questionList = listOf(
        Question(
            id = 1,
            title = "question 1",
            IsSkippable = false,
            variants = listOf("var 1", "var 2", "var 3")
        ), Question(
            id = 2, title = "question 2", IsSkippable = false, variants = listOf("1", "2", "3")
        ), Question(
            id = 3, title = "question 3", IsSkippable = false, variants = listOf("1", "2", "3")
        ), Question(
            id = 4, title = "question 4", IsSkippable = false, variants = listOf("1", "2", "3")
        )
    ),
    eventId = BigInteger.ONE,
    votingAddresses = listOf(),
    votingData = listOf(),
    proposalStatus = ProposalStatus.Started,
    proposalSMT = "",
    proposalResults = listOf(listOf(5, 2, 4), listOf(1, 2, 5), listOf(8, 2, 5))
)

val MOCKED_USER_IN_POLL_ITEM = UserInPoll(
    poll = MOCKED_POLL_ITEM,
    pollCriteriaList = listOf(
        PollCriteria(
            title = "Citizen of Georgia", accomplished = true
        ),
        PollCriteria(
            title = "Over 18+", accomplished = true
        ),
    ),
    userVerificationStatus = PollCriteriaStatus.VERIFIED,
)