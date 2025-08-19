package com.rarilabs.rarime.api.voting.models

import java.math.BigInteger

//enum class PollType {
//    STUB_POLL
//}

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
    val proposalStatus: ProposalStatus,
    val imageUrl: String?
)

data class Question(
    val id: Long,
    val title: String,
    val variants: List<String>,
    val IsSkippable: Boolean,
    val isMultiAnswer: Boolean = false,
)

data class VotingData(
    val selector: BigInteger,
    val citizenshipWhitelist: List<BigInteger>,
    val identityCreationTimestampUpperBound: BigInteger,
    val identityCounterUpperBound: BigInteger,
    val sex: BigInteger,
    val birthDateLowerbound: BigInteger,
    val birthDateUpperbound: BigInteger,
    val expirationDateLowerBound: BigInteger
)

val MOCKED_POLL_ITEM = Poll(

    id = 1,
    title = "Mocked Poll",
    description = "This is a mocked poll for testing purposes.",

    reward = 0,

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
        ),
        Question(
            id = 5, title = "question 4", IsSkippable = false, variants = listOf("1", "2", "3")
        )
    ),
    eventId = BigInteger.ONE,
    votingAddresses = listOf(),
    votingData = listOf(),
    proposalStatus = ProposalStatus.Started,
    proposalSMT = "",
    imageUrl = null,
    proposalResults = listOf(
        listOf(5, 2, 4),
        listOf(1, 2, 5),
        listOf(8, 2, 5),
        listOf(8, 2, 5),
        listOf(8, 2, 5)
    )
)

val MOCKED_RANKING_BASED_VOTE_ITEM = Poll(

    id = 1,
    title = "Mocked Ranking Based Poll",
    description = "This is a mocked ranking based poll for testing purposes.",

    reward = 0,

    voteStartDate = 1723125259,
    voteEndDate = 1630458000000,

    questionList = listOf(
        Question(
            id = 1,
            title = "What drink is tastier?",
            IsSkippable = false,
            variants = listOf("Cola", "Pepsi", "Sprite")
        ), Question(
            id = 2, title = "What drink is tastier",
            IsSkippable = false,
            variants = listOf("Cola", "Pepsi", "Sprite")
        ), Question(
            id = 3, title = "What drink is tastier",
            IsSkippable = false,
            variants = listOf("Cola", "Pepsi", "Sprite")
        )
    ),
    eventId = BigInteger.ONE,
    votingAddresses = listOf(),
    votingData = listOf(),
    proposalStatus = ProposalStatus.Started,
    proposalSMT = "",
    imageUrl = null,
    proposalResults = listOf(
        listOf(5, 2, 4),
        listOf(1, 2, 5),
        listOf(8, 2, 5)
    )
)