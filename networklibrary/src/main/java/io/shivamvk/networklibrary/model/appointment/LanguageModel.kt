package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class LanguageModel(
    val _id: String = "",
    val name: String = ""
): BaseModel, Serializable

data class LanguageResponse(
    var status: Long = 200,
    var message: String = "",
    var data: List<LanguageModel>? = null,
    var errors: Boolean = false
): BaseModel, Serializable
