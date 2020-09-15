package io.shivamvk.networklibrary.model.ticket

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class TicketsResponse(
    var status: Long = 200,
    var message: String = "",
    var data: List<TicketModel>? = null,
    var errors: Boolean = false
): BaseModel, Serializable