package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.CSChatActivity
import ai.mindful.doctor.databinding.TicketLayoutBinding
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.shivamvk.networklibrary.model.ticket.TicketModel

class TicketAdapter(val context: Context, val data: List<TicketModel>): RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    class ViewHolder(val binding: TicketLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
            ticketModel: TicketModel,
            context: Context
        ) {
            binding.ticket = ticketModel
            binding.layout.setOnClickListener {
                context.startActivity(Intent(
                    context, CSChatActivity::class.java
                ).putExtra("ticket", ticketModel))
            }
            if (ticketModel.status != "New"){
                binding.newTicket.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = TicketLayoutBinding.inflate(
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