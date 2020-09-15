package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.RosOptionItemLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.appointment.OptionsModel

class RosOptionAdapter(val context: Context, val data: List<OptionsModel>): RecyclerView.Adapter<RosOptionAdapter.ViewHolder>() {

    class ViewHolder(val binding: RosOptionItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
            optionsModel: OptionsModel,
            context: Context
        ){
            binding.option = optionsModel
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
        holder.bind(data[position], context)

}