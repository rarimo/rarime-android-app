package com.rarilabs.rarime.api.points.models

import com.google.gson.annotations.SerializedName

data class MaintenanceResponse(
    @SerializedName("data") val data: MaintenanceData
)

data class MaintenanceData(
    @SerializedName("type") val type: String,
    @SerializedName("attributes") val attributes: MaintenanceAttributes
)

data class MaintenanceAttributes(
    @SerializedName("maintenance") val maintenance: Boolean
)