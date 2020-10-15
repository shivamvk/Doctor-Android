package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityULEditProfileBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.adapter.PillSelectorAdapter
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
import kotlinx.android.synthetic.main.activity_edit_profile.et_name
import kotlinx.android.synthetic.main.activity_u_l_edit_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ULEditProfileActivity : AppCompatActivity(), ApiManagerListener{

    lateinit var binding: ActivityULEditProfileBinding
    lateinit var languagesAdapter: PillSelectorAdapter<LanguageModel>
    lateinit var symptomsAdapter: PillSelectorAdapter<SimpleSymptomModel>
    @Inject lateinit var apiService: ApiService
    var dob = ""
    var medicalLicenseExpDate = ""
    var deaLicenseEXpDate = ""
    var profilePictureFile:File? = null
    var dlFile: File? = null
    var medicallicencseFile :File? = null
    var deaLicenseFile: File? = null
    var resumeFile: File? = null
    var signatureFile : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_u_l_edit_profile)
        supportActionBar?.hide()
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
                    binding.etDob.setText("${dayOfMonth}-${month}-${year}")
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
                    binding.etMedicalLicenseExp.setText("${dayOfMonth}-${month}-${year}")
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
                    binding.etDeaExpDate.setText("${dayOfMonth}-${month}-${year}")
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
                addProperty("gender", et_gender?.editText?.text.toString())
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
                addProperty("yearsOfExperience", et_years_of_experience.editText?.text.toString())
                addProperty("malpracticeLawsuites", malpractice_yes.isChecked)
                addProperty("medicalBoardDeciplinaryAction", mbda_yes.isChecked)
//                addProperty("checkbox1.accepted", cb_1.isChecked)
                add("languages", languagesArray)
//                add("symptoms", symptomsArray)
//                addProperty("checkbox2.accepted", cb_2.isChecked)
//                addProperty("checkbox3.accepted", cb_3.isChecked)
//                addProperty("checkbox4.accepted", cb_4.isChecked)
//                addProperty("checkbox5.accepted", cb_5.isChecked)
                addProperty("bank.name", et_bank_name.editText?.text.toString())
                addProperty("bank.customerName", et_customer_name.editText?.text.toString())
                addProperty("bank.routingNumber", et_routing_number.editText?.text.toString())
                addProperty("bank.accountNumber", et_account_number.editText?.text.toString())
            }
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

    fun saveFiles(){
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
        if (dataModel is LanguageResponse){
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
        } else if(dataModel is SimpleSymptomResponse){
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
        } else if (dataModel is UtilModel){
            Log.i("response", response)
            var errors = Gson().fromJson(response, UtilModel::class.java).errors
            if (errors){
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
        } else if(dataModel is UtilModelArray){
            val model = Gson().fromJson(response, ProfileResponse::class.java).data.user
            Log.i("response profile", response)
            fillDetails(model)
        }
    }

    private fun fillDetails(user: User) {
        et_name.editText?.setText(user.full_name)
        et_gender.editText?.setText(user.gender)
        et_country.editText?.setText(user.country)
        et_dob.setText(readableStringFromISO(user.dob))
        et_postal_code.editText?.setText(user.postalCode)
        et_mobile.editText?.setText(user.mobileNumber)
        et_medical_license_number.editText?.setText(user.medicalLicenseNumber)
        et_medical_license_exp.setText(readableStringFromISO(user.medicalLicenseExpiration))
        et_medical_license_state.editText?.setText(user.medicalLicenseState)
//        et_education.editText?.setText(user.education)
//        et_college.editText?.setText(user.college)
//        et_professional_statement.editText?.setText(user.professionalStatement)
        et_years_of_experience.editText?.setText(user.yearsOfExperience.toString())
//        et_state_license.editText?.setText(user.stateLicense)
//        et_license_expiration.editText?.setText(user.licenseExpiration)
//        et_board_expiration.editText?.setText(user.boardExpiration)
        et_dea_license_number.editText?.setText(user.deaLicenseNumber)
        et_dea_exp_date.setText(readableStringFromISO(user.deaLicenseExpirationDate))
        et_bank_name.editText?.setText(user.bank?.name)
        et_customer_name.editText?.setText(user.bank?.customerName)
        et_routing_number.editText?.setText(user.bank?.routingNumber)
        et_account_number.editText?.setText(user.bank?.accountNumber)
        malpractice_yes.isChecked = user.malpracticeLawsuites
        mbda_yes.isChecked = user.medicalBoardDeciplinaryAction
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

//        cb_1.isChecked = user.checkbox1.accepted
//        cb_2.isChecked = user.checkbox2.accepted
//        cb_3.isChecked = user.checkbox3.accepted
//        cb_4.isChecked = user.checkbox4.accepted
//        cb_5.isChecked = user.checkbox5.accepted
        user.languages.forEach {
            languagesAdapter.selectedItems.add(it._id)
        }
//        user.symptoms.forEach {
//            symptomsAdapter.selectedItems.add(it._id!!)
//        }
        languagesAdapter.notifyDataSetChanged()
//        symptomsAdapter.notifyDataSetChanged()
    }

    private fun readableStringFromISO(string: String): String {
        return try{
            var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            var date = gmttoLocalDate(sdf.parse(string))
            SimpleDateFormat("dd-MM-yyyy").format(date)
        } catch (e: Exception){
            e.printStackTrace()
            ""
        }
    }

    fun gmttoLocalDate(date: Date): Date? {
        val timeZone = Calendar.getInstance().timeZone.id
        return Date(date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time))
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
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
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