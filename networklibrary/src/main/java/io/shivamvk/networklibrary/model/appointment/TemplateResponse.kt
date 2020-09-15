package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class TemplateRosResponse(
    val status: Long = 200,
    val message: String = "",
    val errors: Boolean = false,
    val data: ArrayList<TemplateModel> = ArrayList()
): BaseModel, Serializable

data class TemplateExamResponse(
    val status: Long = 200,
    val message: String = "",
    val errors: Boolean = false,
    val data: ArrayList<TemplateModel> = ArrayList()
): BaseModel, Serializable