package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.TemplateExamItemLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.appointment.TemplateModel

class TemplateExamAdapter(val context: Context, val  data: List<TemplateModel>): RecyclerView.Adapter<TemplateExamAdapter.ViewHolder>() {

    class ViewHolder(val binding: TemplateExamItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
            templateModel: TemplateModel,
            context: Context
        ){
            binding.template = templateModel
            for (option in templateModel.options){
                var rb = RadioButton(context)
                rb.id = View.generateViewId()
                rb.text = option.name
                binding.rgOptions.addView(rb)
                if (option.name == templateModel.options[0].name){
                    rb.isChecked = true
                }
            }
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
        holder.bind(data[position], context)
}