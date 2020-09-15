package io.shivamvk.networklibrary.api

object ApiRoutes {
    fun postCommentToTicket(id: String): String = "ticket/comment/${id}"
    fun call(id: String): String = "call/id/${id}"
    val templateRosTypes: String = "template/ros/type"
    val templateExamTypes: String = "template/examination/type"
    val ticket: String = "ticket"
    val getAppointments: String = "appointment"
    val getHomeBanners: String = "banner"
    var login: String = "auth/login"
    var signup: String = "auth/register/doctor"
    var registerFcmToken = "firebase/notification/add"
}