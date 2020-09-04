package io.shivamvk.networklibrary.model.patient

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class PatientModel(
    var _id: String? = null,
    val name: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val relationWithAccountHolder: String? = null,
    val user: String? = null,
    var history: HistoryModelData? = null
): BaseModel, Serializable
