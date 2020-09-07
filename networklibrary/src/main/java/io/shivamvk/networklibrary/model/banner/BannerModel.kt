package io.shivamvk.networklibrary.model.banner

data class BannerModel(
    var _id: String = "",
    var audience: String = "",
    var image: String = "",
    var weblink: Boolean = false,
    var url: String = ""
)
