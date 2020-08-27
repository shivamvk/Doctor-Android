package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityDrawerBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.fragment.AppointmentFragment
import ai.mindful.doctor.ui.fragment.HomeFragment
import ai.mindful.doctor.ui.fragment.TncFragment
import ai.mindful.doctor.ui.fragment.WalletFragment
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import io.shivamvk.networklibrary.BuildConfig
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.get
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.set
import io.shivamvk.networklibrary.sharedprefs.SharedPrefKeys
import kotlinx.android.synthetic.main.nav_header_layout.view.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject lateinit var prefs: SharedPreferences
    lateinit var binding: ActivityDrawerBinding
    lateinit var homeFragment: HomeFragment

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
            startActivity(Intent(
                this, EditProfileActivity::class.java
            ))
        }
        toggle.syncState()
        homeFragment = HomeFragment()
        goto(homeFragment)
    }

    override fun onResume() {
        super.onResume()
        binding.navView.getHeaderView(0).uname?.text = prefs[SharedPrefKeys.USER_NAME.toString(), ""]
        binding.navView.getHeaderView(0).uphone?.text = prefs[SharedPrefKeys.USER_EMAIL.toString(), ""]
        Glide.with(this)
            .load(BuildConfig.AWSURL + prefs[SharedPrefKeys.USER_PICTURE.toString(), ""])
            .error(R.drawable.ic_user)
            .into(binding.navView.getHeaderView(0).userthumbimage)
    }

    fun goto(fragment: Fragment){
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
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
                openGmail()
            }
            R.id.nav_tnc -> {
                goto(TncFragment())
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

    fun logout(){
        prefs[SharedPrefKeys.USER_NAME.toString()] = ""
        prefs[SharedPrefKeys.USER_EMAIL.toString()] = ""
        prefs[SharedPrefKeys.USER_TOKEN.toString()] = ""
        prefs[SharedPrefKeys.USER_ID.toString()] = ""
        prefs[SharedPrefKeys.USER_PICTURE.toString()] = ""
        finish()
        startActivity(Intent(
            this,
            LoginActivity::class.java
        ))
    }

    fun openGmail(){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "message/rfc822"
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("info@mindfulmachine.in"))
            putExtra(Intent.EXTRA_SUBJECT, "Help needed!")
        }
        startActivity(Intent.createChooser(intent, "Send an email using "));
    }
}