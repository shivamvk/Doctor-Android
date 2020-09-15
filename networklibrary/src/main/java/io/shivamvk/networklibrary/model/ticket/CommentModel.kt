package io.shivamvk.networklibrary.model.ticket

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class CommentModel(
    var at: String = "",
    var comment: String = "",
    var by: String = "",
    var sent: Boolean = true
): BaseModel, Serializable
