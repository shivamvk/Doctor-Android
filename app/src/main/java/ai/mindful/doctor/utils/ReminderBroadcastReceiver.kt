package ai.mindful.doctor.utils

import ai.mindful.doctor.MainActivity
import ai.mindful.doctor.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("alarm", "received")
        val intent = Intent(context, MainActivity::class.java)
        val mainActivityIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, 0)

        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var builder = context?.let {
            NotificationCompat.Builder(it, "general")
                .setSmallIcon(R.drawable.ic_logo)
//                .setLargeIcon(
//                    Glide.with(context)
//                    .asBitmap()
//                    .load(R.drawable.ic_logo)
//                    .submit()
//                    .get())
                .setContentTitle("Are you still active?")
                .setContentText("Please turn the switch off if your are not active anymore!")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Please turn the switch off if your are not active anymore!"))
                .setContentIntent(mainActivityIntent)
                .addAction(R.drawable.ic_home, "Turn off",
                    mainActivityIntent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("general","General", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        Log.i("alarm", "sent")
        manager.notify(457, builder?.build())
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