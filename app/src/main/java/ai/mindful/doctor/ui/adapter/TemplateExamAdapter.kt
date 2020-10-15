package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.TemplateExamItemLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.appointment.ExamModel
import io.shivamvk.networklibrary.model.appointment.RosExamBookingPutModel
import io.shivamvk.networklibrary.model.appointment.TemplateModel

class TemplateExamAdapter(
    val context: Context,
    val data: List<TemplateModel>,
    var rosExamBookingPutModel: RosExamBookingPutModel
): RecyclerView.Adapter<TemplateExamAdapter.ViewHolder>() {

    class ViewHolder(val binding: TemplateExamItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
            templateModel: TemplateModel,
            context: Context,
            rosExamBookingPutModel: RosExamBookingPutModel,
            position: Int
        ){
            binding.template = templateModel
            var examModel = ExamModel()
            examModel.type = templateModel.title
            var answers = ArrayList<String>()
            for (option in templateModel.answers){
                var rb = RadioButton(context)
                rb.id = View.generateViewId()
                rb.text = option
                binding.rgOptions.addView(rb)
                if (option == templateModel.answers[0]){
                    rb.isChecked = true
                    answers.add(option)
                }
                rb.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked){
                        answers.clear()
                        answers.add(option)
                    }
                }
            }
            examModel.options = answers
            rosExamBookingPutModel.examination?.add(examModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TemplateExamItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int  = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], context, rosExamBookingPutModel, position)
}