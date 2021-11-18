package com.example.hatalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

//    override fun onResume() {
//        super.onResume()
//        val emailView =
//            findViewById<TextView>(R.id.user_email)
//        val nameView =
//            findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.name_input)
//        val socialNumberView =
//            findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.social_number_input)
//        val carrierView =
//            findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.carrier_input)
//        val phoneNumberView =
//            findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.phone_number_input)
//        val nicknameView =
//            findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.nickname_input)
//        val photoUrlView =
//            findViewById<TextView>(R.id.user_profile_url)
//
//        val signUpButton = findViewById<Button>(R.id.signup)
//
//        signUpButton.setOnClickListener {
//            val intent = Intent(this, SignUpLoadingActivity::class.java)
//
//            intent.putExtra("email", emailView.text.toString())
//            intent.putExtra("name", nameView.editText?.text.toString())
//            intent.putExtra("socialNumber", socialNumberView.editText?.text.toString())
//            intent.putExtra("carrier", carrierView.editText?.text.toString())
//            intent.putExtra("phoneNumber", phoneNumberView.editText?.text.toString())
//            intent.putExtra("nickname", nicknameView.editText?.text.toString())
//            intent.putExtra("photoUrl", photoUrlView.text.toString())
//
//            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(resultCode == Activity.RESULT_OK) {
//            if(requestCode == OPEN_GALLERY) {
//                var currentImageUrl : Uri? = data?.data
//                Log.d("URL: ", currentImageUrl.toString())
//
//                try {
//                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
//                    val showImage = findViewById<ImageView>(R.id.show_image)
//                    showImage.setImageBitmap(bitmap)
//                } catch (e:Exception) {
//                    e.printStackTrace()
//                }
//            }
//        } else {
//            Log.d("ActivityResult", "something wrong")
//        }
//    }

//    private fun openGallery() {
//        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.setType("image/*")
//        startActivityForResult(intent, OPEN_GALLERY)
//    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}