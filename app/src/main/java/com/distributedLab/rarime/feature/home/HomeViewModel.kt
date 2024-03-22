package com.distributedLab.rarime.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val templateRepository: TemplateRepository,
): ViewModel() {

    val templateData: Result<List<String>>
        get()  {
            val data = templateRepository.templateData
            return if (data != null) {
                Result.success(data)
            }else {
                Result.failure(IllegalAccessError("No template data"))
            }
        }
}