package com.example.hatalk.signalRoom.sigRoom

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.AudioMode
import com.cometchat.pro.models.User

import com.example.hatalk.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/** [Permission] 처리해줘야 함!!!--------------------------------------------- */
class SignalRoomActivity : AppCompatActivity(R.layout.activity_signal_room) {
    private val TAG = "HEART"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** [Cometchat_init] ------------------------------------------------ */






        addCallListener()
        findViewById<Button>(R.id.temp_call).setOnClickListener {
            initiateCall()
        }

        /** [CometChat_init] ------------------------------------------------ */
    }

    private fun initiateCall() {
        val receiverID: String = "groupx5eu4qhpddbnfckm6sjdo59rcu9a0ul5mbt6xxn06fg8ch6znj03zr47jir2ykizldxfntllvuyu6zzq9qpejhgqtkgpobx"
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_GROUP
        val callType: String = CometChatConstants.CALL_TYPE_AUDIO

        val call = Call(receiverID, receiverType, callType)

        CometChat.initiateCall(call,object :CometChat.CallbackListener<Call>(){
            override fun onSuccess(p0: Call?) {
                Log.d(TAG, "Call initiated successfully: " + p0?.toString())
                startCall(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Call initialization failed with exception: " + p0?.message)
            }

        })
    }

    private fun startCall(call: Call?) {
        val sessionID: String? = call?.sessionId
        var callView: RelativeLayout? = findViewById(R.id.sig_room_ui)
        val activity: Activity? = this

        val callSettings = CallSettings.CallSettingsBuilder(activity,callView)
            .setSessionId(sessionID)
            .setAudioOnlyCall(true)
            .build();

        CometChat.startCall(callSettings, object:  CometChat.OngoingCallListener {

            override fun onUserJoined(user: User?) {
                Log.d(TAG, "onUserJoined: Name "+user!!.getName());
            }

            override fun onUserLeft(user: User?) {
                Log.d(TAG, "onUserLeft: "+user!!.getName());
            }

            override fun onError(e: CometChatException?) {
                Log.d(TAG, "onError: "+ e!!.message);
            }

            override fun onCallEnded(call: Call?) {
                Log.d(TAG, "onCallEnded: "+ call.toString());
            }

            override fun onUserListUpdated(p0: MutableList<User>?) {
                Log.d(TAG, "onUserListUpdated: "+ call.toString());
            }

            override fun onAudioModesUpdated(p0: MutableList<AudioMode>?) {
                Log.d(TAG, "onAudioModesUpdated: "+ call.toString());
            }
        });
        findViewById<RelativeLayout>(R.id.sig_room_ui).visibility = View.GONE
    }

    private fun addCallListener() {
        val listenerID:String= "chatlist_call_" + Date().time.toString();

        CometChat.addCallListener(listenerID,object : CometChat.CallListener(){
            override fun onOutgoingCallAccepted(p0: Call?) {
                Log.d(TAG, "Outgoing call accepted: " + p0?.toString())
                acceptCall(p0!!)
            }
            override fun onIncomingCallReceived(p0: Call?) {
                Log.d(TAG, "Incoming call: " + p0?.toString())
                acceptCall(p0!!)
            }

            override fun onIncomingCallCancelled(p0: Call?) {
                Log.d(TAG, "Incoming call cancelled: " + p0?.toString())
            }

            override fun onOutgoingCallRejected(p0: Call?) {
                Log.d(TAG, "Outgoing call rejected: " + p0?.toString())
            }
        })
    }

    private fun acceptCall(call:Call) {
        val sessionID:String = call.sessionId

        CometChat.acceptCall(sessionID,object :CometChat.CallbackListener<Call>(){
            override fun onSuccess(p0: Call?) {
                Log.d("", "Call accepted successfully: " + p0?.toString())
                startCall(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Call acceptance failed with exception: " + p0?.message)

            }
        })
    }
}