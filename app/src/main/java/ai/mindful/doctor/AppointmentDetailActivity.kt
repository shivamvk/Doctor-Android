package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityAppointmentDetailBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
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
    }
}