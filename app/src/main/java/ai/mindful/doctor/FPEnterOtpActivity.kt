package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityFPEnterOtpBinding
import ai.mindful.doctor.di.DoctorApplication
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

class FPEnterOtpActivity : AppCompatActivity(), ApiManagerListener {

    lateinit var binding: ActivityFPEnterOtpBinding
    @Inject lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_f_p_enter_otp)
        supportActionBar?.hide()
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.etOtp.editText?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etOtp.error = null
            }
        })
        binding.submit.setOnClickListener {
            if (binding.etOtp.editText?.text.toString().isEmpty()){
                binding.etOtp.error = "Please enter a valid OTP"
            } else {
                binding.submit.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                ApiManager(
                    ApiRoutes.fp_verifyotp,
                    apiService,
                    ForgotPasswordResponse(),
                    this,
                    null
                ).doPOSTAPICall(JsonObject().apply {
                    addProperty("token", binding.etOtp.editText?.text.toString())
                })
            }
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is ForgotPasswordResponse){
            binding.submit.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            var model = Gson().fromJson(response, ForgotPasswordResponse::class.java)
            if (model.data!!){
                finish()
                startActivity(Intent(
                    this, FPResetPasswordActivity::class.java
                ).putExtra("otp", binding.etOtp.editText?.text.toString()))
            } else {
                Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        binding.submit.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}