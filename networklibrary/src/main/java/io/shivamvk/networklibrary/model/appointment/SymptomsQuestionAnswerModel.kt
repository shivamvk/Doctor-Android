package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class SymptomsQuestionAnswerModel(
    var question: String? = null,
    var answers: ArrayList<String> = ArrayList<String>()
): BaseModel, Serializable

