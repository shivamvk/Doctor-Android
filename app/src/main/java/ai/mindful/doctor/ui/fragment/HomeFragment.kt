package ai.mindful.doctor.ui.fragment

import ai.mindful.doctor.EditProfileActivity
import ai.mindful.doctor.MainActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.mindful.doctor.R
import ai.mindful.doctor.VideoCallActivity
import ai.mindful.doctor.databinding.FragmentHomeBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.viewmodel.HomeFragmentViewModel
import ai.mindful.doctor.utils.ViewModelFactory
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import io.shivamvk.networklibrary.BuildConfig
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.banner.BannerResponse
import io.shivamvk.networklibrary.models.BaseModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.OnItemClickListener
import java.net.URLEncoder
import javax.inject.Inject


class HomeFragment : Fragment(), ApiManagerListener {

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
        ApiManager(
            ApiRoutes.getHomeBanners,
            apiService,
            BannerResponse(),
            this,
            null
        ).doGETAPICall()
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
        binding.call.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    VideoCallActivity::class.java
                )
            )
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is BannerResponse) {
            var data = Gson().fromJson(response, BannerResponse::class.java).data
            carousel1List.clear()
            for (banner in data!!) {
                carousel1List.add(
                    CarouselItem(
                        imageUrl = "${BuildConfig.AWSURL}${banner.image}"
                    )
                )
            }
            binding.carousel1.addData(carousel1List)
            binding.carousel1.onItemClickListener = object : OnItemClickListener{
                override fun onClick(position: Int, carouselItem: CarouselItem) {
                    if (data[position].weblink){
                        var url = ""
                        if (!data[position].url.startsWith("http")){
                            url = "http://${data[position].url}"
                        } else {
                            url = data[position].url
                        }
                        startActivity(Intent(
                            Intent.ACTION_VIEW
                        ).setData
                        (Uri.parse(url)))
                    }
                }
                override fun onLongClick(position: Int, dataObject: CarouselItem) {}

            }
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        Toast.makeText(context, "${e.localizedMessage}. Please try again!", Toast.LENGTH_SHORT)
            .show()
    }
}