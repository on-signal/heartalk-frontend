package com.example.hatalk.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.hatalk.MainActivity
import com.example.hatalk.R
import com.example.hatalk.model.userInfo
import com.example.hatalk.network.MatchingConfirmResponse
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity(R.layout.activity_home) {
    private lateinit var navController: NavController

    private val requiredPermissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO
    )

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requesAudioPermission()


        navController = findNavController(R.id.home_fragment)
//        val intent: Intent = getIntent()
//        val userInfo = intent.getParcelableExtra<userInfo>("userInfo")
//        Log.d("TESTEST", userInfo.toString())
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

    private fun requesAudioPermission() {
        requestPermissions(requiredPermissions, HomeActivity.REQUEST_RECORD_AUDIO_PERMISSION)
    }
}