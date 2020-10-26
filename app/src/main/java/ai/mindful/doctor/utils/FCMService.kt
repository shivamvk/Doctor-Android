package ai.mindful.doctor.utils

import ai.mindful.doctor.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        Log.i("remoteMessage", message?.data.toString() + "###")
        val builder =
            NotificationCompat.Builder(this, message?.data?.get("channel").toString())
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        builder.setSmallIcon(R.drawable.ic_logo)
        builder.setLargeIcon(
            Glide.with(applicationContext)
                .asBitmap()
                .load(R.drawable.ic_user)
                .submit()
                .get()
        )
        builder.setContentTitle(message?.data?.get("title"))
        builder.setContentText(message?.data?.get("body"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    message?.data?.get("channel").toString(),
                    "General notifications",
                    NotificationManager.IMPORTANCE_HIGH
                )
            manager.createNotificationChannel(channel)
        }
        manager.notify(0, builder.build())
    }

    private fun getBitmapfromUri(imageUri: String): Bitmap? {
        val url: URL
        val connection: HttpURLConnection
        val input: InputStream
        var bitmap: Bitmap? = null
        try {
            url = URL(imageUri)
            connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            input = connection.inputStream
            bitmap = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "getBitmapfromUri: $e")
        }
        return bitmap
    }
}