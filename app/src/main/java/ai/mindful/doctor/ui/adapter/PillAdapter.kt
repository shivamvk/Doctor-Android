package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.PillLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PillAdapter(val context: Context, val data: ArrayList<String>): RecyclerView.Adapter<PillAdapter.ViewHolder>() {

    class ViewHolder(val binding: PillLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(s: String) {
            binding.text.text = s
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = PillLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position])
}