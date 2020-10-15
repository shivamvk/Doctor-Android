package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityFPEnterEmailBinding
import ai.mindful.doctor.di.DoctorApplication
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.forgotpassword.ForgotPasswordResponse
import io.shivamvk.networklibrary.models.BaseModel
import javax.inject.Inject

class FPEnterEmailActivity : AppCompatActivity(), ApiManagerListener {

    lateinit var binding: ActivityFPEnterEmailBinding
    @Inject lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_f_p_enter_email)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.etEmail.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etEmail.error = null
            }
        })
        binding.submit.setOnClickListener {
            if (binding.etEmail.editText?.text.toString().isEmpty()){
                binding.etEmail.error = "Please enter a valid email"
            } else {
                binding.progressBar.visibility = View.VISIBLE
                binding.submit.visibility = View.GONE
                ApiManager(
                    ApiRoutes.fp_sendOTpToEmail,
                    apiService,
                    ForgotPasswordResponse(),
                    this,
                    null
                ).doPOSTAPICall(JsonObject().apply {
                    addProperty("email", binding.etEmail.editText?.text.toString())
                })
            }
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is ForgotPasswordResponse){
            Log.i("fp###########", response)
            var model = Gson().fromJson(response, ForgotPasswordResponse::class.java)
            if (!model.errors){
                binding.progressBar.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE
                finish()
                startActivity(Intent(
                    this, FPEnterOtpActivity::class.java
                ))
            } else {
                binding.progressBar.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE
                Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.GONE
        binding.submit.visibility = View.VISIBLE
    }
}