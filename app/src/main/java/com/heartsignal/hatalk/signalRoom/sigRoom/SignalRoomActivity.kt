package com.heartsignal.hatalk.signalRoom.sigRoom

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.ActivitySignalRoomBinding
import com.heartsignal.hatalk.main.data.MatchingConfirmResponse
import com.heartsignal.hatalk.model.sigRoom.MatchingModel
import com.heartsignal.hatalk.network.DeleteRoomRequest
import com.heartsignal.hatalk.network.MatchingApi
import com.heartsignal.hatalk.signalRoom.PRIVATE.IDs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_signal_room.view.*
import kotlinx.coroutines.launch
import java.util.*
import com.heartsignal.hatalk.model.sigRoom.MatchingUser
import com.cometchat.pro.core.CometChat
import com.facebook.react.bridge.UiThreadUtil
import com.google.gson.Gson
import com.heartsignal.hatalk.model.sigRoom.AnswerModel
import com.heartsignal.hatalk.signalRoom.sigRoom.socket.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_signal_room.*
import org.json.JSONObject
import java.net.URISyntaxException


/** [Permission] 처리해줘야 함!!!--------------------------------------------- */
class SignalRoomActivity : AppCompatActivity() {
    private val TAG = "HEART"
    private lateinit var chatSocket: ChatSocket
    private lateinit var contentsReadySocket: ContentsReadySocket
    private lateinit var introductionSocket: IntroductionSocket
    private lateinit var readyFirstChoiceSocket: ReadyFirstChoiceSocket
    private lateinit var firstQuestionSocket: FirstQuestionSocket
    private lateinit var firstAnswerSocket: Socket
    private lateinit var finalChoiceSocket: Socket
    private lateinit var binding: ActivitySignalRoomBinding
    private val matchingModel: MatchingModel by viewModels()
    private val answerModel: AnswerModel by viewModels()
    private val firstAnswerFragmentDialog = FirstAnswerFragmentDialog()
    private val onFirstAnswer = Emitter.Listener { args ->
        firstAnswerEmitListener(args)
    }
    private val onSecondCall = Emitter.Listener { args ->
        secondCallEmitListener(args)
    }
    private val onFinalChoice = Emitter.Listener { _ ->
        finalChoiceEmitListener()
    }
    private val onFinalCall = Emitter.Listener { args ->
        finalCallEmitListener(args)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignalRoomBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        full_layout.setOnClickListener(View.OnClickListener { view ->
            val inputMethodManager =
                view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        })

        /** [Cometchat_init] ------------------------------------------------ */

        initCometChat()
        setMatchingData()
        setMyButton(view)
        addCallListener()
        callerStart()

        /** [CometChat_init] ------------------------------------------------ */

        chatSocket =
            ChatSocket(view, matchingModel.groupName, matchingModel.myId, matchingModel.myIcon)
        chatSocket.set()
        chatSocket.makeOn()

        contentsReadySocket = ContentsReadySocket(this, matchingModel.groupName)
        contentsReadySocket.set()
        val firstContent = FirstContent(
            matchingModel.groupName,
            matchingModel.myId,
            matchingModel.myGender
        )
        contentsReadySocket.makeOn()
        contentsReadySocket.emit(firstContent)

        introductionSocket =
            IntroductionSocket(this, matchingModel.groupName, matchingModel.myIcon, view)
        introductionSocket.set()
        introductionSocket.makeOn()

        readyFirstChoiceSocket = ReadyFirstChoiceSocket(
            this,
            matchingModel.groupName,
            matchingModel.myId,
            matchingModel.myGender,
            matchingModel.manList,
            matchingModel.womanList,
            TAG
        )
        readyFirstChoiceSocket.set()
        readyFirstChoiceSocket.makeOn()

        firstQuestionSocket =
            FirstQuestionSocket(
                this,
                matchingModel.groupName,
                matchingModel.myId,
                matchingModel.myGender,
                matchingModel.questionList[0]
            )
        firstQuestionSocket.set()
        firstQuestionSocket.makeOn()

//        firstAnswerSocket = FirstAnswerSocket(this, matchingModel.groupName, answerModel, supportFragmentManager)
//        firstAnswerSocket.set()
//        firstAnswerSocket.makeOn()

        try {
            firstAnswerSocket = ContentsSocketApplication.get()
            firstAnswerSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        firstAnswerSocket.on("${matchingModel.groupName}AnswerChoice", onFirstAnswer)
        firstAnswerSocket.on("${matchingModel.groupName}SecondCall", onSecondCall)

        try {
            finalChoiceSocket = ContentsSocketApplication.get()
            finalChoiceSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        finalChoiceSocket.on("${matchingModel.groupName}FinalChoice", onFinalChoice)
        finalChoiceSocket.on("${matchingModel.groupName}FinalCall", onFinalCall)
    }



    override fun onDestroy() {
        super.onDestroy()
        chatSocket.disconnect()
        contentsReadySocket.disconnect()
        introductionSocket.disconnect()
        readyFirstChoiceSocket.disconnect()
        firstQuestionSocket.disconnect()
        firstAnswerSocket.disconnect()

        lifecycleScope.launch {
            val deleteRoomRequest = DeleteRoomRequest(matchingModel.groupName)
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
        binding.sigRoomUi.visibility = View.GONE
        Toast.makeText(this, "연결 됐습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun addCallListener() {
        val listenerID = "SignalRoomActivity"

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

        matchingModel.setGroupName(matchingData?.groupName.toString())
        matchingModel.setCaller(matchingData?.caller.toString())

        matchingModel.setMyGender(matchingData?.gender.toString())
        matchingModel.setMyId(userID)

        when (userID) {
            matchingData?.room_info?.user1?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user1?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user1?.icon.toString())
            }
            matchingData?.room_info?.user2?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user2?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user2?.icon.toString())
            }
            matchingData?.room_info?.user3?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user3?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user3?.icon.toString())
            }
            matchingData?.room_info?.user4?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user4?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user4?.icon.toString())
            }
            matchingData?.room_info?.user5?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user5?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user5?.icon.toString())
            }
            matchingData?.room_info?.user6?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user6?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user6?.icon.toString())
            }
        }

        if (matchingData?.room_info?.user1?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user1?.Id.toString(),
                matchingData?.room_info?.user1?.nickname.toString(),
                matchingData?.room_info?.user1?.gender.toString(),
                matchingData?.room_info?.user1?.icon.toString()
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user1?.Id.toString(),
                matchingData?.room_info?.user1?.nickname.toString(),
                matchingData?.room_info?.user1?.gender.toString(),
                matchingData?.room_info?.user1?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if (matchingData?.room_info?.user2?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user2?.Id.toString(),
                matchingData?.room_info?.user2?.nickname.toString(),
                matchingData?.room_info?.user2?.gender.toString(),
                matchingData?.room_info?.user2?.icon.toString()
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user2?.Id.toString(),
                matchingData?.room_info?.user2?.nickname.toString(),
                matchingData?.room_info?.user2?.gender.toString(),
                matchingData?.room_info?.user2?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if (matchingData?.room_info?.user3?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user3?.Id.toString(),
                matchingData?.room_info?.user3?.nickname.toString(),
                matchingData?.room_info?.user3?.gender.toString(),
                matchingData?.room_info?.user3?.icon.toString()
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user3?.Id.toString(),
                matchingData?.room_info?.user3?.nickname.toString(),
                matchingData?.room_info?.user3?.gender.toString(),
                matchingData?.room_info?.user3?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if (matchingData?.room_info?.user4?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user4?.Id.toString(),
                matchingData?.room_info?.user4?.nickname.toString(),
                matchingData?.room_info?.user4?.gender.toString(),
                matchingData?.room_info?.user4?.icon.toString()
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user4?.Id.toString(),
                matchingData?.room_info?.user4?.nickname.toString(),
                matchingData?.room_info?.user4?.gender.toString(),
                matchingData?.room_info?.user4?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if(matchingData?.room_info?.user5?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user5?.Id.toString(),
                matchingData?.room_info?.user5?.nickname.toString(),
                matchingData?.room_info?.user5?.gender.toString(),
                matchingData?.room_info?.user5?.icon.toString(),
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user5?.Id.toString(),
                matchingData?.room_info?.user5?.nickname.toString(),
                matchingData?.room_info?.user5?.gender.toString(),
                matchingData?.room_info?.user5?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if(matchingData?.room_info?.user6?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user6?.Id.toString(),
                matchingData?.room_info?.user6?.nickname.toString(),
                matchingData?.room_info?.user6?.gender.toString(),
                matchingData?.room_info?.user6?.icon.toString(),
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user6?.Id.toString(),
                matchingData?.room_info?.user6?.nickname.toString(),
                matchingData?.room_info?.user6?.gender.toString(),
                matchingData?.room_info?.user6?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        for (item: String in matchingData?.question_list!!) {
            matchingModel.appendQuestionList(item)
        }
    }

    private fun setMyButton(view: View) {
        var userTextView: TextView? = null
        when (matchingModel.myIcon) {
            "lion" -> {
                userTextView = view.lion_text
            }
            "bee" -> {
                userTextView = view.bee_text
            }
            "penguin" -> {
                userTextView = view.penguin_text
            }
            "hamster" -> {
                userTextView = view.hamster_text
            }
            "wolf" -> {
                userTextView = view.wolf_text
            }
            "fox" -> {
                userTextView = view.fox_text
            }
        }
        userTextView?.setBackgroundColor(Color.parseColor("#fff7d9"))
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
                    initiateCall(matchingModel.groupName)
                },
                100
            )
        }
    }

    private fun firstAnswerEmitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())
        val firstAnswerResponse = Gson().fromJson(res.toString(), FirstAnswerResponse::class.java)

        for (reply in firstAnswerResponse.answers) {
            val answerInfo = AnswerInfo(reply.owner, reply.answer, reply.already, reply.selector)
            answerModel.appendAnswerList(answerInfo)
            answerModel.appendOwnerIdList(reply.owner)
        }

        if (matchingModel.myGender == "1") {
            firstAnswerFragmentDialog.show(
                supportFragmentManager, "FirstAnswerDialog"
            )
        }
    }

    private fun secondCallEmitListener(args: Array<Any>) {
        if(firstAnswerFragmentDialog.isVisible) {
            Thread {
                UiThreadUtil.runOnUiThread(Runnable {
                    kotlin.run {
                        firstAnswerFragmentDialog.dismiss()
                    }
                })
            }.start()
        }

        val res = JSONObject(args[0].toString())
        val secondCallResponse = Gson().fromJson(res.toString(), CallMatchingResponse::class.java)

        var counterPartId = ""
        var counterIcon = ""
        if (matchingModel.myGender == "0") {
            for (partner in secondCallResponse.partners) {
                if (partner[0] == matchingModel.myId) {
                    counterPartId = partner[1]
                    break
                }
            }
            for (woman in matchingModel.womanList) {
                if (counterPartId == woman.id) {
                    counterIcon = woman.icon
                    break
                }
            }
        } else if (matchingModel.myGender == "1") {
            for (partner in secondCallResponse.partners) {
                if (partner[1] == matchingModel.myId) {
                    counterPartId = partner[0]
                    break
                }
            }
            for (man in matchingModel.manList) {
                if (counterPartId == man.id) {
                    counterIcon = man.icon
                    break
                }
            }
        }

        val intent = Intent(this@SignalRoomActivity, DirectCallActivity::class.java)
        val directCallObj =
            DirectCall(
                matchingModel.myId,
                matchingModel.myGender,
                counterPartId,
                counterIcon,
                matchingModel.groupName
            )

        intent.putExtra("directCallData", directCallObj)
        startActivity(intent)
    }

    private fun finalChoiceEmitListener() {
        val dialogBuilder = AlertDialog.Builder(this)
        val womanIconList = arrayOf("fox", "hamster", "bee")
        val manIconList = arrayOf("wolf", "penguin", "lion")
        lateinit var selectedItem: String
        if (matchingModel.myGender == "0") {
            dialogBuilder.setTitle("최종선택")
                .setSingleChoiceItems(womanIconList, -1) { _, pos ->
                    selectedItem = womanIconList[pos]
                }.setPositiveButton("OK") { _, _ ->
                    Toast.makeText(
                        this,
                        "$selectedItem is Selected", Toast.LENGTH_LONG
                    ).show()
                    var choice = ""
                    for (woman in matchingModel.womanList) {
                        if (woman.icon == selectedItem) {
                            choice = woman.id
                        }
                    }
                    val gson = Gson()
                    val finalChoice =
                        JSONObject(
                            gson.toJson(
                                FirstChoiceRequest(
                                    matchingModel.groupName,
                                    matchingModel.myId,
                                    matchingModel.myGender,
                                    choice
                                )
                            )
                        )
                    finalChoiceSocket.emit("finalChoiceToServer", finalChoice)
                }.setCancelable(false)
            Thread {
                UiThreadUtil.runOnUiThread(Runnable {
                    kotlin.run {
                        dialogBuilder.show()
                    }
                })
            }.start()
        } else if (matchingModel.myGender == "1") {
            dialogBuilder.setTitle("최종선택")
                .setSingleChoiceItems(manIconList, -1) { _, pos ->
                    selectedItem = manIconList[pos]
                }.setPositiveButton("OK") { _, _ ->
                    Toast.makeText(
                        this,
                        "$selectedItem is Selected", Toast.LENGTH_LONG
                    ).show()
                    var choice = ""
                    for (man in matchingModel.manList) {
                        if (man.icon == selectedItem) {
                            choice = man.id
                        }
                    }
                    val gson = Gson()
                    val finalChoice =
                        JSONObject(
                            gson.toJson(
                                FirstChoiceRequest(
                                    matchingModel.groupName,
                                    matchingModel.myId,
                                    matchingModel.myGender,
                                    choice
                                )
                            )
                        )
                    finalChoiceSocket.emit("finalChoiceToServer", finalChoice)
                }.setCancelable(false)
            Thread {
                UiThreadUtil.runOnUiThread(Runnable {
                    kotlin.run {
                        dialogBuilder.show()
                    }
                })
            }.start()
        }
    }

    private fun finalCallEmitListener(args: Array<Any>) {
        val res = JSONObject(args[0].toString())
        val finalCallResponse = Gson().fromJson(res.toString(), CallMatchingResponse::class.java)

        var counterPartId = ""
        var counterIcon = ""
        var canVideoCall = false
        if (matchingModel.myGender == "0") {
            for (partner in finalCallResponse.partners) {
                if (partner[0] == matchingModel.myId) {
                    counterPartId = partner[1]
                    canVideoCall = true
                    break
                }
            }
            for (woman in matchingModel.womanList) {
                if (counterPartId == woman.id) {
                    counterIcon = woman.icon
                    break
                }
            }
        } else if (matchingModel.myGender == "1") {
            for (partner in finalCallResponse.partners) {
                if (partner[1] == matchingModel.myId) {
                    counterPartId = partner[0]
                    canVideoCall = true
                    break
                }
            }
            for (man in matchingModel.manList) {
                if (counterPartId == man.id) {
                    counterIcon = man.icon
                    break
                }
            }
        }

        if (canVideoCall) {
            val intent = Intent(this, VideoCallActivity::class.java)
            val directCallObj =
                DirectCall(
                    matchingModel.myId,
                    matchingModel.myGender,
                    counterPartId,
                    counterIcon,
                    matchingModel.groupName
                )

            intent.putExtra("directCallData", directCallObj)
            startActivity(intent)
            finish()
        } else finish()
    }

    override fun onBackPressed() {
    }
}