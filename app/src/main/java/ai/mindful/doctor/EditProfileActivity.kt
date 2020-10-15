package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityEditProfileBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.viewmodel.EditProfileActivityViewModel
import ai.mindful.doctor.utils.ViewModelFactory
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.BuildConfig
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.UtilModelArray
import io.shivamvk.networklibrary.model.emuprofile.ProfileResponse
import io.shivamvk.networklibrary.model.emuprofile.User
import io.shivamvk.networklibrary.models.BaseModel
import io.shivamvk.networklibrary.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.et_name
import kotlinx.android.synthetic.main.pill_layout.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class EditProfileActivity : AppCompatActivity(),
    View.OnClickListener, ApiManagerListener {

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
    var profilePictureF: File? = null
    var panImageF: File? = null
    var aadharImageF: File? = null
    var chequeImageF: File? = null

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
        binding.saveProfile.setOnClickListener {
            if (binding.etName.editText?.text.toString().isEmpty()) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                saveProfile()
            }
        }
        binding.saveDocs.setOnClickListener {
            saveDocs()
        }
        ApiManager(
            ApiRoutes.getProfile,
            apiService,
            ProfileResponse(),
            this,
            null
        ).doGETAPICall()
    }

    fun saveDocs(){
        var params = ArrayList<MultipartBody.Part>()
        var panMP = if (panImageF == null)
            null else MultipartBody.Part
            .createFormData(
                "pan",
                panImageF?.name,
                panImageF!!
                    .asRequestBody(("image/" + panImageF?.extension).toMediaTypeOrNull())
            )
        var aadharMP = if (aadharImageF == null)
            null else MultipartBody.Part
            .createFormData(
                "aadhaar",
                aadharImageF?.name,
                aadharImageF!!
                    .asRequestBody(("image/" + aadharImageF?.extension).toMediaTypeOrNull())
            )
        var chequeMP = if (chequeImageF == null)
            null else MultipartBody.Part
            .createFormData(
                "cheque",
                chequeImageF?.name,
                chequeImageF!!
                    .asRequestBody(("image/" + chequeImageF?.extension).toMediaTypeOrNull())
            )
        if (panMP != null) {
            params.add(panMP)
        }
        if (aadharMP != null) {
            params.add(aadharMP)
        }
        if (chequeMP != null) {
            params.add(chequeMP)
        }
        ApiManager(
            ApiRoutes.EMUupdateDocs,
            apiService,
            UtilModel(),
            this,
            null
        ).doPostAPIMultiPartCall(params)
    }

    fun saveProfile() {
        var params = ArrayList<MultipartBody.Part>()
        var profilePictureMultipart = if (profilePictureF == null)
            null else MultipartBody.Part
            .createFormData(
                "profile_picture",
                profilePictureF?.name,
                profilePictureF!!
                    .asRequestBody(("image/" + profilePictureF?.extension).toMediaTypeOrNull())
            )
        if (profilePictureF != null) {
            params.add(profilePictureMultipart!!)
            ApiManager(
                ApiRoutes.ULupdateProfile,
                apiService,
                UtilModel(),
                this,
                null
            ).doPostAPIMultiPartCall(params)
        }

        var json = JsonObject().apply {
            addProperty("full_name", et_name.editText?.text.toString())
//            addProperty("mobile_number", et_mobile.editText?.text.toString())
            addProperty("bio", et_bio.editText?.text.toString())
            addProperty("college", et_college.editText?.text.toString())
            addProperty("qualification", et_qualification.editText?.text.toString())
            addProperty("experience", et_experience.editText?.text.toString())
            addProperty("expertise", et_expertise.editText?.text.toString())
        }

        ApiManager(
            ApiRoutes.ULupdateProfile,
            apiService,
            UtilModel(),
            this,
            CustomProgressDialog(this)
        ).doPOSTAPICall(json)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PROFILE_PICTURE_PICKER_CODE -> {
                    binding.profilePicture.setImageURI(data?.data)
                    profilePictureF = ImagePicker.getFile(data)!!
                }
                PAN_PICTURE_PICKER_CODE -> {
                    binding.panImage.setImageURI(data?.data)
                    panImageF = ImagePicker.getFile(data)!!
                }
                AADHAR_PICTURE_PICKER_CODE -> {
                    binding.aadharImage.setImageURI(data?.data)
                    aadharImageF = ImagePicker.getFile(data)!!
                }
                CHEQUE_PICTURE_PICKER_CODE -> {
                    binding.chequeImage.setImageURI(data?.data)
                    chequeImageF = ImagePicker.getFile(data)!!
                }
            }
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is UtilModel) {
            Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else if (dataModel is ProfileResponse){
            Log.i("profile", "$response##")
            var model = Gson().fromJson(response, ProfileResponse::class.java).data?.user
            fillDetails(model!!)
        }
    }

    private fun fillDetails(user: User) {
        binding.etName.editText?.setText(user.full_name)
        binding.etMobile.editText?.setText("1234567890")
        binding.etBio.editText?.setText(user.bio)
        binding.etCollege.editText?.setText(user.college)
        binding.etQualification.editText?.setText(user.qualification)
        binding.etExperience.editText?.setText(user.experience.toString())
        binding.etExpertise.editText?.setText(user.expertise)
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.pan)
            .into(binding.panImage)
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.aadhaar)
            .into(binding.aadharImage)
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.cheque)
            .into(binding.chequeImage)
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}