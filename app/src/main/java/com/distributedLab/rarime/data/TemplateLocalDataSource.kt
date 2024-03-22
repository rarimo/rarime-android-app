package com.distributedLab.rarime.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateLocalDataSource @Inject constructor() {
    val templateData = listOf("template1", "template2", "template3", "template4")
}
