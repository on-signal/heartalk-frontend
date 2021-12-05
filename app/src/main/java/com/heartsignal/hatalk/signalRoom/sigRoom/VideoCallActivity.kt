package com.heartsignal.hatalk.signalRoom.sigRoom

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.AudioMode
import com.cometchat.pro.models.User
import com.google.gson.Gson
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.ActivityVideoCallBinding
import com.heartsignal.hatalk.main.HomeActivity
import com.heartsignal.hatalk.main.chat.ChatingActivity
import com.heartsignal.hatalk.main.data.ChatData
import com.heartsignal.hatalk.signalRoom.sigRoom.dataFormat.KeepTalking
import com.heartsignal.hatalk.signalRoom.sigRoom.dataFormat.KeepTalkingResult
import com.heartsignal.hatalk.signalRoom.sigRoom.dataFormat.TalkingInfo
import com.heartsignal.hatalk.signalRoom.sigRoom.socket.ContentsSocketApplication
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class VideoCallActivity : AppCompatActivity() {
    private val TAG = "HEART"
    private lateinit var binding: ActivityVideoCallBinding
    private lateinit var myId: String
    private lateinit var counterPartId: String
    private lateinit var myGender: String
    private lateinit var groupName: String
    private lateinit var videoCallAvailableSocket: Socket
    private val onVideoCallAvailable = Emitter.Listener { _ -> callerStart() }
    private val onKeepTalking = Emitter.Listener { args ->
        keepTalkingEmitListener(args)
    }
    private val onKeepTalkingResult = Emitter.Listener { args ->
        keepTalkingResultEmitListener(args)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent: Intent = getIntent()
        val directCallData = intent.getParcelableExtra<DirectCall>("directCallData")

        if (directCallData != null) {
            myId = directCallData.myId
            counterPartId = directCallData.counterPartId
            myGender = directCallData.myGender
            groupName = directCallData.groupName
        }

        try {
            videoCallAvailableSocket = ContentsSocketApplication.get()
            videoCallAvailableSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        videoCallAvailableSocket.on("${groupName}OneToOneCall", onVideoCallAvailable)
        videoCallAvailableSocket.on("${myId}KeepTalking", onKeepTalking)
        videoCallAvailableSocket.on("${myId}KeepTalkingResult", onKeepTalkingResult)

        addCallListener()
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()

        videoCallAvailableSocket.disconnect()
    }

    override fun onBackPressed() {
    }

    private fun initiateVideoCall(receiverId: String) {
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER
        val callType: String = CometChatConstants.CALL_TYPE_VIDEO

        val call = Call(receiverId, receiverType, callType)

        CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d(TAG, "DirectCall initiated successfully: " + p0?.toString())
                startCall(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "DirectCall initialization failed with exception: " + p0?.message)
            }
        })
    }

    private fun addCallListener() {
        val listenerID = "VideoCallActivity"

        CometChat.addCallListener(listenerID, object : CometChat.CallListener() {
            override fun onOutgoingCallAccepted(p0: Call?) {
                Log.d(TAG, "Outgoing DirectCall accepted: " + p0?.toString())
                acceptCall(p0!!)
            }

            override fun onIncomingCallReceived(p0: Call?) {
                Log.d(TAG, "Incoming DirectCall: " + p0?.toString())
                acceptCall(p0!!)
            }

            override fun onIncomingCallCancelled(p0: Call?) {
                Log.d(TAG, "Incoming DirectCall cancelled: " + p0?.toString())
            }

            override fun onOutgoingCallRejected(p0: Call?) {
                Log.d(TAG, "Outgoing DirectCall rejected: " + p0?.toString())
            }
        })

        val gson = Gson()
        val directCallAvailableObj =
            JSONObject(gson.toJson(DirectCallAvailable(groupName, "finalCall")))

        videoCallAvailableSocket.emit("callInit", directCallAvailableObj)
    }

    private fun acceptCall(call: Call) {
        val sessionID: String = call.sessionId

        CometChat.acceptCall(sessionID, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("", "DirectCall accepted successfully: " + p0?.toString())
                startCall(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "DirectCall acceptance failed with exception: " + p0?.message)
            }
        })
    }

    private fun startCall(call: Call?) {
        val sessionID: String? = call?.sessionId
        val callView: RelativeLayout = findViewById(R.id.video_ui)
        val activity: Activity = this

        val callSettings = CallSettings.CallSettingsBuilder(activity, callView)
            .setSessionId(sessionID)
            .setAudioOnlyCall(true)
            .setDefaultAudioMode(CometChatConstants.AUDIO_MODE_SPEAKER)
            .build();

        CometChat.startCall(callSettings, object : CometChat.OngoingCallListener {

            override fun onUserJoined(user: User?) {
                Log.d(TAG, "DirectCall onUserJoined: Name " + user!!.getName());
            }

            override fun onUserLeft(user: User?) {
                Log.d(TAG, "DirectCall onUserLeft: " + user!!.getName());
            }

            override fun onError(e: CometChatException?) {
                Log.d(TAG, "DirectCall onError: " + e!!.message);
            }

            override fun onCallEnded(call: Call?) {
                Log.d(TAG, "DirectCall onCallEnded: " + call.toString());
            }

            override fun onUserListUpdated(p0: MutableList<User>?) {
                Log.d(TAG, "DirectCall onUserListUpdated: " + call.toString());
            }

            override fun onAudioModesUpdated(p0: MutableList<AudioMode>?) {
                Log.d(TAG, "DirectCall onAudioModesUpdated: " + call.toString());
            }
        });
        Toast.makeText(this, "1대1 영상 통화 연결 됐습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun callerStart() {
        if (myGender == "0") {
            initiateVideoCall(counterPartId)
        }
    }

    private fun keepTalkingEmitListener(args: Array<Any>) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("인연").setMessage("이분과 계속 인연을 이어가시겠습니까?")
            .setPositiveButton("수락", DialogInterface.OnClickListener { _, _ ->
                val gson = Gson()
                val finalChoice =
                    JSONObject(gson.toJson(KeepTalking(groupName, myId, "yes")))
                videoCallAvailableSocket.emit("keepTalkingToServer", finalChoice)
            }).setNegativeButton("취소", DialogInterface.OnClickListener { _, _ ->
                val gson = Gson()
                val finalChoice =
                    JSONObject(gson.toJson(KeepTalking(groupName, myId, "no")))
                videoCallAvailableSocket.emit("keepTalkingToServer", finalChoice)
            }).setCancelable(false)
        Thread {
            runOnUiThread() {
                kotlin.run {
                    dialogBuilder.show()
                }
            }
        }.start()
    }

    private fun keepTalkingResultEmitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())
        val keepTalkingResult = Gson().fromJson(res.toString(), KeepTalkingResult::class.java)

        CometChat.endCall(
            CometChat.getActiveCall().sessionId,
            object : CometChat.CallbackListener<Call?>() {
                override fun onSuccess(call: Call?) {
                    // handle end call success
                    Log.d(TAG, "CALL Ended successfully: " + call.toString())

                    CometChat.removeCallListener("VideoCallActivity")

                    if (keepTalkingResult.success) {
                        val intent = Intent(this@VideoCallActivity, ChatingActivity::class.java)
                        intent.putExtra("partner", keepTalkingResult.info?.partner)
                        val chatData = ChatData(null, null, null, null, null, null, mutableListOf(), null)
                        intent.putExtra("chatMessage", chatData)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@VideoCallActivity, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onError(e: CometChatException) {
                    // handled end call error
                    Log.d(TAG, "CALL Ended Error: $e")

                    CometChat.removeCallListener("VideoCallActivity")

                    if (keepTalkingResult.success) {
                        val intent = Intent(this@VideoCallActivity, ChatingActivity::class.java)
                        intent.putExtra("partner", keepTalkingResult.info?.partner)
                        val chatData = ChatData(null, null, null, null, null, null, mutableListOf(), null)
                        intent.putExtra("chatMessage", chatData)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@VideoCallActivity, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                        finish()
                    }
                }
            })


    }
}