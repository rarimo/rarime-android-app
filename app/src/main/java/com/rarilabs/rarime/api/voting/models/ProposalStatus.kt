package com.rarilabs.rarime.api.voting.models

enum class ProposalStatus {
    None, Waiting, Started, Ended, DoNotShow
}

fun uint8ToProposalStatus(value: Int): ProposalStatus {
    return when (value.toLong()) {
        0L -> ProposalStatus.None
        1L -> ProposalStatus.Waiting
        2L -> ProposalStatus.Started
        3L -> ProposalStatus.Ended
        4L -> ProposalStatus.DoNotShow
        else -> throw IllegalArgumentException("Unknown ProposalStatus value: $value")
    }
}