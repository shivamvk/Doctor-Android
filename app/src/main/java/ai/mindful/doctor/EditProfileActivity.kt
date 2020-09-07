package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityEditProfileBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.viewmodel.EditProfileActivityViewModel
import ai.mindful.doctor.utils.ViewModelFactory
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.appbar.AppBarLayout
import io.shivamvk.networklibrary.api.ApiService
import java.lang.Exception
import javax.inject.Inject

class EditProfileActivity : AppCompatActivity(),
    View.OnClickListener {

    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var prefs: SharedPreferences
    lateinit var binding: ActivityEditProfileBinding
    lateinit var viewmodel: EditProfileActivityViewModel
    var isShow = true
    var scrollRange = -1
    val PROFILE_PICTURE_PICKER_CODE = 457
    val PAN_PICTURE_PICKER_CODE = 742
    val AADHAR_PICTURE_PICKER_CODE = 100
    val CHEQUE_PICTURE_PICKER_CODE = 963

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        viewmodel = ViewModelProviders.of(
            this,
            ViewModelFactory(apiService, this, prefs)
        ).get(EditProfileActivityViewModel::class.java)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this
        binding.panImage.setOnClickListener(this)
        binding.aadharImage.setOnClickListener(this)
        binding.chequeImage.setOnClickListener(this)
        binding.profilePicture.setOnClickListener(this)
        binding.back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.profile_picture -> {
                ImagePicker.with(this)
                    .cropSquare()
                    .start(PROFILE_PICTURE_PICKER_CODE)
            }
            R.id.pan_image -> {
                ImagePicker.with(this)
                    .crop(16f, 9f)
                    .start(PAN_PICTURE_PICKER_CODE)
            }
            R.id.aadhar_image -> {
                ImagePicker.with(this)
                    .crop(16f, 9f)
                    .start(AADHAR_PICTURE_PICKER_CODE)
            }
            R.id.cheque_image -> {
                ImagePicker.with(this)
                    .crop(16f, 9f)
                    .start(CHEQUE_PICTURE_PICKER_CODE)
            }
            R.id.back -> {
                onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                PROFILE_PICTURE_PICKER_CODE -> {
                    binding.profilePicture.setImageURI(data?.data)
                }
                PAN_PICTURE_PICKER_CODE -> {
                    binding.panImage.setImageURI(data?.data)
                }
                AADHAR_PICTURE_PICKER_CODE -> {
                    binding.aadharImage.setImageURI(data?.data)
                }
                CHEQUE_PICTURE_PICKER_CODE -> {
                    binding.chequeImage.setImageURI(data?.data)
                }
            }
        }
    }
}