package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityOnboardingBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.adapter.OnBoardingViewPagerAdapter
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.shivamvk.networklibrary.sharedprefs.SharedPrefKeys
import javax.inject.Inject
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.set

class OnboardingActivity : AppCompatActivity() {

    @Inject lateinit var prefs: SharedPreferences
    lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)

        binding.viewpager.adapter = OnBoardingViewPagerAdapter()
        binding.tabLayout.setupWithViewPager(binding.viewpager, true)

        binding.skip.setOnClickListener {
            prefs[SharedPrefKeys.ONBOARDING_DONE.toString()] = true
            finish()
            startActivity(
                Intent(
                    this, LoginActivity::class.java
                )
            )
        }
    }
}