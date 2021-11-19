package com.example.hatalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.hatalk.model.UserJoinModel


class SignUpActivity : AppCompatActivity(R.layout.activity_sign_up) {
    private lateinit var navController: NavController
    private val viewModel: UserJoinModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val email = intent?.extras?.getString("email").toString()
        val photoUrl = intent?.extras?.getString("photoUrl").toString()

        viewModel.setEmail(email)
        viewModel.setProfileUrl(photoUrl)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.signup_nav_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}