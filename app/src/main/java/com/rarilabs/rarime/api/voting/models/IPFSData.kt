package com.rarilabs.rarime.api.voting.models

data class OptionVariant(
    val title: String,
    val description: String?,
    val variants: List<String>
)

data class IPFSResponseData(
    val title: String,
    val description: String?,
    val acceptedOptions: List<OptionVariant>,
    val imageCid: String?,
    val rankingBased: Boolean
)