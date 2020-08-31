package io.shivamvk.networklibrary.model.banner

data class BannerResponse(
    var status: Long = 200,
    var message: String = "",
    var data: List<BannerModel>? = null,
    var errors: Boolean = false
)