package com.distributedLab.rarime.data

import javax.inject.Inject
class TemplateRepository @Inject constructor (
    private val templateLocalDataSource: TemplateLocalDataSource
) {
    val templateData: List<String>? = templateLocalDataSource.templateData
}