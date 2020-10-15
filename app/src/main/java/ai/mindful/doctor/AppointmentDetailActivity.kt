package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityAppointmentDetailBinding
import ai.mindful.doctor.ui.adapter.PillAdapter
import ai.mindful.doctor.ui.adapter.QnaAdapter
import ai.mindful.doctor.ui.bottomsheet.CancelOrderBottomSheet
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.shivamvk.networklibrary.model.appointment.AppointmentModel

class AppointmentDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityAppointmentDetailBinding
    lateinit var appointmentModel: AppointmentModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail)
        appointmentModel = intent.getSerializableExtra("appointmentModel") as AppointmentModel
        binding.booking = appointmentModel
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.call.setOnClickListener {
            startActivity(Intent(
                this, VideoCallActivity::class.java
            ).putExtra("calleeId", appointmentModel.createdBy?._id)
                .putExtra("appointmentModel", appointmentModel))
        }
        if (appointmentModel.status == "Cancelled by doctor"
            || appointmentModel.status == "Cancelled by patient"
            || appointmentModel.status == "Cancelled by admin"){
            binding.cancelCallSection.visibility = View.GONE
            binding.cancellationReason.visibility = View.VISIBLE
            binding.cancellationReason.text = "${appointmentModel.status}\n${appointmentModel.cancellationReason}"
        }
        binding.cancel.setOnClickListener {
            var cbs = CancelOrderBottomSheet(appointmentModel._id)
            cbs.show(supportFragmentManager, "Cancel order")
        }

        var cmlm = FlexboxLayoutManager(this)
        cmlm.flexDirection = FlexDirection.ROW
        cmlm.justifyContent = JustifyContent.FLEX_START
        binding.rvCurrentMedication.layoutManager = cmlm
        binding.rvCurrentMedication.adapter =
            appointmentModel.patient?.history?.currentMedication?.let { PillAdapter(this, it) }
        if (appointmentModel.patient?.history?.currentMedication.isNullOrEmpty()){
            binding.cmText.visibility = View.GONE
        }

        var alm = FlexboxLayoutManager(this)
        alm.flexDirection = FlexDirection.ROW
        alm.justifyContent = JustifyContent.FLEX_START
        binding.rvAllergies.layoutManager = alm
        binding.rvAllergies.adapter =
            appointmentModel.patient?.history?.allergies?.let { PillAdapter(this, it) }
        if (appointmentModel.patient?.history?.allergies.isNullOrEmpty()){
            binding.aText.visibility = View.GONE
        }

        var slm = FlexboxLayoutManager(this)
        slm.flexDirection = FlexDirection.ROW
        slm.justifyContent = JustifyContent.FLEX_START
        binding.rvSurgeries.layoutManager = slm
        binding.rvSurgeries.adapter =
            appointmentModel.patient?.history?.surgeries?.let { PillAdapter(this, it) }
        if (appointmentModel.patient?.history?.surgeries.isNullOrEmpty()){
            binding.sText.visibility = View.GONE
        }

        var mplm = FlexboxLayoutManager(this)
        mplm.flexDirection = FlexDirection.ROW
        mplm.justifyContent = JustifyContent.FLEX_START
        binding.rvMedicalProblems.layoutManager = mplm
        binding.rvMedicalProblems.adapter =
            appointmentModel.patient?.history?.medicalProblems?.let { PillAdapter(this, it) }
        if (appointmentModel.patient?.history?.medicalProblems.isNullOrEmpty()){
            binding.mpText.visibility = View.GONE
        }

        if (appointmentModel.patient?.history?.smoking!!){
            binding.smokeDetails.text = "Social history: Is an active smoker? Yes"
        } else {
            binding.smokeDetails.text = "Social history: Is an active smoker? No"
        }

        binding.rvQna.layoutManager = LinearLayoutManager(this)
        binding.rvQna.adapter =
            appointmentModel.symptoms?.get(0)?.questions?.let { QnaAdapter(this, it) }
    }

    fun cancelOrder() {
        binding.cancelCallSection.visibility = View.GONE
        binding.cancellationReason.visibility = View.VISIBLE
        binding.cancellationReason.text = "Cancelled"
    }
}