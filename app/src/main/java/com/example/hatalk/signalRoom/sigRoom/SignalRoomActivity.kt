package com.example.hatalk.signalRoom.sigRoom

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.*
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.AudioMode
import com.cometchat.pro.models.User
import com.example.hatalk.R
import com.example.hatalk.databinding.ActivitySignalRoomBinding
import com.example.hatalk.main.data.MatchingConfirmResponse
import com.example.hatalk.model.sigRoom.MatchingModel
import com.example.hatalk.network.DeleteRoomRequest
import com.example.hatalk.network.MatchingApi
import com.example.hatalk.signalRoom.PRIVATE.IDs
import com.example.hatalk.signalRoom.sigRoom.socket.ContentsSocketApplication
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_signal_room.view.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*
import kotlin.concurrent.timer

/** [Permission] 처리해줘야 함!!!--------------------------------------------- */
class SignalRoomActivity : AppCompatActivity() {
    private val TAG = "HEART"
    private lateinit var chatSocket: Socket
    private lateinit var contentsReadySocket: Socket
    private lateinit var binding: ActivitySignalRoomBinding
    private val matchingModel: MatchingModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignalRoomBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /** [Cometchat_init] ------------------------------------------------ */

        initCometChat()
        setMatchingData()
        setMyButton(view)
        addCallListener()
        callerStart()

        /** [CometChat_init] ------------------------------------------------ */

        setChatSocket(view)

        setContentsReadySocket()
    }

    override fun onDestroy() {
        super.onDestroy()
        chatSocket.disconnect()
        contentsReadySocket.disconnect()
        lifecycleScope.launch {
            val deleteRoomRequest = DeleteRoomRequest(matchingModel.groupRoomName)
            MatchingApi.retrofitService.deleteRoom(deleteRoomRequest)
        }
    }

    private fun initiateCall(guid: String) {
        val receiverID: String = guid
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_GROUP
        val callType: String = CometChatConstants.CALL_TYPE_AUDIO

        val call = Call(receiverID, receiverType, callType)

        CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
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

        val callSettings = CallSettings.CallSettingsBuilder(activity, callView)
            .setSessionId(sessionID)
            .setAudioOnlyCall(true)
            .setDefaultAudioMode(CometChatConstants.AUDIO_MODE_SPEAKER)
            .build();

        CometChat.startCall(callSettings, object : CometChat.OngoingCallListener {

            override fun onUserJoined(user: User?) {
                Log.d(TAG, "onUserJoined: Name " + user!!.getName());
            }

            override fun onUserLeft(user: User?) {
                Log.d(TAG, "onUserLeft: " + user!!.getName());
            }

            override fun onError(e: CometChatException?) {
                Log.d(TAG, "onError: " + e!!.message);
            }

            override fun onCallEnded(call: Call?) {
                Log.d(TAG, "onCallEnded: " + call.toString());
            }

            override fun onUserListUpdated(p0: MutableList<User>?) {
                Log.d(TAG, "onUserListUpdated: " + call.toString());
            }

            override fun onAudioModesUpdated(p0: MutableList<AudioMode>?) {
                Log.d(TAG, "onAudioModesUpdated: " + call.toString());
            }
        });
        findViewById<RelativeLayout>(R.id.sig_room_ui).visibility = View.GONE
        Toast.makeText(this, "연결 됐습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun addCallListener() {
        val listenerID: String = "chatlist_call_" + Date().time.toString();

        CometChat.addCallListener(listenerID, object : CometChat.CallListener() {
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

    private fun acceptCall(call: Call) {
        val sessionID: String = call.sessionId

        CometChat.acceptCall(sessionID, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.d("", "Call accepted successfully: " + p0?.toString())
                startCall(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Call acceptance failed with exception: " + p0?.message)
            }
        })
    }

    private fun setMatchingData() {
        val intent: Intent = getIntent()
        val matchingData = intent.getParcelableExtra<MatchingConfirmResponse>("matchingData")

        val userID: String = CometChat.getLoggedInUser().uid.toString()

        matchingModel.setGroupRoomName(matchingData?.groupName.toString())
        matchingModel.setCaller(matchingData?.caller.toString())

        matchingModel.setMyId(userID)

        if (userID == matchingData?.room_info?.user1?.Id.toString()) {
            matchingModel.setMyNickname(matchingData?.room_info?.user1?.nickname.toString())
            matchingModel.setMyIcon(matchingData?.room_info?.user1?.icon.toString())
        } else if (userID == matchingData?.room_info?.user2?.Id.toString()) {
            matchingModel.setMyNickname(matchingData?.room_info?.user2?.nickname.toString())
            matchingModel.setMyIcon(matchingData?.room_info?.user2?.icon.toString())
        }

        matchingModel.setUser1Id(matchingData?.room_info?.user1?.Id.toString())
        matchingModel.setUser1Nickname(matchingData?.room_info?.user1?.nickname.toString())
        matchingModel.setUser1Icon(matchingData?.room_info?.user1?.icon.toString())

        matchingModel.setUser2Id(matchingData?.room_info?.user2?.Id.toString())
        matchingModel.setUser2Nickname(matchingData?.room_info?.user2?.nickname.toString())
        matchingModel.setUser2Icon(matchingData?.room_info?.user2?.icon.toString())
    }

    private fun setMyButton(view: View) {
        var userImageVIew: ImageView? = null
        when (matchingModel.myIcon) {
            "lion" -> {
                userImageVIew = view.lion
            }
            "bee" -> {
                userImageVIew = view.bee
            }
            "penguin" -> {
                userImageVIew = view.penguin
            }
            "hamster" -> {
                userImageVIew = view.hamster
            }
            "wolf" -> {
                userImageVIew = view.wolf
            }
            "fox" -> {
                userImageVIew = view.fox
            }
        }
        userImageVIew?.setBackgroundColor(Color.parseColor("#472a2b"))
    }

    private fun initCometChat() {
        val appID: String = IDs.APP_ID // Replace with your App ID
        val region: String = IDs.REGION // Replace with your App Region ("eu" or "us")

        val appSettings =
            AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region)
                .build()
        CometChat.init(this, appID, appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                Log.d(TAG, "Initialization completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Initialization failed with exception: " + p0?.message)
            }
        })
    }

    private fun callerStart() {
        if (matchingModel.caller == matchingModel.myId) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    initiateCall(matchingModel.groupRoomName)
                },
                1000
            )
        }
    }

    private fun setChatSocket(view: View) {
        val chatButton = view.button_chat_send
        try {
            chatSocket = ContentsSocketApplication.get()
            chatSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace();
        }

        val onChatConnect = Emitter.Listener { args ->
            val res = JSONObject(args[0].toString())

            var userChat: TextView? = null
            when (res.getString("icon")) {
                "lion" -> {
                    userChat = view.lion_text
                }
                "bee" -> {
                    userChat = view.bee_text
                }
                "penguin" -> {
                    userChat = view.penguin_text
                }
                "hamster" -> {
                    userChat = view.hamster_text
                }
                "wolf" -> {
                    userChat = view.wolf_text
                }
                "fox" -> {
                    userChat = view.fox_text
                }
            }

            Thread {
                runOnUiThread(Runnable {
                    kotlin.run {
                        userChat?.text = res.getString("text")
                    }
                })
            }.start()
        }
        chatSocket.on(matchingModel.groupRoomName, onChatConnect)

        chatButton.setOnClickListener {
            val chatText = view.edit_chat_message
            val message = TempMessage(
                matchingModel.groupRoomName,
                matchingModel.myId,
                chatText.text.toString(),
                matchingModel.myIcon
            )
            val gson = Gson()
            val obj = JSONObject(gson.toJson(message))

            chatSocket.emit("msgToServer", obj)
            chatText.text.clear()
        }
    }

    private fun setContentsReadySocket() {
        try {
            contentsReadySocket = ContentsSocketApplication.get()
            contentsReadySocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace();
        }

        val onContentsConnect = Emitter.Listener { args ->
            val res = JSONObject(args[0].toString())
            val introContentsResponse =
                Gson().fromJson(res.toString(), IntroContentsResponse::class.java)

            val introductionTime = Date(introContentsResponse.contentsStartTime.introduction).time
            timer(period = 10) {
                if (Date().time >= introductionTime) {
                    Thread {
                        runOnUiThread(Runnable {
                            Toast.makeText(
                                applicationContext,
                                "지금부터 자기소개 시간입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    }.start()
                    this.cancel()
                }
            }
            timer(period = 10) {
                if (Date().time >= introductionTime + 5000) {
                    Thread {
                        runOnUiThread(Runnable {
                            Toast.makeText(
                                applicationContext,
                                "늑대님 자기 소개 시간 10초 드리겠습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    }.start()
//                    if (matchingModel.myIcon == "wolf") {
//                        callManager.muteAudio(false)
//                    } else {
//                        callManager.muteAudio(true)
//                    }
                    this.cancel()
                }
            }
            timer(period = 10) {
                if (Date().time >= introductionTime + 15000) {
                    Thread {
                        runOnUiThread(Runnable {
                            Toast.makeText(
                                applicationContext,
                                "여우님 자기 소개 시간 10초 드리겠습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    }.start()
//                    if (matchingModel.myIcon == "fox") {
//                        callManager.muteAudio(false)
//                    } else {
//                        callManager.muteAudio(true)
//                    }
                    this.cancel()
                }
            }
        }
        contentsReadySocket.on("${matchingModel.groupRoomName}firstChoice", onContentsConnect)

        val firstContent = FirstContent(
            matchingModel.groupRoomName,
            matchingModel.myId
        )
        val firstContentGson = Gson()
        val firstContentObj = JSONObject(firstContentGson.toJson(firstContent))
        contentsReadySocket.emit("readyToServer", firstContentObj)
    }
}