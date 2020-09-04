package io.shivamvk.networklibrary.api

object ApiRoutes {
    fun call(id: String): String = "call/id/${id}"
    val getAppointments: String = "appointment"
    val getHomeBanners: String = "banner"
    var login: String = "auth/login"
    var signup: String = "auth/register/doctor"
    var registerFcmToken = "firebase/notification/add"
}