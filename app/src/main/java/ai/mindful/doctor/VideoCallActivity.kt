package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityVideoCallBinding
import ai.mindful.doctor.di.DoctorApplication
import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtm.*
import io.shivamvk.networklibrary.api.ApiService
import javax.inject.Inject

class VideoCallActivity : AppCompatActivity(), View.OnClickListener, RtmClientListener, RtmCallEventListener {

    @Inject lateinit var prefs: SharedPreferences
    @Inject lateinit var apiService: ApiService
    private val PERMISSION_REQ_ID_CAMERA: Int = 975
    private val PERMISSION_REQ_ID_RECORD_AUDIO: Int = 180
    lateinit var binding: ActivityVideoCallBinding
    private var rtcEngine: RtcEngine? = null
    private var rtmClient: RtmClient? = null
    private var rtmCallManager: RtmCallManager? = null
    private var localInvitation: LocalInvitation? = null
    private val rtcEventHandler = object : IRtcEngineEventHandler() {

        // Listen for the onFirstRemoteVideoDecoded callback.
        // This callback occurs when the first video frame of a remote user is received and decoded after the remote user successfully joins the channel.
        // You can call the setupRemoteVideo method in this callback to set up the remote video view.
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread { setupRemoteVideo(uid) }
        }

        // Listen for the onUserOffline callback.
        // This callback occurs when the remote user leaves the channel or drops offline.
        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread { onRemoteUserLeft() }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as DoctorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_call)
        init()
    }

    fun init(){
        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO,
                PERMISSION_REQ_ID_RECORD_AUDIO
            ) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
        ) {
            initAgoraEngineAndJoinChannel()
            binding.btnCall.setOnClickListener(this)
        }
    }

    private fun initializeAgoraEngine() {
        try {
            rtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), rtcEventHandler)
            rtmClient = RtmClient.createInstance(baseContext, getString(R.string.agora_app_id), this)
            rtmClient?.login(null, "16103223shivam", object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) { Log.e("rtm client login", "${p0.toString()} ########") }
                override fun onFailure(p0: ErrorInfo?) { Log.e("rtm client login", "${p0.toString()} ########") }
            })
            rtmCallManager = rtmClient?.rtmCallManager
            rtmCallManager?.setEventListener(this)
            localInvitation = rtmCallManager?.createLocalInvitation("shivamvkinbox")
            rtmCallManager?.sendLocalInvitation(localInvitation, object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) { Log.e("rtm client login", "${p0.toString()} ########") }
                override fun onFailure(p0: ErrorInfo?) { Log.e("rtm client login", "${p0.toString()} ########") }
            })
        } catch (e: Exception) {
            Log.e("VideoCallActivity", Log.getStackTraceString(e))
            throw RuntimeException("NEED TO check rtc sdk init fatal error:\n" + Log.getStackTraceString(e))
        }
    }


    private fun setupRemoteVideo(uid: Int) {
        val count: Int = binding.remoteVideoViewContainer.childCount
        var view: View? = null
        for (i in 0 until count) {
            val v: View = binding.remoteVideoViewContainer.getChildAt(i)
            if (v.tag is Int && v.tag as Int == uid) {
                view = v
            }
        }

        if (view != null) {
            return
        }
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        binding.remoteVideoViewContainer.addView(surfaceView)
        rtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    private fun setupLocalVideo() {
        rtcEngine!!.enableVideo()
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        binding.localVideoViewContainer.addView(surfaceView)
        rtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun joinChannel() {
        rtcEngine!!.joinChannel(null, "asdfghjkl", "Extra Optional Data", 0)
    }

    private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine()
        setupLocalVideo()
        joinChannel()
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
        rtmClient?.logout(null)
    }


    private fun leaveChannel() {
        rtcEngine!!.leaveChannel()
    }

    private fun onRemoteUserLeft(){
        finish()
    }

    override fun onTokenExpired() {

    }

    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
    }

    override fun onConnectionStateChanged(p0: Int, p1: Int) {
    }

    override fun onMessageReceived(p0: RtmMessage?, p1: String?) {
    }

    override fun onRemoteInvitationCanceled(p0: RemoteInvitation?) {

    }

    override fun onRemoteInvitationRefused(p0: RemoteInvitation?) {

    }

    override fun onLocalInvitationFailure(p0: LocalInvitation?, p1: Int) {

    }

    override fun onLocalInvitationRefused(p0: LocalInvitation?, p1: String?) {
        //Toast.makeText(this, "${p1} ####", Toast.LENGTH_SHORT).show()
        runOnUiThread {
            Toast.makeText(this, "call refused", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onLocalInvitationCanceled(p0: LocalInvitation?) {

    }

    override fun onRemoteInvitationFailure(p0: RemoteInvitation?, p1: Int) {

    }

    override fun onLocalInvitationReceivedByPeer(p0: LocalInvitation?) {
        //Toast.makeText(this, "ringing...", Toast.LENGTH_SHORT).show()
        runOnUiThread {
            binding.invitationStatus.text = "Ringing..."
        }
    }

    override fun onRemoteInvitationReceived(p0: RemoteInvitation?) {

    }

    override fun onLocalInvitationAccepted(p0: LocalInvitation?, p1: String?) {

    }

    override fun onRemoteInvitationAccepted(p0: RemoteInvitation?) {

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_call -> {
                rtmCallManager?.cancelLocalInvitation(localInvitation, object: ResultCallback<Void>{
                    override fun onSuccess(p0: Void?) {
                    }
                    override fun onFailure(p0: ErrorInfo?) {
                        Toast.makeText(this@VideoCallActivity, p0?.errorDescription.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
                finish()
            }
        }
    }
}