package com.heartsignal.hatalk.main

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ChatService : Service() {

//    private var timer = Timer("TimerTest", false).schedule(2000, 3000) {
//        doSomethingTimer()
//    }
    override fun onBind(intent: Intent?): IBinder? {
        // TODO("Return the communication channel to the service.")
        //super.onBind(intent)
        throw UnsupportedOperationException("Not yet")
    }
    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, ChatService::class.java)
            context.startService(startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, ChatService::class.java)
            context.stopService(stopIntent)
        }
    }
    // 처음 시작되면 호출되는 함수.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        callEvent(intent)
        return START_NOT_STICKY
    }
    // 이벤트를 호출(구현하고 싶은 코드를 구현하면 됩니다.)
    private fun callEvent(intent: Intent?){
        Log.d("HEART", "callEvent()")
//        timer.scheduledExecutionTime()
    }
    // Timer에서 실행되는 함수.
//    private fun doSomethingTimer(){
//        Handler(Looper.getMainLooper()).post{
//            Toast.makeText(applicationContext, "test", Toast.LENGTH_SHORT).show()
//        }
//    }
    override fun onDestroy() {
        super.onDestroy()
//        timer.cancel()
    }
}