package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.RosExamItemLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.shivamvk.networklibrary.model.appointment.RosExamModel

class RosExamAdapter(val context: Context, var data: List<RosExamModel>): RecyclerView.Adapter<RosExamAdapter.ViewHolder>() {

    class ViewHolder(val binding: RosExamItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(context: Context, rosExamModel: RosExamModel) {
            binding.data = rosExamModel
            var lm = FlexboxLayoutManager(context)
            lm.flexDirection = FlexDirection.ROW
            lm.justifyContent = JustifyContent.FLEX_START
            binding.rvAnswers.layoutManager = lm
            var list = ArrayList<String>()
            rosExamModel.answers?.forEach {
                list.add(it.option!!)
            }
            binding.rvAnswers.adapter = PillAdapter(context, list)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RosExamItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(context, data[position])

    override fun getItemCount(): Int =
        data.size
}