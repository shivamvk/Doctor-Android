package io.shivamvk.networklibrary.model.auth

import io.shivamvk.networklibrary.model.user.UserModel
import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class AuthModel(
    val token: String? = null,
    val user: UserModel? = null
): BaseModel, Serializable
