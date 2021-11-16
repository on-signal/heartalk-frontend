package com.example.hatalk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

//        val openGalleryButton = findViewById<Button>(R.id.image_input_button)
//
//        openGalleryButton.setOnClickListener {
//            openGallery()
//        }
    }

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
}