package com.heartsignal.hatalk.signalRoom.sigRoom

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.heartsignal.hatalk.databinding.ActivityDirectCallBinding
import com.heartsignal.hatalk.signalRoom.sigRoom.socket.CallEndSocket
import com.heartsignal.hatalk.signalRoom.sigRoom.socket.ContentsSocketApplication
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class DirectCallActivity : AppCompatActivity() {
    private val TAG = "HEART"
    private lateinit var binding: ActivityDirectCallBinding
    private lateinit var myId: String
    private lateinit var counterPartId: String
    private lateinit var myGender: String
    private lateinit var groupName: String
    private lateinit var callEndSocket: CallEndSocket
    private lateinit var directCallAvailableSocket: Socket
    private val onDirectCallAvailable = Emitter.Listener { _ ->
        callerStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectCallBinding.inflate(layoutInflater)
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
            directCallAvailableSocket = ContentsSocketApplication.get()
            directCallAvailableSocket.connect()
        } catch(e:URISyntaxException) {
            e.printStackTrace()
        }

        directCallAvailableSocket.on("${groupName}OneToOneCall", onDirectCallAvailable)

        addCallListener()

        callEndSocket = CallEndSocket(this, groupName, TAG)
        callEndSocket.set()
        callEndSocket.makeOn()
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

        callEndSocket.disconnect()
        directCallAvailableSocket.disconnect()
    }

    private fun initiateDirectCall(receiverId: String) {
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER
        val callType: String = CometChatConstants.CALL_TYPE_AUDIO

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
        val listenerID = "DirectCallActivity"


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
            JSONObject(gson.toJson(DirectCallAvailable(groupName, "oneToOneCall")))

        directCallAvailableSocket.emit("callInit", directCallAvailableObj)
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
        val callView: RelativeLayout = findViewById(R.id.direct_ui)
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
        binding.directUi.visibility = View.GONE
        Toast.makeText(this, "1대1 콜 연결 됐습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun callerStart() {
        if (myGender == "0") {
            initiateDirectCall(counterPartId)
        }
    }

    override fun onBackPressed() {
    }
}