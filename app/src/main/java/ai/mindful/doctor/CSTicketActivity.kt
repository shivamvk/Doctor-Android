package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityCSTicketBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.adapter.TicketAdapter
import ai.mindful.doctor.ui.bottomsheet.NewTicketBottomSheet
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.ticket.TicketModel
import io.shivamvk.networklibrary.model.ticket.TicketsResponse
import io.shivamvk.networklibrary.models.BaseModel
import javax.inject.Inject

class CSTicketActivity : AppCompatActivity(), ApiManagerListener {

    @Inject lateinit var apiService: ApiService
    lateinit var binding: ActivityCSTicketBinding
    var openTickets: MutableList<TicketModel> = ArrayList<TicketModel>()
    var closedTickets: MutableList<TicketModel> = ArrayList<TicketModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_c_s_ticket)
        supportActionBar?.hide()
//        binding.addNewTicket.setOnClickListener {
//
//        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        ApiManager(
            ApiRoutes.ticket,
            apiService,
            TicketsResponse(),
            this,
            null
        ).doGETAPICall()
    }

    fun addnewTicket(view: View){
        var newTicketBS = NewTicketBottomSheet()
        newTicketBS.show(supportFragmentManager, "New Ticket")
//        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
    }

    fun setUpTickets(){
        if (!openTickets.isNullOrEmpty()){
            binding.rvOpenTickets.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvOpenTickets.adapter = TicketAdapter(this, openTickets)
            binding.noOpenTickets.visibility = View.GONE
        }
        if (!closedTickets.isNullOrEmpty()){
            binding.rvClosedTickets.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvClosedTickets.adapter = TicketAdapter(this, closedTickets)
            binding.noClosedTickets.visibility = View.GONE
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is TicketsResponse){
            var data = Gson().fromJson(response, TicketsResponse::class.java).data
            if (data.isNullOrEmpty()){
                return
            }
            data.forEach {
                if (it.status == "New" || it.status == "Open"){
                    openTickets.add(it)
                } else {
                    closedTickets.add(it)
                }
            }
            setUpTickets()
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(this, e.localizedMessage + ". Please try again", Toast.LENGTH_SHORT).show()
    }
}