package io.shivamvk.networklibrary.model.ticket

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class TicketResponse(
    var stauts: Long = 200,
    var message: String = "",
    var errors: Boolean = false,
    var data: TicketModel? = null
): BaseModel, Serializable