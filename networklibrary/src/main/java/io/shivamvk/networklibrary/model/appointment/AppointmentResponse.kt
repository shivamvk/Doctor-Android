package io.shivamvk.networklibrary.model.appointment
import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class AppointmentResponse(
    var status: Long = 200,
    var message: String = "",
    var data: List<AppointmentModel>? = null,
    var errors: Boolean? = false
): BaseModel, Serializable