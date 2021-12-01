package com.heartsignal.hatalk.signalRoom.sigRoom

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.ActivityOneToOneCallBinding
import com.heartsignal.hatalk.signalRoom.sigRoom.socket.FirstCallEndSocket

class OneToOneCallActivity : AppCompatActivity() {
    private val TAG = "HEART"
    private lateinit var binding: ActivityOneToOneCallBinding
    private lateinit var myId: String
    private lateinit var counterPartId: String
    private lateinit var myGender: String
    private lateinit var groupName: String
    private lateinit var firstCallEndSocket: FirstCallEndSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOneToOneCallBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent: Intent = getIntent()
        val oneToOneCallData = intent.getParcelableExtra<OnetoOneCall>("oneToOneCallData")

        if (oneToOneCallData != null) {
            myId = oneToOneCallData.myId
            counterPartId = oneToOneCallData.counterPartId
            myGender = oneToOneCallData.myGender
            groupName = oneToOneCallData.groupName
        }


        addCallListener()
        callerStart()

        firstCallEndSocket = FirstCallEndSocket(this, groupName, TAG)
        firstCallEndSocket.set()
        firstCallEndSocket.makeOn()
    }

    override fun onDestroy() {
        super.onDestroy()

        firstCallEndSocket.disconnect()
    }

    private fun initiateOneToOneCall(receiverId: String) {
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER
        val callType: String = CometChatConstants.CALL_TYPE_AUDIO

        val call = Call(receiverId, receiverType, callType)

        CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d(TAG, "OneToOneCall initiated successfully: " + p0?.toString())
                startCall(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "OneToOneCall initialization failed with exception: " + p0?.message)
            }

        })
    }

    private fun addCallListener() {
        val listenerID= "OneToOneCallActivity"

        CometChat.addCallListener(listenerID, object : CometChat.CallListener() {
            override fun onOutgoingCallAccepted(p0: Call?) {
                Log.d(TAG, "Outgoing OneToOneCall accepted: " + p0?.toString())
                acceptCall(p0!!)
            }

            override fun onIncomingCallReceived(p0: Call?) {
                Log.d(TAG, "Incoming OneToOneCall: " + p0?.toString())
                acceptCall(p0!!)
            }

            override fun onIncomingCallCancelled(p0: Call?) {
                Log.d(TAG, "Incoming OneToOneCall cancelled: " + p0?.toString())
            }

            override fun onOutgoingCallRejected(p0: Call?) {
                Log.d(TAG, "Outgoing OneToOneCall rejected: " + p0?.toString())
            }
        })
    }

    private fun acceptCall(call: Call) {
        val sessionID: String = call.sessionId

        CometChat.acceptCall(sessionID, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("", "OneToOneCall accepted successfully: " + p0?.toString())
                startCall(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "OneToOneCall acceptance failed with exception: " + p0?.message)
            }
        })
    }

    private fun startCall(call: Call?) {
        val sessionID: String? = call?.sessionId
        val callView: RelativeLayout = findViewById(R.id.one_to_one_ui)
        val activity: Activity = this

        val callSettings = CallSettings.CallSettingsBuilder(activity, callView)
            .setSessionId(sessionID)
            .setAudioOnlyCall(true)
            .setDefaultAudioMode(CometChatConstants.AUDIO_MODE_SPEAKER)
            .build();

        CometChat.startCall(callSettings, object : CometChat.OngoingCallListener {

            override fun onUserJoined(user: User?) {
                Log.d(TAG, "OneToOneCall onUserJoined: Name " + user!!.getName());
            }

            override fun onUserLeft(user: User?) {
                Log.d(TAG, "OneToOneCall onUserLeft: " + user!!.getName());
            }

            override fun onError(e: CometChatException?) {
                Log.d(TAG, "OneToOneCall onError: " + e!!.message);
            }

            override fun onCallEnded(call: Call?) {
                Log.d(TAG, "OneToOneCall onCallEnded: " + call.toString());
            }

            override fun onUserListUpdated(p0: MutableList<User>?) {
                Log.d(TAG, "OneToOneCall onUserListUpdated: " + call.toString());
            }

            override fun onAudioModesUpdated(p0: MutableList<AudioMode>?) {
                Log.d(TAG, "OneToOneCall onAudioModesUpdated: " + call.toString());
            }
        });
        binding.oneToOneUi.visibility = View.GONE
        Toast.makeText(this, "1대1 콜 연결 됐습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun callerStart() {
        if (myGender == "0") {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    initiateOneToOneCall(counterPartId)
                },
                3000
            )

        }
    }
}