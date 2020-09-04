package io.shivamvk.networklibrary.model.patient

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class HistoryModelData(
    var medicalProblems: ArrayList<String>? = null,
    val surgeries: ArrayList<String>? = null,
    val allergies: ArrayList<String>? = null,
    val currentMedication: ArrayList<String>? = null,
    val smoking: Boolean? = false
): BaseModel, Serializable
