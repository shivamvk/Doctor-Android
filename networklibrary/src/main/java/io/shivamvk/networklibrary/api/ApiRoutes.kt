package io.shivamvk.networklibrary.api

object ApiRoutes {
    fun postCommentToTicket(id: String): String = "ticket/comment/${id}"
    fun call(id: String): String = "call/id/${id}"
    fun questions(id: String, type: String): String = "question/bysymptom/${id}/${type}"
    val upcomingBooking: String = "appointment/upcoming"
    val ticket: String = "ticket"
    val getAppointments: String = "appointment"
    fun cancelAppointment(_id: String): String = "appointment/cancel/${_id}"
    val getHomeBanners: String = "banner"
    var login: String = "auth/login/doctor"
    var signup: String = "auth/register/doctor"
    var registerFcmToken = "firebase/notification/add"
    var toggleAvailable = "user/update/available"
    val languages = "language"
    val symptoms = "symptom"
    val ULupdateProfile = "user/update/me"
    val EMUupdateDocs = "user/update/docs"
    val getProfile = "auth/me"
    val fp_sendOTpToEmail = "auth/forgetpassword"
    val fp_verifyotp = "auth/verifyresettoken"
    val fp_resetPassword = "auth/resetpassword"
}