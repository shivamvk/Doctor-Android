package io.shivamvk.networklibrary.model.callasessment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable


data class CallAsessmentModel(
    var ros: MutableList<CARosModel>? = emptyList<CARosModel>().toMutableList(),
    var examination: MutableList<CARosModel>? = emptyList<CARosModel>().toMutableList(),
    var assessment: String? = ""
): BaseModel, Serializable

data class CARosModel(
    var question: String? = "",
    var answers: MutableList<CARosAnswerModel>? = emptyList<CARosAnswerModel>().toMutableList()
): BaseModel, Serializable

data class CARosAnswerModel(
    var option: String? = "",
    var value: Boolean? = false
): BaseModel, Serializable
