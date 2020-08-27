package ai.mindful.doctor.ui.viewmodel

import android.app.Activity
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import io.shivamvk.networklibrary.api.ApiService

class EditProfileActivityViewModel(
    val apiService: ApiService,
    val activity: Activity,
    val prefs: SharedPreferences
) : ViewModel(){

    fun backPressed(){
        activity.onBackPressed()
    }
}