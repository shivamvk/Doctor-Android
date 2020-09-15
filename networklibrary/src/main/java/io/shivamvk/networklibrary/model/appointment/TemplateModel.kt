package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class TemplateModel(
    val name: String = "",
    val options: ArrayList<OptionsModel> = ArrayList()
): BaseModel, Serializable
