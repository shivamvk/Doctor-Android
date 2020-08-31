package ai.mindful.doctor.ui.fragment

import ai.mindful.doctor.EditProfileActivity
import ai.mindful.doctor.MainActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.mindful.doctor.R
import ai.mindful.doctor.databinding.FragmentHomeBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.viewmodel.HomeFragmentViewModel
import ai.mindful.doctor.utils.ViewModelFactory
import android.content.Intent
import android.content.SharedPreferences
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import io.shivamvk.networklibrary.api.ApiService
import kotlinx.android.synthetic.main.fragment_home.*
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import javax.inject.Inject


class HomeFragment : Fragment() {

    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var prefs: SharedPreferences
    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: HomeFragmentViewModel
    val carousel1List = mutableListOf<CarouselItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity!!.application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(apiService, activity!!, prefs)
        ).get(HomeFragmentViewModel::class.java)
        binding.appointment.setOnClickListener {
            (context as MainActivity).goto(AppointmentFragment())
        }
        binding.profile.setOnClickListener {
            startActivity(
                Intent(
                    context, EditProfileActivity::class.java
                )
            )
        }
        binding.wallet.setOnClickListener {
            (context as MainActivity).goto(WalletFragment())
        }
        binding.help.setOnClickListener {
            (context as MainActivity).openGmail()
        }
        for (i in 1..10) {
            if (i % 2 == 0) {
                carousel1List.add(
                    CarouselItem(
                        imageUrl = "https://images.unsplash.com/photo-1576091160550-2173dba999ef?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80"
                    )
                )
            } else {
                carousel1List.add(
                    CarouselItem(
                        imageUrl = "https://images.unsplash.com/photo-1557825835-a526494be845?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80"
                    )
                )
            }
        }
        binding.carousel1.addData(carousel1List)
        binding.activeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Snackbar.make(active_switch, "You are now marked active!", Snackbar.LENGTH_SHORT)
                    .setAction("Close") {}
                    .show()
            } else {
                Snackbar.make(active_switch, "You are now marked inactive!", Snackbar.LENGTH_SHORT)
                    .setAction("Close") {}
                    .show()
            }
        }
    }
}