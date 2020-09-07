package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.databinding.QnaLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.shivamvk.networklibrary.model.appointment.SymptomsQuestionAnswerModel

class QnaAdapter(val context: Context, val data: List<SymptomsQuestionAnswerModel>): RecyclerView.Adapter<QnaAdapter.ViewHolder>() {

    class ViewHolder(val binding: QnaLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
            symptomsQuestionAnswerModel: SymptomsQuestionAnswerModel,
            context: Context
        ) {
            binding.question.text = symptomsQuestionAnswerModel.question
            val lm = FlexboxLayoutManager(context)
            lm.flexDirection = FlexDirection.ROW
            lm.justifyContent = JustifyContent.FLEX_START
            binding.rvAnswers.layoutManager = lm
            binding.rvAnswers.adapter = PillAdapter(context, symptomsQuestionAnswerModel.answers)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QnaLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], context)
}