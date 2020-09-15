package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.R
import ai.mindful.doctor.databinding.CSMessageListItemBinding
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.ticket.CommentModel

class CSMessagesAdapter(val context: Context, val data: MutableList<CommentModel>) :
    RecyclerView.Adapter<CSMessagesAdapter.ViewHolder>() {

    class ViewHolder(val binding: CSMessageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            commentModel: CommentModel,
            context: Context
        ) {
            binding.comment = commentModel
            binding.message.text = commentModel.comment
            binding.time.text = commentModel.at
            if (commentModel.sent) {
                binding.status.setColorFilter(
                    ContextCompat.getColor(context, R.color.secondary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                binding.status.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorLightGrey
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            if (commentModel.by == "Patient") {
                (binding.layout.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                binding.layout.background =
                    context.resources.getDrawable(R.drawable.message_bg_send)
            } else {
                binding.message.setTextColor(context.resources.getColor(R.color.black))
                binding.time.setTextColor(context.resources.getColor(R.color.black))
                var llStatuslp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                llStatuslp.leftMargin = 30
                llStatuslp.rightMargin = 30
                llStatuslp.gravity = Gravity.LEFT
                var mlp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                mlp.setMargins(40, 6, 40, 6)
                binding.message.layoutParams = mlp
                binding.status.visibility = View.GONE
                binding.llStatus.layoutParams = llStatuslp
                (binding.layout.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                binding.layout.background =
                    context.resources.getDrawable(R.drawable.message_bg_receive)
            }
        }
    }

    fun addCommentTemp(commentModel: CommentModel) {
        data.add(commentModel)
        notifyDataSetChanged()
    }

    fun addComment(commentModel: CommentModel) {
        commentModel.sent = true
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CSMessageListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], context)
}