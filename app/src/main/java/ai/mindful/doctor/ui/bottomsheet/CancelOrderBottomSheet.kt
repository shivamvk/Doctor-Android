package ai.mindful.doctor.ui.bottomsheet

import ai.mindful.doctor.AppointmentDetailActivity
import ai.mindful.doctor.R
import ai.mindful.doctor.databinding.CancelOrderBottomSheetBinding
import ai.mindful.doctor.di.DoctorApplication
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.models.BaseModel
import javax.inject.Inject

class CancelOrderBottomSheet(val _id: String) : BottomSheetDialogFragment(), ApiManagerListener {

    lateinit var binding: CancelOrderBottomSheetBinding
    @Inject lateinit var apiService: ApiService
    var cancelled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.cancel_order_bottom_sheet, container, false
        )
        (activity!!.application as DoctorApplication).getDeps().inject(this)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.close.setOnClickListener {
            this.dismiss()
        }
        binding.cancel.setOnClickListener {
            cancelled = true
            ApiManager(
                ApiRoutes.cancelAppointment(_id),
                apiService,
                UtilModel(),
                this,
                null
            ).doPUTAPICall(JsonObject().apply { addProperty("reason", binding.etReason.editText?.text.toString()) })
            binding.cancel.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (cancelled){
            (activity as AppointmentDetailActivity).cancelOrder()
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        this.dismiss()
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        e.printStackTrace()
        binding.cancel.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }
}