package io.shivamvk.networklibrary.model.appointment

import io.shivamvk.networklibrary.models.BaseModel
import java.io.Serializable

data class RosExamBookingPutModel(
    var ros: MutableList<RosModel>? = ArrayList<RosModel>(),
    var examination: MutableList<ExamModel>? = ArrayList<ExamModel>()
): BaseModel, Serializable

class ExamModel(
    var type: String = "",
    var options: MutableList<String>? = ArrayList<String>()
): BaseModel, Serializable

data class RosModel(
    var type: String = "",
    var patientAdmits: MutableList<String>? = ArrayList<String>(),
    var patientDenies: MutableList<String>? = ArrayList<String>()
): BaseModel, Serializable
