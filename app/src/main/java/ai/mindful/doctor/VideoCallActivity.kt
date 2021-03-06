package ai.mindful.doctor

import ai.mindful.doctor.databinding.ActivityVideoCallBinding
import ai.mindful.doctor.di.DoctorApplication
import ai.mindful.doctor.ui.bottomsheet.TemplateBottomSheet
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtm.*
import io.shivamvk.networklibrary.api.ApiManager
import io.shivamvk.networklibrary.api.ApiManagerListener
import io.shivamvk.networklibrary.api.ApiRoutes
import io.shivamvk.networklibrary.api.ApiService
import io.shivamvk.networklibrary.model.UtilModel
import io.shivamvk.networklibrary.model.appointment.*
import io.shivamvk.networklibrary.model.callasessment.CallAsessmentModel
import io.shivamvk.networklibrary.models.BaseModel
import io.shivamvk.networklibrary.sharedprefs.SharedPrefKeys
import io.shivamvk.networklibrary.sharedprefs.PreferencesHelper.get
import javax.inject.Inject

class VideoCallActivity : AppCompatActivity(), View.OnClickListener, RtmClientListener,
    RtmCallEventListener, ApiManagerListener {

    private val PERMISSION_ALL_REQUEST_CODE: Int = 672
    @Inject
    lateinit var prefs: SharedPreferences
    @Inject
    lateinit var apiService: ApiService
    private val PERMISSION_REQ_ID_CAMERA: Int = 975
    private val PERMISSION_REQ_ID_RECORD_AUDIO: Int = 180
    var callStarted: Boolean = false
    var seconds: Long = 0
    lateinit var binding: ActivityVideoCallBinding
    private var rtcEngine: RtcEngine? = null
    private var rtmClient: RtmClient? = null
    private var rtmCallManager: RtmCallManager? = null
    private var localInvitation: LocalInvitation? = null
    private var mediaPlayer: MediaPlayer? = null
    lateinit var appointmentModel: AppointmentModel
    lateinit var templateBottomSheet: TemplateBottomSheet
    var rosList = ArrayList<TemplateModel>()
    var examList = ArrayList<TemplateModel>()
    lateinit var rosExamBookingPutModel: RosExamBookingPutModel
    var callAsessmentModel = CallAsessmentModel()
    private val rtcEventHandler = object : IRtcEngineEventHandler() {

        // Listen for the onFirstRemoteVideoDecoded callback.
        // This callback occurs when the first video frame of a remote user is received and decoded after the remote user successfully joins the channel.
        // You can call the setupRemoteVideo method in this callback to set up the remote video view.
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
            }
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
        appointmentModel = intent.getSerializableExtra("appointmentModel") as AppointmentModel
        init()
    }

    fun init() {
        val PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL_REQUEST_CODE)
        } else {
            permissionGrantedInitEverything()
        }
    }

    private fun permissionGrantedInitEverything() {
        initAgoraEngineAndJoinChannel()
        sendNotificationToPatient()
        binding.btnCall.setOnClickListener(this)
        binding.btnMute.setOnClickListener(this)
        binding.btnMute.isSelected = false
        binding.btnSwitchCamera.isSelected = false
        binding.btnSwitchCamera.setOnClickListener(this)
        binding.booking = appointmentModel
        rosExamBookingPutModel = RosExamBookingPutModel()
        ApiManager(
            ApiRoutes.questions(appointmentModel.symptoms?.get(0)?.symptomId!!, "ROS"),
            apiService,
            TemplateRosResponse(),
            this,
            null
        ).doGETAPICall()
        Log.i("shivamvk####", ApiRoutes.questions(appointmentModel.symptoms?.get(0)?.symptomId!!, "Examination"))
        ApiManager(
            ApiRoutes.questions(appointmentModel.symptoms?.get(0)?.symptomId!!, "Examination"),
            apiService,
            TemplateExamResponse(),
            this,
            null
        ).doGETAPICall()
        binding.remoteVideoViewContainer.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP){
                if (templateBottomSheet != null) {
                    if (!templateBottomSheet.isAdded){
                        showBottomSheetTemplates()
                    }
                }
            }
            true
        }
    }

    private fun showBottomSheetTemplates() {
        binding.localVideoViewContainer.visibility = View.INVISIBLE
        binding.btnSwitchCamera.visibility = View.INVISIBLE
        binding.btnMute.visibility = View.INVISIBLE
        binding.btnCall.visibility = View.INVISIBLE
        binding.timer.visibility = View.INVISIBLE
        binding.controlPanel.visibility = View.INVISIBLE
        binding.screen.layoutParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (450*resources.displayMetrics.density+0.5f).toInt())
        Log.i("size reduced", "true")
        templateBottomSheet.show(supportFragmentManager, "templates")
    }

    fun hideTemplateSheet(){
        binding.screen.layoutParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        Log.i("size expanded", "true")
        binding.localVideoViewContainer.visibility = View.VISIBLE
        binding.btnSwitchCamera.visibility = View.VISIBLE
        binding.btnMute.visibility = View.VISIBLE
        binding.btnCall.visibility = View.VISIBLE
        binding.timer.visibility = View.VISIBLE
        binding.controlPanel.visibility = View.VISIBLE
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ALL_REQUEST_CODE &&
            grantResults.size != permissions.size){
            Toast.makeText(this, "All permissions are required to connect call", Toast.LENGTH_SHORT).show()
        } else {
            permissionGrantedInitEverything()
        }
    }

    private fun sendNotificationToPatient() {
        ApiManager(
            ApiRoutes.call(intent.getStringExtra("calleeId")),
            apiService,
            UtilModel(),
            this,
            null
        ).doPOSTAPICall(JsonObject().apply {
            addProperty("booking", appointmentModel._id)
        })
    }

    private fun initializeAgoraEngine() {
        try {
            rtcEngine =
                RtcEngine.create(baseContext, getString(R.string.agora_app_id), rtcEventHandler)
            rtmClient =
                RtmClient.createInstance(baseContext, getString(R.string.agora_app_id), this)
            rtmClient?.login(
                null,
                prefs[SharedPrefKeys.USER_ID.toString()],
                object : ResultCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        Log.e("rtm client login", "${p0.toString()} ########")
                    }

                    override fun onFailure(p0: ErrorInfo?) {
                        Log.e("rtm client login", "${p0.toString()} ########")
                    }
                })
            rtmCallManager = rtmClient?.rtmCallManager
            rtmCallManager?.setEventListener(this)
            localInvitation =
                rtmCallManager?.createLocalInvitation(intent.getStringExtra("calleeId"))
            rtmCallManager?.sendLocalInvitation(localInvitation, object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    Log.e("rtm client login", "${p0.toString()} ########")
                }

                override fun onFailure(p0: ErrorInfo?) {
                    Log.e("rtm client login", "${p0.toString()} ########")
                }
            })
        } catch (e: Exception) {
            Log.e("VideoCallActivity", Log.getStackTraceString(e))
            throw RuntimeException(
                "NEED TO check rtc sdk init fatal error:\n" + Log.getStackTraceString(
                    e
                )
            )
        }
    }


    private fun setupRemoteVideo(uid: Int) {
        mediaPlayer?.stop()
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
        callStarted = true
        startTimer()
    }

    fun startTimer(){
        Handler().postDelayed(
            {
                if (callStarted){
                    seconds++
                    var m = ""
                    var s = ""
                    if (seconds < 60){
                        m = "00"
                        s = seconds.toString()
                    } else {
                        m = (seconds/60).toString()
                        if (m.length == 1){
                            m = "0${m}"
                        }
                        s = (seconds%60).toString()
                    }
                    if (seconds < 10){
                        s = "0${seconds}"
                    }
                    binding.timer.text = "${m}:${s}"
                    startTimer()
                }
            }, 1000
        )
    }

    private fun setupLocalVideo() {
        rtcEngine!!.enableVideo()
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        binding.localVideoViewContainer.addView(surfaceView)
        rtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun joinChannel() {
        rtcEngine!!.joinChannel(null, "asdfghjkl", "Extra Optional io.shivamvk.networklibrary.model.profile.Data", 0)
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
        mediaPlayer?.stop()
        if (callStarted){
            startActivity(Intent(
                this, TakeNoteActivity::class.java
            ).putExtra("callAssessment", callAsessmentModel)
                .putExtra("appointmentId", appointmentModel._id))
        }
    }
    
    private fun leaveChannel() {
        rtcEngine!!.leaveChannel()
    }

    private fun onRemoteUserLeft() {
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
            mediaPlayer = MediaPlayer.create(this, io.shivamvk.networklibrary.R.raw.tring_tring)
            mediaPlayer?.setVolume(1f, 1f)
            mediaPlayer?.start()

        }
    }

    override fun onRemoteInvitationReceived(p0: RemoteInvitation?) {

    }

    override fun onLocalInvitationAccepted(p0: LocalInvitation?, p1: String?) {

    }

    override fun onRemoteInvitationAccepted(p0: RemoteInvitation?) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_call -> {
                rtmCallManager?.cancelLocalInvitation(
                    localInvitation,
                    object : ResultCallback<Void> {
                        override fun onSuccess(p0: Void?) {
                        }

                        override fun onFailure(p0: ErrorInfo?) {
                            Toast.makeText(
                                this@VideoCallActivity,
                                p0?.errorDescription.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                finish()
            }
            R.id.btn_mute -> {
                binding.btnMute.isSelected = !binding.btnMute.isSelected
                rtcEngine!!.muteLocalAudioStream(binding.btnMute.isSelected)
            }
            R.id.btn_switch_camera -> {
                binding.btnSwitchCamera.isSelected = !binding.btnSwitchCamera.isSelected
                rtcEngine!!.switchCamera()
            }
//            R.id.back_from_parent -> {
//                MaterialAlertDialogBuilder(this)
//                    .setTitle("End call?")
//                    .setPositiveButton("End", DialogInterface.OnClickListener { dialog, _ ->
//                        dialog.cancel()
//                        finish()
//                    })
//                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
//                        dialog.cancel()
//                    })
//                    .show()
//            }
        }
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        when(dataModel){
            is TemplateRosResponse -> {
                Log.i("ros$$###", response)
                rosList = Gson().fromJson(response, TemplateRosResponse::class.java).data
                if (examList.isNotEmpty()){
                    templateBottomSheet = TemplateBottomSheet(appointmentModel, rosList, examList, callAsessmentModel)
                    setBehaviuor()
                }
            }
            is TemplateExamResponse -> {
                Log.i("ros$$###", response)
                examList = Gson().fromJson(response, TemplateExamResponse::class.java).data
                if (examList.isNotEmpty()){
                    templateBottomSheet = TemplateBottomSheet(appointmentModel, rosList, examList, callAsessmentModel)
                    setBehaviuor()
                }
            }
        }
    }

    private fun setBehaviuor() {

    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(this, "${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        binding.back.performClick()
    }
}