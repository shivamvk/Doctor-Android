package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityCSChatBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.adapter.CSMessagesAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.ticket.CommentModel
import io.shivamvk.networklibrary.model.ticket.TicketModel
import io.shivamvk.networklibrary.model.ticket.TicketResponse
import io.shivamvk.networklibrary.models.BaseModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CSChatActivity : AppCompatActivity(), ApiManagerListener, TextWatcher {

    @Inject lateinit var apiService: ApiService
    lateinit var binding: ActivityCSChatBinding
    lateinit var ticketModel: TicketModel
    var tempCommentModel = CommentModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_c_s_chat)
        ticketModel = intent.getSerializableExtra("ticket") as TicketModel
        Log.i("ticket", ticketModel.toString())
        init()
    }

    fun handleSendMessage(){
        if (binding.message.editText?.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }
        binding.noItemLayout.visibility = View.GONE
        var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        tempCommentModel = CommentModel(sdf.format(Date()), binding.message.editText?.text.toString(), "Patient", false)
        if (binding.rvMessages.adapter == null){
            binding.rvMessages.adapter = CSMessagesAdapter(this, mutableListOf(tempCommentModel))
        } else {
            (binding.rvMessages.adapter as CSMessagesAdapter).addCommentTemp(tempCommentModel)
        }
        ApiManager(
            ApiRoutes.postCommentToTicket(ticketModel._id),
            apiService,
            TicketResponse(),
            this,
            null
        ).doPUTAPICall(JsonObject().apply {
            this.addProperty("comment", binding.message.editText?.text.toString())
        })
        binding.message.editText?.setText("")
    }

    fun init(){
        binding.ticketId.text = ticketModel.ticket_id
        binding.message.isEndIconVisible = false
        binding.message.editText?.addTextChangedListener(this)
        binding.issue.text = ticketModel.issue
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.rvMessages.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom<oldBottom && binding.rvMessages.adapter != null){
                binding.rvMessages.postDelayed(Runnable {
                    binding.rvMessages.smoothScrollToPosition(
                        (binding.rvMessages.adapter as CSMessagesAdapter).itemCount
                    )
                }, 100)
            }
        }
        updateComments(ticketModel)
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is TicketResponse){
            Log.i("ticket", response)
            var data = Gson().fromJson(response, TicketResponse::class.java).data
            (binding.rvMessages.adapter as CSMessagesAdapter).addComment(tempCommentModel)
        }
    }

    private fun updateComments(ticketModel: TicketModel?) {
        var comments = ticketModel?.comments
        if (comments?.isEmpty()!!){
            return
        } else {
            binding.rvMessages.adapter = CSMessagesAdapter(this,
                comments as MutableList<CommentModel>
            )
            binding.noItemLayout.visibility = View.GONE
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(this, e.localizedMessage + ". Please try again", Toast.LENGTH_SHORT).show()
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isEmpty()){
            binding.message.isEndIconVisible = false
            binding.message.setEndIconOnClickListener(null)
        } else {
            binding.message.isEndIconVisible = true
            binding.message.setEndIconOnClickListener {
                handleSendMessage()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}