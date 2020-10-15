package io.shivamvk.networklibrary.model.auth

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class AuthResponse(
    var status: Long = 200,
    var message: String = "",
    var data: AuthModel? = null,
    var errors: Boolean = true
): BaseModel, Serializable