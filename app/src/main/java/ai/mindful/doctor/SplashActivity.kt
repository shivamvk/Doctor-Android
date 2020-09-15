package ai.mindful.doctor

import ai.mindful.doctor.di.DoctorApplication
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import javax.inject.Inject
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.get
import io.shivamvk.networklibrary.sharedprefs.SharedPrefKeys

class SplashActivity : AppCompatActivity() {

    @Inject lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            openActivity()
        }, 2000)
    }

    fun openActivity(){
        if (prefs[SharedPrefKeys.USER_TOKEN.toString(),""]?.isEmpty()!!){
            if (!prefs[SharedPrefKeys.ONBOARDING_DONE.toString(), false]!!){
                startActivity(Intent(
                    this, OnboardingActivity::class.java
                ))
            } else {
                startActivity(Intent(
                    this, LoginActivity::class.java
                ))
            }
        } else {
            startActivity(Intent(
                this, MainActivity::class.java
            ))
        }
        finish()
    }
}