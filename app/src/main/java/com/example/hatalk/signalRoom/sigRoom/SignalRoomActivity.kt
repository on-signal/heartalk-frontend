package com.example.hatalk.signalRoom.sigRoom

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.AudioMode
import com.cometchat.pro.models.User

import com.example.hatalk.R
import com.example.hatalk.network.MatchingApi
import com.example.hatalk.network.MatchingRequest
import com.example.hatalk.signalRoom.PRIVATE.IDs
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import java.net.URISyntaxException
import java.util.*

/** [Permission] 처리해줘야 함!!!--------------------------------------------- */
class SignalRoomActivity : AppCompatActivity(R.layout.activity_signal_room) {
    private val TAG = "HEART"
    lateinit var mSocket: Socket
    private val onConnect = Emitter.Listener {
        mSocket.emit("emitReceive", "OK")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** [Cometchat_init] ------------------------------------------------ */
        val userID:String = CometChat.getLoggedInUser().toString()
        findViewById<Button>(R.id.button_chat_send).setOnClickListener {
            Log.d(TAG, userID)
        }





        addCallListener()
        findViewById<Button>(R.id.temp_call).setOnClickListener {
            initiateCall()
//            matchingCall()
        }

        /** [CometChat_init] ------------------------------------------------ */

        /**
         * [Socket IO Chat Start]
         */

        try {
            mSocket = IO.socket("http://localhost/8000")
            mSocket.connect()
            Log.d("Connected", "OK")
        } catch (e: URISyntaxException) {
            Log.d("ERR", e.toString())
        }
        mSocket.on(Socket.EVENT_CONNECT, onConnect)

        /**
         * [Socket IO Chat End]
         */
    }



    private fun initiateCall() {
        val receiverID: String = IDs.RECEIVERID
        Log.d("TEST", receiverID)
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
        Toast.makeText(this,"연결 됐습니다.", Toast.LENGTH_SHORT).show()
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