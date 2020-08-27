package ai.mindful.doctor.ui.viewmodel

import android.app.Activity
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.shivamvk.networklibrary.api.ApiService

class LoginActivityViewModel(
    val apiService: ApiService,
    val activity: Activity,
    val prefs: SharedPreferences
) : ViewModel() {

    var isLoginView = MutableLiveData<Boolean>()
    var headingText = MutableLiveData<String>()
    var subheadingText = MutableLiveData<String>()
    var proceedWithLogin = MutableLiveData<Boolean>()
    var proceedWithSignup = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()

    init {
        loading.value = false
        proceedWithLogin.value = false
        proceedWithSignup.value = false
        isLoginView.value = true
        headingText.value = "Welcome back"
        subheadingText.value = "Don't have an account yet? Click here to register"
        loadLoginView()
    }

    fun loadLoginView(){
        isLoginView.postValue(true)
        headingText.postValue("Welcome back")
        subheadingText.postValue("Don't have an account yet? Click here to register")
    }

    fun loadSignupView(){
        isLoginView.postValue(false)
        headingText.postValue("Welcome to Doctor")
        subheadingText.postValue("Already have an account? Click here to login")
    }

    fun toggleView(){
        if (isLoginView.value!!){
            loadSignupView()
        } else {
            loadLoginView()
        }
    }

    fun handleContinueClick(){
        if (isLoginView.value!!){
            proceedWithLogin.postValue(true)
        } else {
            proceedWithSignup.postValue(true)
        }
    }

}