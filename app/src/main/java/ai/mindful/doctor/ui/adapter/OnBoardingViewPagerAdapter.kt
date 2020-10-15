package ai.mindful.doctor.ui.adapter

import ai.mindful.doctor.R
import ai.mindful.doctor.utils.ClientPrefs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView

class OnBoardingViewPagerAdapter : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.onboarding_page_1, container, false)
        when(position){
            0 -> {
                view.findViewById<LottieAnimationView>(R.id.anim).setAnimation(io.shivamvk.networklibrary.R.raw.onboarding_anim_1)
                view.findViewById<TextView>(R.id.heading).text = "Welcome to ${ClientPrefs.clientName}"
                view.findViewById<TextView>(R.id.subheading).text = "Register as an expert with us and get connected with users all across the globe"
            }
            1 -> {
                view.findViewById<LottieAnimationView>(R.id.anim).setAnimation(io.shivamvk.networklibrary.R.raw.onboarding_anim_2)
                view.findViewById<TextView>(R.id.heading).text = "Instant appointment"
                view.findViewById<TextView>(R.id.subheading).text = "Set up profile, link bank account and begin with getting bookings instantly"
            }
            2 -> {
                view.findViewById<LottieAnimationView>(R.id.anim).setAnimation(io.shivamvk.networklibrary.R.raw.onboarding_anim_3)
                view.findViewById<TextView>(R.id.heading).text = "Easy access"
                view.findViewById<TextView>(R.id.subheading).text = "Manage schedule on the go, consult users online and maximize  your earnings"
            }
        }
        container.addView(view, position)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = 3
}