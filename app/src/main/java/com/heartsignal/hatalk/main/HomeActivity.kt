package com.heartsignal.hatalk.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.FragmentSignUpLoadingBinding
import com.heartsignal.hatalk.main.chat.ChatSocketApplication
import com.heartsignal.hatalk.main.userModel.UserModel
import com.heartsignal.hatalk.model.userInfo
import com.heartsignal.hatalk.network.UserApi
import com.facebook.react.bridge.UiThreadUtil
import com.google.gson.Gson
import com.heartsignal.hatalk.GlobalApplication
import com.heartsignal.hatalk.main.data.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class HomeActivity : AppCompatActivity(R.layout.activity_home) {
    private val TAG = "HEART"
    private val sharedViewModel: UserModel by viewModels()
    private lateinit var navController: NavController
    lateinit var mSocket: Socket


    private val requiredPermissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    companion object {
        private const val REQUEST_ALL_PERMISSION = 101
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()

        /**  [ChatSocket__OPEN]  */

        mSocket = ChatSocketApplication.get(GlobalApplication.userInfo.accessToken)
        mSocket.connect()

        mSocket.on("chats", onFriendsConnection)


        navController = findNavController(R.id.home_fragment)

        setupActionBarWithNavController(navController)
    }

    @SuppressLint("NotifyDataSetChanged")
    val onFriendsConnection = Emitter.Listener { args ->
        // Array type은 JSONArray로 받아야 한다.

        val friendsJson = JSONArray(args[0].toString())
        val FriendsList = Gson().fromJson(friendsJson.toString(), Array<Friends>::class.java)
        sharedViewModel.setFriends(FriendsList)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.home_fragment)?.childFragmentManager?.fragments?.last()
        if (currentFragment is ChatFragment) {
            Log.d(TAG, "HIHIHI")
            Log.d(TAG, currentFragment.friends.toString())
            currentFragment.friends.clear()
            Log.d(TAG, currentFragment.friends.toString())
            currentFragment.RecyclerViewAdapter().update(FriendsList)
            Log.d(TAG, currentFragment.friends.toString())
        }
    }


    private fun checkPermissions() {
        val rejectedPermissionList = ArrayList<String>()

        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                rejectedPermissionList.add(permission)
            }
        }
        if(rejectedPermissionList.isNotEmpty()){
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), REQUEST_ALL_PERMISSION)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.heartsignal.hatalk.R.menu.tabs, menu)
        bottom_bar.setupWithNavController(menu!!, navController)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return true
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        supportActionBar?.hide()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            finishAffinity()
                        }
                    }
                }
            }
        }
    }



    override fun onBackPressed() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("알림")
            .setMessage("종료하시겠습니까?")
            .setPositiveButton("예",
                DialogInterface.OnClickListener{ dialog, id ->
//                    super.onBackPressed()
                    finishAffinity()
                })
            .setNegativeButton("아니오",
                DialogInterface.OnClickListener{ dialog, id ->

                }
            )
        Thread {
            UiThreadUtil.runOnUiThread(Runnable {
                kotlin.run {
                    dialogBuilder.show()
                }
            })
        }.start()
    }
}