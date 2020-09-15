package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.TemplateRosItemLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.appointment.TemplateModel

class TemplateRosAdapter(val context: Context, val data: List<TemplateModel>) :
    RecyclerView.Adapter<TemplateRosAdapter.ViewHolder>() {

    class ViewHolder(val binding: TemplateRosItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            templateModel: TemplateModel,
            context: Context
        ) {
            binding.template = templateModel
            binding.rvOptions.layoutManager = LinearLayoutManager(context)
            binding.rvOptions.adapter = RosOptionAdapter(context, templateModel.options)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TemplateRosItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], context)

}