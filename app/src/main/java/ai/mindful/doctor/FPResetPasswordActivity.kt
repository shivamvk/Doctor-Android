package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityFPResetPasswordBinding
import ai.mindful.doctor.di.DoctorApplication
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_f_p_reset_password.*
import kotlinx.android.synthetic.main.pill_layout.view.*
import javax.inject.Inject

class FPResetPasswordActivity : AppCompatActivity(), ApiManagerListener {

    lateinit var binding: ActivityFPResetPasswordBinding
    @Inject lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_f_p_reset_password)
        supportActionBar?.hide()
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.submit.setOnClickListener {
            if (et_password_1.editText?.text.toString().isEmpty() || et_password_2.editText?.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
            } else if (et_password_1.editText?.text.toString() != et_password_2.editText?.text.toString()){
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            } else {
                binding.submit.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                ApiManager(
                    ApiRoutes.fp_resetPassword,
                    apiService,
                    ForgotPasswordResponse(),
                    this,
                    null
                ).doPOSTAPICall(
                    JsonObject().apply {
                        addProperty("newPassword", et_password_1.editText?.text.toString())
                        addProperty("token", intent.getStringExtra("otp"))
                    }
                )
            }
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is ForgotPasswordResponse){
            binding.submit.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            val model = Gson().fromJson(response, ForgotPasswordResponse::class.java)
            if (model.errors){
                Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
                finishAffinity()
                startActivity(Intent(
                    this, LoginActivity::class.java
                ))
            }
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        binding.submit.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE
    }
}