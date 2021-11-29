package com.example.hatalk.main

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
import com.example.hatalk.R
import com.example.hatalk.databinding.FragmentSignUpLoadingBinding
import com.example.hatalk.main.chat.ChatSocketApplication
import com.example.hatalk.main.data.MatchingConfirmData
import com.example.hatalk.main.data.MatchingConfirmResponse
import com.example.hatalk.main.data.Partner
import com.example.hatalk.main.data.testData
import com.example.hatalk.main.userModel.UserModel
import com.example.hatalk.model.userInfo
import com.example.hatalk.network.UserApi
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


class HomeActivity : AppCompatActivity(R.layout.activity_home) {
    private val TAG = "HEART"
    private var binding: FragmentSignUpLoadingBinding? = null
    private val sharedViewModel: UserModel by viewModels()
    private lateinit var navController: NavController
    lateinit var mSocket: Socket
    lateinit var partner: Partner
    var users: Array<String> = arrayOf()


    private val requiredPermissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO
    )

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAudioPermission()


        val userInfo = intent?.getParcelableExtra<userInfo>("userInfo")

        CoroutineScope(Dispatchers.IO).launch {
            val getProfileResponse =
                UserApi.retrofitService.getCurrentUser("Bearer ${userInfo?.accessToken}")
            val profile = getProfileResponse.body()
            sharedViewModel.setEmail(profile?.email.toString())
            sharedViewModel.setName(profile?.name.toString())
            sharedViewModel.setNickname(profile?.nickname.toString())
            sharedViewModel.setProfileUrl(profile?.photoUrl.toString())
            sharedViewModel.setGender(profile?.gender.toString())
            sharedViewModel.setAge(profile!!.age)
            sharedViewModel.setKakaoUserId(userInfo!!.kakaoUserId)
            sharedViewModel.setAccessToken(userInfo.accessToken)
        }

        /**
         * [ChatSocket__OPEN]
         */
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