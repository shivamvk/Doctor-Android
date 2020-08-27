package io.shivamvk.networklibrary.api

import io.shivamvk.networklibrary.models.BaseModel

interface ApiManagerListener {
    fun onSuccess(dataModel: BaseModel?, response: String)
    fun onFailure(dataModel: BaseModel?, e: Throwable) {}
}