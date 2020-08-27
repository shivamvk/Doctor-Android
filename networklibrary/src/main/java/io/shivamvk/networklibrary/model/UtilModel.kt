package io.shivamvk.networklibrary.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class UtilModel(
    var status: Long = 200,
    var message: String = "",
    var data: JsonObject = JsonObject(),
    var errors: Boolean = false
): BaseModel, Serializable
