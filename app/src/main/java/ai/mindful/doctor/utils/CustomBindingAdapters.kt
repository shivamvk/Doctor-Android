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

    fun readableStringFromISO(string: String): String {
        return try{
            var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            var date = gmtToLocalDate(sdf.parse(string))
            SimpleDateFormat("MM-dd-yyyy").format(date)
        } catch (e: Exception){
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    @BindingAdapter("ticketDateFromISO")
    fun setTicketDateFromIso(textView: TextView, string: String){
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date? = gmtToLocalDate(iso.parse(string))
        val month = SimpleDateFormat("dd MMM").format(date)
        val day = SimpleDateFormat("hh:mm aa").format(date)
        textView.setText("${month}, ${day}")
    }

    @JvmStatic
    @BindingAdapter("restrictLengthTo25")
    fun restrictLengthTo25(textView: TextView, string: String){
        if (string.length < 25){
            textView.text = string
            return
        }
        textView.text = "${string.substring(0, 25)}..."
    }
}