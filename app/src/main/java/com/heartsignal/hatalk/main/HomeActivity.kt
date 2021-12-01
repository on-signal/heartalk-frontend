package com.heartsignal.hatalk.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.FragmentSignUpLoadingBinding
import com.heartsignal.hatalk.main.chat.ChatSocketApplication
import com.heartsignal.hatalk.main.data.MatchingConfirmData
import com.heartsignal.hatalk.main.data.MatchingConfirmResponse
import com.heartsignal.hatalk.main.data.Partner
import com.heartsignal.hatalk.main.data.testData
import com.heartsignal.hatalk.main.userModel.UserModel
import com.heartsignal.hatalk.model.userInfo
import com.heartsignal.hatalk.network.UserApi
import com.facebook.react.bridge.UiThreadUtil
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException
import io.socket.client.Manager
import io.socket.client.IO








class HomeActivity : AppCompatActivity(R.layout.activity_home) {
    private val TAG = "HEART"
    private var binding: FragmentSignUpLoadingBinding? = null
    private val sharedViewModel: UserModel by viewModels()
    private lateinit var navController: NavController
    lateinit var mSocket: Socket


    private val requiredPermissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO
    )

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAudioPermission()



        /**  [ChatSocket__OPEN]  */


        mSocket = ChatSocketApplication.get()
        mSocket.connect()


        navController = findNavController(R.id.home_fragment)

        setupActionBarWithNavController(navController)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tabs, menu)
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

        val audioRecordPermissionGranted =
            requestCode == HomeActivity.REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (!audioRecordPermissionGranted) {
            finish()
        }
    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, HomeActivity.REQUEST_RECORD_AUDIO_PERMISSION)
    }
}