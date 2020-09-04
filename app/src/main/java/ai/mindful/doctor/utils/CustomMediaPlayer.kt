package ai.mindful.doctor.utils

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri

public class CustomMediaPlayer(context: Context){

    var ringtone: Ringtone? = null
    var context = context

    fun playRingtone(){
        try {
            val call: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(context, call)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopRingtone(){
        if (ringtone == null){
            return
        }
        ringtone?.stop()
    }

    fun playCallingSound(){

    }
}