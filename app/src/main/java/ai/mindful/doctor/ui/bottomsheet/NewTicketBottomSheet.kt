package ai.mindful.doctor.ui.bottomsheet

import ai.mindful.doctor.CSChatActivity
import ai.mindful.doctor.databinding.NewTicketBottomSheetBinding
import ai.mindful.doctor.di.DoctorApplication
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.ticket.TicketResponse
import io.shivamvk.networklibrary.models.BaseModel
import io.shivamvk.networklibrary.utils.CustomProgressDialog
import org.json.JSONObject
import javax.inject.Inject

class NewTicketBottomSheet : BottomSheetDialogFragment(), ApiManagerListener {

    lateinit var binding: NewTicketBottomSheetBinding

    @Inject
    lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity!!.application as DoctorApplication).getDeps().inject(this)
        binding = NewTicketBottomSheetBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.close.setOnClickListener {
            this.dismiss()
        }
        binding.submit.setOnClickListener {
            if (binding.tilMessage.editText?.text.toString().isNullOrEmpty() ||
                binding.tilMessage.editText?.text.toString().length > 50
            ) {
                binding.tilMessage.error = "Please describe your issue in 1-50 letters"
                binding.tilMessage.editText?.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        binding.tilMessage.error = null
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
            } else {
                var jsonObject = JsonObject().apply {
                    this.addProperty("issue", binding.tilMessage.editText?.text.toString())
                }
                ApiManager(
                    ApiRoutes.ticket,
                    apiService,
                    TicketResponse(),
                    this,
                    null
                ).doPOSTAPICall(jsonObject)
                binding.progressBar.visibility = View.VISIBLE
                binding.submit.visibility = View.INVISIBLE
            }
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is TicketResponse) {
            var data = Gson().fromJson(response, TicketResponse::class.java).data
            binding.submit.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            Log.i("ticket", "$response#####");
            startActivity(
                Intent(
                    activity, CSChatActivity::class.java
                ).putExtra("ticket", data)
            )
            this.dismiss()
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
    }
}