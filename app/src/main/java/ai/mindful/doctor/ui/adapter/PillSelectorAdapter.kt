package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.R
import ai.mindful.doctor.databinding.PillLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.appointment.LanguageModel
import io.shivamvk.networklibrary.model.appointment.SimpleSymptomModel

class PillSelectorAdapter<T>(
    var selectedItems: MutableList<String>,
    val totalItems: List<T>,
    val context: Context
) : RecyclerView.Adapter<PillSelectorAdapter.ViewHolder>() {

    class ViewHolder(val binding: PillLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PillLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (totalItems[position] is LanguageModel) {
            holder.binding.text.text = (totalItems[position] as LanguageModel).name
            if (selectedItems.contains((totalItems[position] as LanguageModel)._id)){
                holder.binding.edit.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                holder.binding.text.setTextColor(context.resources.getColor(R.color.white))
            }
            holder.binding.edit.setOnClickListener {
                if (selectedItems.contains((totalItems[position] as LanguageModel)._id)) {
                    selectedItems.remove((totalItems[position] as LanguageModel)._id)
                    holder.binding.edit.setCardBackgroundColor(context.resources.getColor(R.color.colorLightGrey))
                    holder.binding.text.setTextColor(context.resources.getColor(R.color.black))
                } else {
                    selectedItems.add((totalItems[position] as LanguageModel)._id)
                    holder.binding.edit.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                    holder.binding.text.setTextColor(context.resources.getColor(R.color.white))
                }
            }
        } else if (totalItems[position] is SimpleSymptomModel){
            holder.binding.text.text = (totalItems[position] as SimpleSymptomModel).name
            if (selectedItems.contains((totalItems[position] as SimpleSymptomModel)._id)){
                holder.binding.edit.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                holder.binding.text.setTextColor(context.resources.getColor(R.color.white))
            }
            holder.binding.edit.setOnClickListener {
                if (selectedItems.contains((totalItems[position] as SimpleSymptomModel)._id)) {
                    selectedItems.remove((totalItems[position] as SimpleSymptomModel)._id)
                    holder.binding.edit.setCardBackgroundColor(context.resources.getColor(R.color.colorLightGrey))
                    holder.binding.text.setTextColor(context.resources.getColor(R.color.black))
                } else {
                    selectedItems.add((totalItems[position] as SimpleSymptomModel)._id!!)
                    holder.binding.edit.setCardBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                    holder.binding.text.setTextColor(context.resources.getColor(R.color.white))
                }
            }
        }
    }

    override fun getItemCount(): Int = totalItems.size
}