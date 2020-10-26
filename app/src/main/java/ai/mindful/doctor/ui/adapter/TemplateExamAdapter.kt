package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.TemplateExamItemLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.appointment.ExamModel
import io.shivamvk.networklibrary.model.appointment.TemplateModel
import io.shivamvk.networklibrary.model.callasessment.CARosAnswerModel
import io.shivamvk.networklibrary.model.callasessment.CARosModel
import io.shivamvk.networklibrary.model.callasessment.CallAsessmentModel

class TemplateExamAdapter(
    val context: Context,
    val data: List<TemplateModel>,
    var callAsessmentModel: CallAsessmentModel
): RecyclerView.Adapter<TemplateExamAdapter.ViewHolder>() {

    class ViewHolder(val binding: TemplateExamItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
            templateModel: TemplateModel,
            context: Context,
            callAsessmentModel: CallAsessmentModel,
            position: Int
        ){
            binding.template = templateModel
            var examModel = CARosModel()
            examModel.question = templateModel.title
            var answers : MutableList<CARosAnswerModel> = emptyList<CARosAnswerModel>().toMutableList()
            for (option in templateModel.answers){
                var rb = RadioButton(context)
                rb.id = View.generateViewId()
                rb.text = option
                binding.rgOptions.addView(rb)
                var caRosModel = CARosModel()
                callAsessmentModel.examination?.forEach {
                    if (it.question == templateModel.title){
                        caRosModel = it
                    }
                }
                if (caRosModel?.answers?.contains(CARosAnswerModel(option, true))!!){
                    rb.isChecked = true
                    answers.add(CARosAnswerModel(option, true))
                }
                rb.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked){
                        answers.clear()
                        answers.add(CARosAnswerModel(option, true))
                    }
                }
            }
            examModel.answers = answers
            callAsessmentModel.examination?.add(examModel)
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
        holder.bind(data[position], context, callAsessmentModel, position)
}