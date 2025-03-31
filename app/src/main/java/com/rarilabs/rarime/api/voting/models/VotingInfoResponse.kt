package com.rarilabs.rarime.api.voting.models

import com.google.gson.annotations.SerializedName

data class VotingInfoResponse(
    @SerializedName("data") val data: VotingInfoData
)

data class VotingInfoData(
    @SerializedName("type") val type: String,
    @SerializedName("attributes") val attributes: VotingInfoAttributes
)

data class VotingInfoAttributes(
    @SerializedName("contract") val contract: Contract,
    @SerializedName("author") val author: String,
    @SerializedName("metadata") val metadata: Metadata
)

data class Contract(
    @SerializedName("proposalSMT") val proposalSMT: String,
    @SerializedName("status") val status: Int,
    @SerializedName("config") val config: Config,
    @SerializedName("voting_results") val votingResults: List<List<Int>>
)

data class Config(
    @SerializedName("start_timestamp") val startTimestamp: Long,
    @SerializedName("end_timestamp") val endTimestamp: Long,
    @SerializedName("proposal_id") val proposalId: Long,
    @SerializedName("description") val description: String,
    @SerializedName("multichoice") val multichoice: Int,
    @SerializedName("voting_whitelist") val votingWhitelist: List<String>,
    @SerializedName("voting_whitelist_data") val votingWhitelistData: List<String>,
    @SerializedName("parsed_voting_whitelist_data") val parsedVotingWhitelistData: List<ParsedVotingWhitelistData>
)

data class ParsedVotingWhitelistData(
    @SerializedName("birth_date_upper_bound") val birthDateUpperBound: String,
    @SerializedName("expiration_date_lower_bound") val expirationDateLowerBound: String,
    @SerializedName("identity_counter_upper_bound") val identityCounterUpperBound: String,
    @SerializedName("identity_creation_timestamp_upper_bound") val identityCreationTimestampUpperBound: String,
    @SerializedName("citizenship_whitelist") val citizenshipWhitelist: List<String>,
    @SerializedName("min_age") val minAge: Int
)

data class Metadata(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("acceptedOptions") val acceptedOptions: List<AcceptedOption>
)

data class AcceptedOption(
    @SerializedName("title") val title: String,
    @SerializedName("variants") val variants: List<String>
)