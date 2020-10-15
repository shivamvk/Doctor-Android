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
    var cancellationReason: String? = ""
): BaseModel, Serializable