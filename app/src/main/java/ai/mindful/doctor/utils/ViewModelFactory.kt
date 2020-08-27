package ai.mindful.doctor.utils

import ai.mindful.doctor.ui.viewmodel.EditProfileActivityViewModel
import ai.mindful.doctor.ui.viewmodel.LoginActivityViewModel
import android.app.Activity
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.shivamvk.networklibrary.api.ApiService

class ViewModelFactory(
    private val apiService: ApiService, private val activity: Activity, private val prefs: SharedPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginActivityViewModel::class.java) -> {
                LoginActivityViewModel(apiService, activity, prefs) as T
            }
            modelClass.isAssignableFrom(EditProfileActivityViewModel::class.java) -> {
                EditProfileActivityViewModel(apiService, activity, prefs) as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }

}