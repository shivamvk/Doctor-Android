package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityEditProfileBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.viewmodel.EditProfileActivityViewModel
import ai.mindful.doctor.utils.ViewModelFactory
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import io.shivamvk.networklibrary.api.ApiService
import java.lang.Exception
import javax.inject.Inject

class EditProfileActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    @Inject lateinit var apiService: ApiService
    @Inject lateinit var prefs: SharedPreferences
    lateinit var binding: ActivityEditProfileBinding
    lateinit var viewmodel: EditProfileActivityViewModel
    var isShow = true
    var scrollRange = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.profileAppBar.addOnOffsetChangedListener(this)
        viewmodel = ViewModelProviders.of(
            this,
            ViewModelFactory(apiService, this, prefs)
        ).get(EditProfileActivityViewModel::class.java)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        try{
            binding.profileCollapsingSection.setCollapsedTitleTextColor(
                resources.getColor(
                    R.color.colorWhite
                )
            )
            if (scrollRange == -1){
                scrollRange = appBarLayout!!.totalScrollRange
            }
            Log.i("profile", "scroll $scrollRange, $verticalOffset")
            if (scrollRange + verticalOffset - 500 < 0) {
                Toast.makeText(this, "show", Toast.LENGTH_SHORT).show()
                binding.profileCollapsingSection.title = "Edit profile"
                binding.expandedToolbarContent.visibility = View.INVISIBLE
                isShow = true
            } else {
                Toast.makeText(this, "hide", Toast.LENGTH_SHORT).show()
                binding.profileCollapsingSection.title = ""
                binding.expandedToolbarContent.visibility = View.VISIBLE
                isShow = false
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}