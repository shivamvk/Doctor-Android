package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class OptionsModel(
    val name: String = ""
): BaseModel, Serializable
