package io.shivamvk.networklibrary.model.banner

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class BannerResponse(
    var status: Long = 200,
    var message: String = "",
    var data: List<BannerModel>? = null,
    var errors: Boolean = false
): BaseModel, Serializable