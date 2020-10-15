package io.shivamvk.networklibrary.model.forgotpassword

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class ForgotPasswordResponse(
    var status: Long = 200,
    var errors: Boolean = false,
    var message: String? = "",
    var data: Boolean? = false
): BaseModel, Serializable