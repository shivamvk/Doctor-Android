package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityTakeNoteBinding
import ai.mindful.doctor.di.DoctorApplication
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.callasessment.CallAsessmentModel
import io.shivamvk.networklibrary.models.BaseModel
import org.json.JSONArray
import javax.inject.Inject

class TakeNoteActivity : AppCompatActivity(), ApiManagerListener {

    lateinit var binding: ActivityTakeNoteBinding
    lateinit var callAssessment: CallAsessmentModel
    lateinit var appointmentId: String

    @Inject
    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_take_note)
        callAssessment = intent.getSerializableExtra("callAssessment") as CallAsessmentModel
        appointmentId = intent.getStringExtra("appointmentId")
        supportActionBar?.hide()
        Log.i("callAssessment ###", callAssessment.toString())
        binding.submit.setOnClickListener {
            if (binding.etAssessment.editText?.text.toString().isEmpty()) {
                binding.etAssessment.error = "Please write a note of your booking"
            } else {
                binding.etAssessment.error = null
                binding.submit.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                var rosjson = JsonArray()
                var examjson = JsonArray()
                callAssessment.ros?.forEach {
                    var json = JsonObject()
                    json.addProperty("question", it.question)
                    var jsonarray = JsonArray()
                    it.answers?.forEach {
                        var j = JsonObject().apply {
                            addProperty("option", it.option)
                            addProperty("value", it.value)
                        }
                        jsonarray.add(j)
                    }
                    if (it.answers?.isNotEmpty()!!) {
                        json.add("answers", jsonarray)
                        rosjson.add(json)
                    }
                }
                callAssessment.examination?.forEach {
                    var json = JsonObject()
                    json.addProperty("question", it.question)
                    var jsonarray = JsonArray()
                    it.answers?.forEach {
                        var j = JsonObject().apply {
                            addProperty("option", it.option)
                            addProperty("value", it.value)
                        }
                        jsonarray.add(j)
                    }
                    if (it.answers?.isNotEmpty()!!) {
                        json.add("answers", jsonarray)
                        examjson.add(json)
                    }
                }
                Log.i("ros", rosjson.toString())
                Log.i("rose", examjson.toString())
                ApiManager(
                    ApiRoutes.callAssessment(appointmentId),
                    apiService,
                    UtilModel(),
                    this,
                    null
                ).doPUTAPICall(
                    JsonObject().apply {
                        addProperty(
                            "assesmentNote",
                            binding.etAssessment.editText?.text.toString()
                        )
                        add("ros", rosjson)
                        add("examination", examjson)
                    }
                )
            }
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        var model = Gson().fromJson(response, UtilModel::class.java)
        if (model.errors) {
            Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            binding.submit.visibility = View.VISIBLE
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        binding.progressBar.visibility = View.GONE
        binding.submit.visibility = View.VISIBLE
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}