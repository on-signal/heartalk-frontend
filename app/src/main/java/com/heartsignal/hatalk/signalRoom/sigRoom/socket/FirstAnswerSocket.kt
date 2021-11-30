package com.heartsignal.hatalk.signalRoom.sigRoom.socket

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.facebook.react.bridge.UiThreadUtil
import com.google.gson.Gson
import com.heartsignal.hatalk.R
//import com.heartsignal.hatalk.model.sigRoom.AnswerInfo
import com.heartsignal.hatalk.model.sigRoom.AnswerModel
import com.heartsignal.hatalk.signalRoom.sigRoom.FirstAnswerFragmentDialog
import com.heartsignal.hatalk.signalRoom.sigRoom.FirstAnswerResponse
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

//class FirstAnswerSocket(
//    private val context: Context,
//    private val groupName: String,
//    private val answerModel: AnswerModel,
//    private val supportFragmentManager: FragmentManager
//) {
//    private lateinit var socket: Socket
//    private lateinit
//    private val onFirstAnswer = Emitter.Listener { args ->
//        emitListener(args)
//    }
//
//    fun set() {
//        try {
//            socket = ContentsSocketApplication.get()
//            socket.connect()
//        } catch (e: URISyntaxException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun makeOn() {
//        socket.on("${groupName}AnswerChoice", onFirstAnswer)
//    }
//
//    private fun emitListener(args: Array<Any>) {
//        val res = JSONObject(args[0].toString())
//        val firstAnswerResponse = Gson().fromJson(res.toString(), FirstAnswerResponse::class.java)
//
//        for (reply in firstAnswerResponse.answers) {
//            val answerInfo = AnswerInfo(reply.owner, reply.answer, reply.already, reply.selector)
//            answerModel.appendAnswerList(answerInfo)
//            answerModel.appendOwnerIdList(reply.owner)
//        }
//
//
//        Thread {
//            UiThreadUtil.runOnUiThread(Runnable {
//                kotlin.run {
//                    FirstAnswerFragmentDialog().show(
//                        supportFragmentManager, "FirstAnswerDialog"
//                    )
//                }
//            })
//        }.start()
//    }
//
//    fun disconnect() {
//        socket.disconnect()
//    }
//}