package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityULEditProfileBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.adapter.PillSelectorAdapter
import ai.mindful.doctor.utils.CustomBindingAdapters
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.BuildConfig
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.UtilModelArray
import io.shivamvk.networklibrary.model.appointment.LanguageModel
import io.shivamvk.networklibrary.model.appointment.LanguageResponse
import io.shivamvk.networklibrary.model.appointment.SimpleSymptomModel
import io.shivamvk.networklibrary.model.appointment.SimpleSymptomResponse
import io.shivamvk.networklibrary.model.profile.ProfileResponse
import io.shivamvk.networklibrary.model.profile.User
import io.shivamvk.networklibrary.models.BaseModel
import kotlinx.android.synthetic.main.activity_u_l_edit_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ULEditProfileActivity : AppCompatActivity(), ApiManagerListener {

    lateinit var binding: ActivityULEditProfileBinding
    lateinit var languagesAdapter: PillSelectorAdapter<LanguageModel>
    lateinit var symptomsAdapter: PillSelectorAdapter<SimpleSymptomModel>
    @Inject
    lateinit var apiService: ApiService
    var dob = ""
    var medicalLicenseExpDate = ""
    var deaLicenseEXpDate = ""
    var profilePictureFile: File? = null
    var dlFile: File? = null
    var medicallicencseFile: File? = null
    var deaLicenseFile: File? = null
    var resumeFile: File? = null
    var signatureFile: File? = null
    var incompleteProfile: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_u_l_edit_profile)
        supportActionBar?.hide()
        incompleteProfile = intent.getBooleanExtra("incompleteProfile", false)
        if (incompleteProfile) {
            binding.back.visibility = View.GONE
            binding.title.text = "We need the following details from you before you start"
        }
        ApiManager(
            ApiRoutes.languages,
            apiService,
            LanguageResponse(),
            this,
            null
        ).doGETAPICall()
        ApiManager(
            ApiRoutes.symptoms,
            apiService,
            SimpleSymptomResponse(),
            this,
            null
        ).doGETAPICall()
        ApiManager(
            ApiRoutes.getProfile,
            apiService,
            UtilModelArray(),
            this,
            null
        ).doGETAPICall()
        binding.etDob.setOnClickListener {
            val c = Calendar.getInstance()
            var dp = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    binding.etDob.setText("${month}-${dayOfMonth}-${year}")
                    var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    var cal = Calendar.getInstance()
                    cal.set(year, month, dayOfMonth)
                    dob = sdf.format(cal.time)
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            dp.show()
        }
        binding.etMedicalLicenseExp.setOnClickListener {
            val c = Calendar.getInstance()
            var dp = DatePickerDialog(
                this,
                { view, year, month, dayOfMonth ->
                    binding.etMedicalLicenseExp.setText("${month}-${dayOfMonth}-${year}")
                    var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    var cal = Calendar.getInstance()
                    cal.set(year, month, dayOfMonth)
                    medicalLicenseExpDate = sdf.format(cal.time)
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            dp.show()
        }
        binding.etDeaExpDate.setOnClickListener {
            val c = Calendar.getInstance()
            var dp = DatePickerDialog(
                this,
                { view, year, month, dayOfMonth ->
                    binding.etDeaExpDate.setText("${month}-${dayOfMonth}-${year}")
                    var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    var cal = Calendar.getInstance()
                    cal.set(year, month, dayOfMonth)
                    deaLicenseEXpDate = sdf.format(cal.time)
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            dp.show()
        }

        binding.ivDrivingLicense.setOnClickListener {
            ImagePicker.with(this)
                .crop(16f, 9f)
                .start(461)
        }
        binding.ivMedicalLicense.setOnClickListener {
            ImagePicker.with(this)
                .crop(16f, 9f)
                .start(462)
        }
        binding.ivDeaLicense.setOnClickListener {
            ImagePicker.with(this)
                .crop(16f, 9f)
                .start(463)
        }
        binding.ivResume.setOnClickListener {
            ImagePicker.with(this)
                .crop(9f, 16f)
                .start(464)
        }
        binding.ivSignature.setOnClickListener {
            ImagePicker.with(this)
                .crop(4f, 4f)
                .start(465)
        }
        binding.profileImage.setOnClickListener {
            ImagePicker.with(this)
                .crop(4f, 4f)
                .start(460)
        }
        binding.save.setOnClickListener {
            if (!validateInputs()) {
                return@setOnClickListener
            }
            binding.formLayout.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            Log.i("langues####", languagesAdapter.selectedItems.toString())
//            Log.i("symtoms###", symptomsAdapter.selectedItems.toString())
            var languagesArray = JsonArray()
            languagesAdapter.selectedItems.forEach {
                languagesArray.add(it)
            }
//            var symptomsArray = JsonArray()
//            symptomsAdapter.selectedItems.forEach {
//                symptomsArray.add(it)
//            }
            var jsonObject = JsonObject().apply {
                addProperty("full_name", et_name?.editText?.text.toString())
                addProperty("gender", sp_gender.selectedItem.toString())
                addProperty("dob", dob)
                addProperty("country", et_country?.editText?.text.toString())
                addProperty("postalCode", et_postal_code.editText?.text.toString())
                addProperty("mobileNumber", et_mobile.editText?.text.toString())
                addProperty("medicalLicenseType", sp_medical_license_type.selectedItem.toString())
                addProperty(
                    "medicalLicenseNumber",
                    et_medical_license_number.editText?.text.toString()
                )
                addProperty("medicalLicenseExpiration", medicalLicenseExpDate)
                addProperty(
                    "medicalLicenseState",
                    et_medical_license_state.editText?.text.toString()
                )
                addProperty("boardCertified", board_certified_yes.isChecked)
                addProperty("deaLicenseNumber", et_dea_license_number.editText?.text.toString())
                addProperty("deaLicenseExpirationDate", deaLicenseEXpDate)
//                addProperty("professionalStatement", et_professional_statement.editText?.text.toString())
//                addProperty("education", et_education.editText?.text.toString())
//                addProperty("college", et_college.editText?.text.toString())
//                addProperty("stateLicense", et_state_license.editText?.text.toString())
//                addProperty("licenseExpiration", et_license_expiration.editText?.text.toString())
//                addProperty("boardExpiration", et_board_expiration.editText?.text.toString())
                addProperty(
                    "yearsOfExperience",
                    et_years_of_experience.editText?.text.toString().toInt()
                )
                addProperty("malpracticeLawsuites", malpractice_yes.isChecked)
                addProperty("medicalBoardDeciplinaryAction", mbda_yes.isChecked)
                addProperty("boardCertified", board_certified_yes.isChecked)
                addProperty("checkbox1.accepted", cb_1.isChecked)
                add("languages", languagesArray)
//                add("symptoms", symptomsArray)
                addProperty("checkbox2.accepted", cb_2.isChecked)
                addProperty("checkbox3.accepted", cb_3.isChecked)
                addProperty("checkbox4.accepted", cb_4.isChecked)
                addProperty("checkbox5.accepted", cb_5.isChecked)
                addProperty("bank.name", et_bank_name.editText?.text.toString())
                addProperty("bank.customerName", et_customer_name.editText?.text.toString())
                addProperty("bank.routingNumber", et_routing_number.editText?.text.toString())
                addProperty("bank.accountNumber", et_account_number.editText?.text.toString())
            }
            Log.i("updateProfile", jsonObject.toString())
            saveFiles()
            ApiManager(
                ApiRoutes.ULupdateProfile,
                apiService,
                UtilModel(),
                this,
                null
            ).doPOSTAPICall(jsonObject)
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun validateInputs(): Boolean {
        if (et_name.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (dob.isEmpty()) {
            Toast.makeText(this, "Date of birth is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_country.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Country is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_postal_code.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Postal code is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_mobile.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Mobile is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_medical_license_number.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Medical license no. is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (medicalLicenseExpDate.isEmpty()) {
            Toast.makeText(this, "Medical license exp date is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_medical_license_state.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Medical license state is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_years_of_experience.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Experience is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_dea_license_number.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "DEA license no. is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (deaLicenseEXpDate.isEmpty()) {
            Toast.makeText(this, "DEA license exp date is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_bank_name.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Bank n ame is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_customer_name.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Account holder name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_routing_number.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Routing no. is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_account_number.editText?.text.toString().isEmpty()) {
            Toast.makeText(this, "Account no. is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (languagesAdapter.selectedItems.isEmpty()) {
            Toast.makeText(this, "At least one language is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!binding.cb1.isChecked ||
            !binding.cb2.isChecked || !binding.cb3.isChecked || !binding.cb4.isChecked || !binding.cb5.isChecked
        ) {
            Toast.makeText(this, "Please read and accept all the disclaimers", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun saveFiles() {
        var params = ArrayList<MultipartBody.Part>()
        var profileMP = if (profilePictureFile == null)
            null else MultipartBody.Part
            .createFormData(
                "profile_picture",
                profilePictureFile?.name,
                profilePictureFile!!
                    .asRequestBody(("image/" + profilePictureFile?.extension).toMediaTypeOrNull())
            )
        var drivingMP = if (dlFile == null)
            null else MultipartBody.Part
            .createFormData(
                "kyc.drivingLicense",
                dlFile?.name,
                dlFile!!
                    .asRequestBody(("image/" + dlFile?.extension).toMediaTypeOrNull())
            )
        var medicalMP = if (medicallicencseFile == null)
            null else MultipartBody.Part
            .createFormData(
                "kyc.medicalLicense",
                medicallicencseFile?.name,
                medicallicencseFile!!
                    .asRequestBody(("image/" + medicallicencseFile?.extension).toMediaTypeOrNull())
            )
        var deaMP = if (deaLicenseFile == null)
            null else MultipartBody.Part
            .createFormData(
                "kyc.deaLicense",
                deaLicenseFile?.name,
                deaLicenseFile!!
                    .asRequestBody(("image/" + deaLicenseFile?.extension).toMediaTypeOrNull())
            )
        var resumeMP = if (resumeFile == null)
            null else MultipartBody.Part
            .createFormData(
                "kyc.resume",
                resumeFile?.name,
                resumeFile!!
                    .asRequestBody(("image/" + resumeFile?.extension).toMediaTypeOrNull())
            )
        var signatureMP = if (signatureFile == null)
            null else MultipartBody.Part
            .createFormData(
                "kyc.signature",
                signatureFile?.name,
                signatureFile!!
                    .asRequestBody(("image/" + signatureFile?.extension).toMediaTypeOrNull())
            )
        var full_name = MultipartBody.Part.createFormData(
            "full_name",
            et_name.editText?.text.toString()
        )
        if (profileMP != null) {
            params.add(profileMP)
        }
        if (drivingMP != null) {
            params.add(drivingMP)
        }
        if (medicalMP != null) {
            params.add(medicalMP)
        }
        if (deaMP != null) {
            params.add(deaMP)
        }
        if (signatureMP != null) {
            params.add(signatureMP)
        }
        if (resumeMP != null) {
            params.add(resumeMP)
        }
        params.add(full_name) //full name is a required field
        ApiManager(
            ApiRoutes.ULupdateProfile,
            apiService,
            UtilModel(),
            this,
            null
        ).doPostAPIMultiPartCall(params)
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is LanguageResponse) {
            var model = Gson().fromJson(response, LanguageResponse::class.java).data
            var languagesLM = FlexboxLayoutManager(this)
            languagesLM.flexDirection = FlexDirection.ROW
            languagesLM.justifyContent = JustifyContent.FLEX_START
            binding.rvLanguages.layoutManager = languagesLM
            languagesAdapter = PillSelectorAdapter<LanguageModel>(
                emptyList<String>().toMutableList(),
                model!!,
                this
            )
            binding.rvLanguages.adapter = languagesAdapter
            binding.progressBar.visibility = View.GONE
            binding.formLayout.visibility = View.VISIBLE
        } else if (dataModel is SimpleSymptomResponse) {
            Log.i("symptomsresppnse ###", response)
            var model = Gson().fromJson(response, SimpleSymptomResponse::class.java).data
            var symptomsLM = FlexboxLayoutManager(this)
            symptomsLM.flexDirection = FlexDirection.ROW
            symptomsLM.justifyContent = JustifyContent.FLEX_START
//            binding.rvSymptoms.layoutManager = symptomsLM
//            symptomsAdapter = PillSelectorAdapter(emptyList<String>().toMutableList(), model!!, this)
//            binding.rvSymptoms.adapter = symptomsAdapter
            binding.progressBar.visibility = View.GONE
            binding.formLayout.visibility = View.VISIBLE
        } else if (dataModel is UtilModel) {
            Log.i("response", response)
            var errors = Gson().fromJson(response, UtilModel::class.java).errors
            if (errors) {
                binding.formLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    Gson().fromJson(response, UtilModel::class.java).message,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //also update shared prefs
                finishAffinity()
                startActivity(Intent(this, MainActivity::class.java))
            }
        } else if (dataModel is UtilModelArray) {
            val model = Gson().fromJson(response, ProfileResponse::class.java).data.user
            Log.i("response profile", response)
            fillDetails(model)
        }
    }

    private fun fillDetails(user: User) {
        et_name.editText?.setText(user.full_name)
        if (user.gender == "Male" || user.gender == "male") {
            sp_gender.setSelection(0)
        } else {
            sp_gender.setSelection(1)
        }
        et_country.editText?.setText(user.country)
        if (!user.dob.isNullOrEmpty()) {
            et_dob.setText(CustomBindingAdapters.readableStringFromISO(user.dob))
            dob = user.dob
        }
        et_postal_code.editText?.setText(user.postalCode)
        et_mobile.editText?.setText(user.mobileNumber)
        et_medical_license_number.editText?.setText(user.medicalLicenseNumber)
        et_medical_license_exp.setText(CustomBindingAdapters.readableStringFromISO(user.medicalLicenseExpiration))
        medicalLicenseExpDate = user.medicalLicenseExpiration
        et_medical_license_state.editText?.setText(user.medicalLicenseState)
//        et_education.editText?.setText(user.education)
//        et_college.editText?.setText(user.college)
//        et_professional_statement.editText?.setText(user.professionalStatement)
        et_years_of_experience.editText?.setText(user.yearsOfExperience.toString())
//        et_state_license.editText?.setText(user.stateLicense)
//        et_license_expiration.editText?.setText(user.licenseExpiration)
//        et_board_expiration.editText?.setText(user.boardExpiration)
        et_dea_license_number.editText?.setText(user.deaLicenseNumber)
        et_dea_exp_date.setText(CustomBindingAdapters.readableStringFromISO(user.deaLicenseExpirationDate))
        deaLicenseEXpDate = user.deaLicenseExpirationDate
        et_bank_name.editText?.setText(user.bank?.name)
        et_customer_name.editText?.setText(user.bank?.customerName)
        et_routing_number.editText?.setText(user.bank?.routingNumber)
        et_account_number.editText?.setText(user.bank?.accountNumber)
        malpractice_yes.isChecked = user.malpracticeLawsuites
        mbda_yes.isChecked = user.medicalBoardDeciplinaryAction
        board_certified_yes.isChecked = user.boardCertified
        user.languages.forEach {
            languagesAdapter.selectedItems.add(it._id)
        }
//        user.symptoms.forEach {
//            symptomsAdapter.selectedItems.add(it._id!!)
//        }
        languagesAdapter.notifyDataSetChanged()
//        symptomsAdapter.notifyDataSetChanged()
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.kyc!!.drivingLicense)
            .into(iv_driving_license)
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.kyc!!.medicalLicense)
            .into(iv_medical_license)
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.kyc!!.deaLicense)
            .into(iv_dea_license)
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.kyc!!.resume)
            .into(iv_resume)
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.kyc!!.signature)
            .into(iv_signature)
        Glide.with(this)
            .load(BuildConfig.AWSURL + user.profile_picture)
            .into(profile_image)
        cb_1.isChecked = user.checkbox1.accepted
        cb_2.isChecked = user.checkbox2.accepted
        cb_3.isChecked = user.checkbox3.accepted
        cb_4.isChecked = user.checkbox4.accepted
        cb_5.isChecked = user.checkbox5.accepted

    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        binding.formLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
//        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                460 -> {
                    binding.profileImage.setImageURI(data?.data)
                    profilePictureFile = ImagePicker.getFile(data)
                }
                461 -> {
                    binding.ivDrivingLicense.setImageURI(data?.data)
                    dlFile = ImagePicker.getFile(data)
                }
                462 -> {
                    binding.ivMedicalLicense.setImageURI(data?.data)
                    medicallicencseFile = ImagePicker.getFile(data)
                }
                463 -> {
                    binding.ivDeaLicense.setImageURI(data?.data)
                    deaLicenseFile = ImagePicker.getFile(data)
                }
                464 -> {
                    binding.ivResume.setImageURI(data?.data)
                    resumeFile = ImagePicker.getFile(data)
                }
                465 -> {
                    binding.ivSignature.setImageURI(data?.data)
                    signatureFile = ImagePicker.getFile(data)
                }
            }
        }
    }
}