package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityLoginBinding
import ai.mindful.doctor.ui.viewmodel.LoginActivityViewModel
import ai.mindful.doctor.utils.ViewModelFactory
import ai.mindful.doctor.di.DoctorApplication
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.util.Util
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.get
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.auth.AuthResponse
import io.shivamvk.networklibrary.models.BaseModel
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.set
import io.shivamvk.networklibrary.sharedprefs.SharedPrefKeys
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), ApiManagerListener {

    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var prefs: SharedPreferences
    lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        init()
    }

    fun init() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(apiService, this, prefs)
        ).get(LoginActivityViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setUpObservers()
        setUpTextWatchers()
    }

    fun setUpObservers() {
        viewModel.proceedWithLogin.observe(this, Observer {
            if (it) {
                login()
            }
        })
        viewModel.proceedWithSignup.observe(this, Observer {
            if (it) {
                signup()
            }
        })
        viewModel.loading.observe(this, Observer {
            if (it) {
                disableEditTexts()
            } else {
                enableEditTexts()
            }
        })
    }

    fun disableEditTexts() {
        et_email.editText?.isEnabled = false
        et_name.editText?.isEnabled = false
        et_password.editText?.isEnabled = false
    }

    fun enableEditTexts() {
        et_name.editText?.isEnabled = true
        et_password.editText?.isEnabled = true
        et_email.editText?.isEnabled = true
    }

    fun login() {
        if (validateLoginInputs()) {
            viewModel.loading.postValue(true)
            var jsonObject = JsonObject()
            jsonObject.addProperty("email", et_email.editText?.text.toString())
            jsonObject.addProperty("password", et_password.editText?.text.toString())
            ApiManager(
                ApiRoutes.login,
                apiService,
                AuthResponse(),
                this,
                null
            ).doPOSTAPICall(jsonObject)
        }
    }

    fun validateLoginInputs(): Boolean {
        if (et_email.editText?.text.toString().isEmpty()) {
            et_email.errorIconDrawable = resources.getDrawable(R.drawable.ic_error)
            et_email.error = "Email is required"
            return false
        }
        if (et_password.editText?.text.toString().isEmpty()) {
            et_password.errorIconDrawable = resources.getDrawable(R.drawable.ic_error)
            et_password.error = "Password is required"
            return false
        }
        return true
    }

    fun signup() {
        if (validateInputsForSignup()) {
            viewModel.loading.postValue(true)
            var jsonObject = JsonObject()
            jsonObject.addProperty("full_name", et_name.editText?.text.toString())
            jsonObject.addProperty("email", et_email.editText?.text.toString())
            jsonObject.addProperty("password", et_password.editText?.textAlignment.toString())
            ApiManager(
                ApiRoutes.signup,
                apiService,
                AuthResponse(),
                this,
                null
            ).doPOSTAPICall(jsonObject)
        }
    }

    fun validateInputsForSignup(): Boolean {
        if (et_email.editText?.text.toString().isEmpty()) {
            et_email.errorIconDrawable = resources.getDrawable(R.drawable.ic_error)
            et_email.error = "Email is required"
            return false
        }
        if (et_password.editText?.text.toString().isEmpty()) {
            et_password.errorIconDrawable = resources.getDrawable(R.drawable.ic_error)
            et_password.error = "Password is required"
            return false
        }
        if (et_name.editText?.text.toString().isEmpty()) {
            et_name.errorIconDrawable = resources.getDrawable(R.drawable.ic_error)
            et_name.error = "Name is required"
        }
        if (!tv_tnc_pp.isChecked) {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT)
                .show()
        }
        return true
    }

    fun setUpTextWatchers() {
        et_email.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty())
                    et_email.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        et_password.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty())
                    et_password.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        et_name.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty())
                    et_name.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun registerFcm() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var jsonObject = JsonObject()
                    jsonObject.addProperty("registrationToken", it.result?.token)
                    ApiManager(
                        ApiRoutes.registerFcmToken,
                        apiService,
                        UtilModel(),
                        this,
                        null
                    ).doPOSTAPICall(jsonObject)
                } else {
                    onFailure(UtilModel(), Throwable(it.exception))
                }
            }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is AuthResponse) {
            var data = Gson().fromJson(response, AuthResponse::class.java).data
            prefs[SharedPrefKeys.USER_TOKEN.toString()] = data?.token
            prefs[SharedPrefKeys.USER_ID.toString()] = data?.user?._id
            prefs[SharedPrefKeys.USER_NAME.toString()] = data?.user?.full_name
            prefs[SharedPrefKeys.USER_EMAIL.toString()] = data?.user?.email
            prefs[SharedPrefKeys.USER_PICTURE.toString()] = data?.user?.profile_picture
            finish()
            (application as DoctorApplication).initDagger()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        viewModel.loading.postValue(false)
        if (dataModel is AuthResponse) {
            Toast.makeText(this, "Auth: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}