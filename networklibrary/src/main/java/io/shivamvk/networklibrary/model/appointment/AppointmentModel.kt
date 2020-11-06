package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.model.patient.PatientModel
import io.shivamvk.networklibrary.model.user.UserModel
import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class AppointmentModel(
    val _id: String = "",
    val createdAt: String = "",
    val createdBy: UserModel? = null,
    val patient: PatientModel? = null,
    val language: LanguageModel? = null,
    val symptoms: List<SymptomModel>? = null,
    var templateType: String = "",
    var status: String? = "",
    var cancellationReason: String? = "",
    var assesmentNote: String? = "",
    var prescription: String? = "",
    val examination: List<RosExamModel>? = null,
    val ros: List<RosExamModel>? = null
): BaseModel, Serializable

data class RosExamModel(
    val answers: List<RosExamAnswerModel>? = null,
    val question: String? = ""
): BaseModel, Serializable

data class RosExamAnswerModel(
    val _id: String? = "",
    val option: String? = "",
    val value: String? = ""
): BaseModel, Serializable