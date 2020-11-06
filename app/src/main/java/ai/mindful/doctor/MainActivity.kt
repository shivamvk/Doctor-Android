package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityDrawerBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.fragment.*
import ai.mindful.doctor.utils.ClientPrefs
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.BuildConfig
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.UtilModelArray
import io.shivamvk.networklibrary.model.profile.ProfileResponse
import io.shivamvk.networklibrary.model.profile.User
import io.shivamvk.networklibrary.models.BaseModel
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.get
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.set
import io.shivamvk.networklibrary.sharedprefs.SharedPrefKeys
import kotlinx.android.synthetic.main.nav_header_layout.view.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ApiManagerListener {

    private var doubleBackPressedOnce: Boolean = false
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var prefs: SharedPreferences
    lateinit var binding: ActivityDrawerBinding
    lateinit var homeFragment: HomeFragment
    lateinit var fcmToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_drawer)
        val toggle =
            ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                null,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
        binding.activityMain.menuMain.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        binding.drawerLayout.addDrawerListener(toggle)
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.getHeaderView(0).setOnClickListener {
            if (ClientPrefs.isEazemeupClient) {
                startActivity(
                    Intent(
                        this, EditProfileActivity::class.java
                    )
                )
            } else {
                startActivity(
                    Intent(
                        this, ULEditProfileActivity::class.java
                    )
                )
            }
        }
        if (prefs[SharedPrefKeys.FCM_TOKEN.toString(), ""].isNullOrEmpty()) {
            registerFcm()
        }
        toggle.syncState()
        binding.tvAppVersion.text = "App Version ${ai.mindful.doctor.BuildConfig.VERSION_NAME} (${ai.mindful.doctor.BuildConfig.VERSION_CODE})"
        homeFragment = HomeFragment()
        goto(homeFragment)
        if (!ClientPrefs.isEazemeupClient){
            ApiManager(
                ApiRoutes.getProfile,
                apiService,
                UtilModelArray(),
                this,
                null
            ).doGETAPICall()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.navView.getHeaderView(0).uname?.text =
            prefs[SharedPrefKeys.USER_NAME.toString(), ""]
        binding.navView.getHeaderView(0).uphone?.text =
            prefs[SharedPrefKeys.USER_EMAIL.toString(), ""]
        Glide.with(this)
            .load(BuildConfig.AWSURL + prefs[SharedPrefKeys.USER_PICTURE.toString(), ""])
            .error(R.drawable.ic_user)
            .into(binding.navView.getHeaderView(0).userthumbimage)
    }

    fun goto(fragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                goto(homeFragment)
            }
            R.id.nav_appointment -> {
                goto(AppointmentFragment())
            }
            R.id.nav_wallet -> {
                goto(WalletFragment())
            }
            R.id.nav_support -> {
                startActivity(
                    Intent(
                        this,
                        CSTicketActivity::class.java
                    )
                )
            }
            R.id.nav_tnc -> {
                goto(TncFragment())
            }
            R.id.nav_privacy -> {
                goto(PrivacyFragment())
            }
            R.id.nav_logout -> {
                logout()
            }
            else -> {
                goto(homeFragment)
            }
        }
        binding.drawerLayout.closeDrawer(Gravity.LEFT)
        return true
    }

    fun logout() {
        prefs[SharedPrefKeys.USER_NAME.toString()] = ""
        prefs[SharedPrefKeys.USER_EMAIL.toString()] = ""
        prefs[SharedPrefKeys.USER_TOKEN.toString()] = ""
        prefs[SharedPrefKeys.USER_ID.toString()] = ""
        prefs[SharedPrefKeys.USER_PICTURE.toString()] = ""
        prefs[SharedPrefKeys.FCM_TOKEN.toString()] = ""
        (application as DoctorApplication).initDagger()
        finish()
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )
    }

    fun registerFcm() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    fcmToken = it.result?.token.toString()
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

    fun openGmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "message/rfc822"
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ClientPrefs.clientSupportEmail))
            putExtra(Intent.EXTRA_SUBJECT, "Help needed!")
        }
        startActivity(Intent.createChooser(intent, "Send an email using "));
    }

    override fun onBackPressed() {
        if (doubleBackPressedOnce) {
            super.onBackPressed()
            return
        }

        doubleBackPressedOnce = true
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(
            Runnable { doubleBackPressedOnce = false },
            2000
        )
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is UtilModel) {
            prefs[SharedPrefKeys.FCM_TOKEN.toString()] = fcmToken
            Log.i("fcmRegistration", "success")
        } else if (dataModel is UtilModelArray) {
            val model = Gson().fromJson(response, ProfileResponse::class.java).data.user
            checkIfProfileIsComplete(model)
        }
    }

    private fun checkIfProfileIsComplete(user: User) {
        if (user.full_name.isNullOrEmpty() ||
            user.gender.isNullOrEmpty() ||
//            user.mobileNumber.isEmpty() ||
            user.languages.isNullOrEmpty() ||
            user.yearsOfExperience.isNullOrEmpty() ||
//            user.kyc == null ||
            user.bank?.name.isNullOrEmpty() ||
            user.bank?.customerName.isNullOrEmpty() ||
            user.bank?.routingNumber.isNullOrEmpty() ||
            user.bank?.accountNumber.toString().isEmpty()
        ) {
            finishAffinity()
            Toast.makeText(this, "Please complete your profile before starting", Toast.LENGTH_SHORT)
                .show()
            startActivity(
                Intent(
                    this, ULEditProfileActivity::class.java
                ).putExtra("incompleteProfile", true)
            )
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(this, "${e.localizedMessage}. Please try again!", Toast.LENGTH_SHORT).show()
    }
}