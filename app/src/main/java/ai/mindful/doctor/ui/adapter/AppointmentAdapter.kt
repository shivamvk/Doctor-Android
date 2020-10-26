package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.AppointmentDetailActivity
import ai.mindful.doctor.VideoCallActivity
import ai.mindful.doctor.databinding.AppointmentItemLayoutBinding
import ai.mindful.doctor.utils.CustomBindingAdapters
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.appointment.AppointmentModel

class AppointmentAdapter(
    val context: Context,
    val data: List<AppointmentModel>
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: AppointmentItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            context: Context,
            appointment: AppointmentModel
        ){
            binding.appointment = appointment
            binding.dobGender.text = "${CustomBindingAdapters.readableStringFromISO(appointment.patient?.dob!!)}, ${appointment.patient?.gender}"
            binding.item.setOnClickListener {
                context.startActivity(Intent(
                    context,
                    AppointmentDetailActivity::class.java
                ).putExtra("appointmentModel", appointment))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AppointmentItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(context, data[position])
}