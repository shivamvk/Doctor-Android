package ai.mindful.doctor.ui.fragment

import ai.mindful.doctor.R
import ai.mindful.doctor.databinding.FragmentAppointmentBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.adapter.AppointmentAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.appointment.AppointmentModel
import io.shivamvk.networklibrary.model.appointment.AppointmentResponse
import io.shivamvk.networklibrary.models.BaseModel
import javax.inject.Inject

class AppointmentFragment: Fragment(), ApiManagerListener {

    @Inject lateinit var apiService: ApiService
    @Inject lateinit var prefs: SharedPreferences
    lateinit var binding: FragmentAppointmentBinding

    fun init(){
        binding.rvAppointments.layoutManager = LinearLayoutManager(context)
        ApiManager(
            ApiRoutes.getAppointments,
            apiService,
            AppointmentResponse(),
            this,
            null
        ).doGETAPICall()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_appointment, container, false
        )
        (activity!!.application as DoctorApplication).getDeps().inject(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is AppointmentResponse){
            Log.i("appointment", response)
            val data = Gson().fromJson(response, AppointmentResponse::class.java).data
            if (data.isNullOrEmpty()){
                binding.noItemLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            } else {
                binding.noItemLayout.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.itemsLayout.visibility = View.VISIBLE
                binding.rvAppointments.adapter =
                    context?.let { AppointmentAdapter(it, data) }
            }
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(context, "${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}