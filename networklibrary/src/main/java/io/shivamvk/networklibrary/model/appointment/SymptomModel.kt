package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class SymptomModel(
    var symptom: String? = null,
    var questions: ArrayList<SymptomsQuestionAnswerModel> = ArrayList<SymptomsQuestionAnswerModel>()
): BaseModel, Serializable
