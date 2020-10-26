package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.RosOptionItemLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.callasessment.CARosAnswerModel
import io.shivamvk.networklibrary.model.callasessment.CARosModel
import io.shivamvk.networklibrary.model.callasessment.CallAsessmentModel

class RosOptionAdapter(
    val context: Context,
    val data: ArrayList<String>,
    val title: String,
    var callAsessmentModel: CallAsessmentModel
) : RecyclerView.Adapter<RosOptionAdapter.ViewHolder>() {

    class ViewHolder(val binding: RosOptionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            optionsModel: String,
            context: Context,
            title: String,
            callAsessmentModel: CallAsessmentModel
        ) {
            binding.option = optionsModel
            var caRosModel = CARosModel()
            callAsessmentModel.ros?.forEach {
                if (it.question == title) {
                    caRosModel = it
                }
            }
            if (caRosModel.question?.isNotEmpty()!!){
                if (caRosModel.answers?.contains(CARosAnswerModel(optionsModel, true))!!){
                    binding.cb.isChecked = true
                }
            }
            binding.cb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    var caRosModel = CARosModel()
                    callAsessmentModel.ros?.forEach {
                        if (it.question == title) {
                            caRosModel = it
                        }
                    }
                    if (caRosModel.question.isNullOrEmpty()) {
                        caRosModel.question = title
                    } else {
                        callAsessmentModel.ros?.remove(caRosModel)
                    }
                    caRosModel.answers?.add(CARosAnswerModel(optionsModel, true))
                    callAsessmentModel.ros?.add(caRosModel)
                } else {
                    var caRosModel = CARosModel()
                    callAsessmentModel.ros?.forEach {
                        if (it.question == title) {
                            caRosModel = it
                        }
                    }
                    callAsessmentModel.ros?.remove(caRosModel)
                    if (!caRosModel.question.isNullOrEmpty())
                        caRosModel.answers?.remove(CARosAnswerModel(optionsModel, true))

                    if (caRosModel.answers?.isNotEmpty()!!)
                        callAsessmentModel.ros?.add(caRosModel)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RosOptionItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], context, title, callAsessmentModel)

}