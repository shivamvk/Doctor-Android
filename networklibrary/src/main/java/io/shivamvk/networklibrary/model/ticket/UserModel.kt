package io.shivamvk.networklibrary.model.ticket

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class UserModel(
    var userType: String = "",
    var _id: String = "",
    var full_name: String = ""
): BaseModel, Serializable
