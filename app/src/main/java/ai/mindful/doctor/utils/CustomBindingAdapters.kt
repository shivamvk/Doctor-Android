package ai.mindful.doctor.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

object CustomBindingAdapters {

    @JvmStatic
    fun gmtToLocalDate(date: Date): Date? {
        val timeZone = Calendar.getInstance().timeZone.id
        return Date(
            date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time)
        )
    }

    @JvmStatic
    @BindingAdapter("isoToReadableString")
    fun setReadableStringFromIso(textview: TextView, string: String) {
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date? = gmtToLocalDate(iso.parse(string))
        val month = SimpleDateFormat("dd MMMM").format(date)
        val day = SimpleDateFormat("EEEE").format(date)
        textview.setText("${day}, ${month}")
    }
}