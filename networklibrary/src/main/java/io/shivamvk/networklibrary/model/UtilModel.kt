package io.shivamvk.networklibrary.model

import com.google.gson.JsonArray
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

data class UtilModelArray(
    var status: Long = 200,
    var message: String = "",
    var data: JsonArray = JsonArray(),
    var errors: Boolean = false
): BaseModel, Serializable

data class UtilModelNullable(
    var status: Long = 200,
    var message: String = "",
    var errors: Boolean = false
): BaseModel, Serializable