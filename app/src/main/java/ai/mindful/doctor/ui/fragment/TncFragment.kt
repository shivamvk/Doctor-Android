package ai.mindful.doctor.ui.fragment

import ai.mindful.doctor.R
import ai.mindful.doctor.databinding.FragmentTncBinding
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class TncFragment: Fragment() {
    lateinit var binding: FragmentTncBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_tnc, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.text.text = Html.fromHtml(getString(R.string.eazemeup_tnc))
    }
}