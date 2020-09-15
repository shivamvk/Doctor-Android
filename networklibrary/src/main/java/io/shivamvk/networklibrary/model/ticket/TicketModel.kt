package io.shivamvk.networklibrary.model.ticket

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class TicketModel(
    var _id: String = "",
    var createdAt: String = "",
    var issue: String = "",
    var status: String = "",
    var ticket_id: String = "",
    var user: UserModel? = null,
    var comments: List<CommentModel> = ArrayList<CommentModel>()
): BaseModel, Serializable
