package ai.mindful.doctor.ui.bottomsheet

import ai.mindful.doctor.R
import ai.mindful.doctor.databinding.TemplateBottomSheetBinding
import ai.mindful.doctor.ui.adapter.PillAdapter
import ai.mindful.doctor.ui.adapter.QnaAdapter
import ai.mindful.doctor.ui.adapter.TemplateExamAdapter
import ai.mindful.doctor.ui.adapter.TemplateRosAdapter
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.shivamvk.networklibrary.model.appointment.AppointmentModel
import io.shivamvk.networklibrary.model.appointment.RosExamBookingPutModel
import io.shivamvk.networklibrary.model.appointment.TemplateModel


class TemplateBottomSheet(
    val appointmentModel: AppointmentModel,
    val rosList: ArrayList<TemplateModel>,
    val examList: ArrayList<TemplateModel>,
    var rosExamBookingPutModel: RosExamBookingPutModel
): BottomSheetDialogFragment() {

    lateinit var binding: TemplateBottomSheetBinding
    val allowedTemplateTypes =
        arrayOf("ENT", "Gastrointestinal", "Nephrology", "Musculoskeletal")

    fun init(){
        Log.i("ros", "shivamvk##### ${rosList.toString()}")
        Log.i("exam", "shivamvk#### ${examList.toString()}")
        binding.booking = appointmentModel
        var cmlm = FlexboxLayoutManager(context)
        cmlm.flexDirection = FlexDirection.ROW
        cmlm.justifyContent = JustifyContent.FLEX_START
        binding.rvCurrentMedication.layoutManager = cmlm
        binding.rvCurrentMedication.adapter =
            appointmentModel.patient?.history?.currentMedication?.let { PillAdapter(context!!, it) }
        if (appointmentModel.patient?.history?.currentMedication.isNullOrEmpty()){
            binding.cmText.visibility = View.GONE
        }

        var alm = FlexboxLayoutManager(context)
        alm.flexDirection = FlexDirection.ROW
        alm.justifyContent = JustifyContent.FLEX_START
        binding.rvAllergies.layoutManager = alm
        binding.rvAllergies.adapter =
            appointmentModel.patient?.history?.allergies?.let { PillAdapter(context!!, it) }
        if (appointmentModel.patient?.history?.allergies.isNullOrEmpty()){
            binding.aText.visibility = View.GONE
        }

        var slm = FlexboxLayoutManager(context)
        slm.flexDirection = FlexDirection.ROW
        slm.justifyContent = JustifyContent.FLEX_START
        binding.rvSurgeries.layoutManager = slm
        binding.rvSurgeries.adapter =
            appointmentModel.patient?.history?.surgeries?.let { PillAdapter(context!!, it) }
        if (appointmentModel.patient?.history?.surgeries.isNullOrEmpty()){
            binding.sText.visibility = View.GONE
        }

        var mplm = FlexboxLayoutManager(context)
        mplm.flexDirection = FlexDirection.ROW
        mplm.justifyContent = JustifyContent.FLEX_START
        binding.rvMedicalProblems.layoutManager = mplm
        binding.rvMedicalProblems.adapter =
            appointmentModel.patient?.history?.medicalProblems?.let { PillAdapter(context!!, it) }
        if (appointmentModel.patient?.history?.medicalProblems.isNullOrEmpty()){
            binding.mpText.visibility = View.GONE
        }

        if (appointmentModel.patient?.history?.smoking!!){
            binding.smokeDetails.text = "Social history: Is an active smoker? Yes"
        } else {
            binding.smokeDetails.text = "Social history: Is an active smoker? No"
        }

        binding.rvQna.layoutManager = LinearLayoutManager(context)
        binding.rvQna.adapter =
            appointmentModel.symptoms?.get(0)?.questions?.let { QnaAdapter(context!!, it) }

        val adapter = ArrayAdapter(
            context!!,
            R.layout.support_simple_spinner_dropdown_item,
            allowedTemplateTypes
        )
        (binding.templateType.editText as AutoCompleteTextView).setAdapter(adapter)
        if (appointmentModel.templateType.isNotEmpty()){
            binding.templateType.editText?.setText(appointmentModel.templateType)
        }
        binding.templateType.visibility = View.GONE
        binding.rvPhysicalExam.layoutManager = LinearLayoutManager(context)
        binding.rvPhysicalExam.adapter = TemplateExamAdapter(context!!, examList, rosExamBookingPutModel)

        binding.rvRos.layoutManager = LinearLayoutManager(context)
        binding.rvRos.adapter = TemplateRosAdapter(context!!, rosList, rosExamBookingPutModel)

        binding.save.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (binding.templateType.editText?.text.toString().isNotEmpty() &&
                allowedTemplateTypes.contains(binding.templateType.editText?.text.toString())){
            appointmentModel.templateType = binding.templateType.editText?.text.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.template_bottom_sheet, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
}