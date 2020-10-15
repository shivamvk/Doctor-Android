package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class SymptomModel(
    var symptomId: String? = "",
    var symptom: String? = null,
    var questions: ArrayList<SymptomsQuestionAnswerModel> = ArrayList<SymptomsQuestionAnswerModel>()
): BaseModel, Serializable

data class SimpleSymptomModel(
    val _id: String? = null,
    val name: String? = null,
    val icon: String? = null
): BaseModel, Serializable

data class SimpleSymptomResponse(
    var status: Long? = 200,
    var message: String? = "",
    var data: List<SimpleSymptomModel>? = ArrayList<SimpleSymptomModel>(),
    var errors: Boolean? = false
): BaseModel, Serializable