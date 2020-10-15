package io.shivamvk.networklibrary.model.user

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class UserModel(
    var _id: String? = null,
    val active: Boolean? = false,
    val userType: String? = null,
    val full_name: String? = null,
    val profile_picture: String = "",
    val email: String? = null,
    val verified: Boolean = false,
    val dob: String = "",
    val education: String = "",
    val college: String = "",
    val gender: String = "",
    val expertise: String = ""
): BaseModel, Serializable
