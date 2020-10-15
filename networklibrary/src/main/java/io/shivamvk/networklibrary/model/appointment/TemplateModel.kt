package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class TemplateModel(
    val _id: String = "",
    val symptom: String = "",
    val title: String = "",
    val answers: ArrayList<String> = ArrayList()
): BaseModel, Serializable
