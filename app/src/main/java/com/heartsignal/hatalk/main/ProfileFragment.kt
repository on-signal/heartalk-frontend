package com.heartsignal.hatalk.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.heartsignal.hatalk.MainActivity
import com.heartsignal.hatalk.R
import com.heartsignal.hatalk.databinding.FragmentMainHomeBinding
import com.heartsignal.hatalk.databinding.FragmentProfileBinding
import com.heartsignal.hatalk.main.userModel.UserModel
import com.heartsignal.hatalk.model.UserJoinModel
import com.heartsignal.hatalk.model.userInfo
import com.heartsignal.hatalk.network.DeleteUserRequest
import com.heartsignal.hatalk.network.DeleteUserResponse
import com.heartsignal.hatalk.network.UserApi
import com.kakao.sdk.user.UserApiClient
import com.skydoves.balloon.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private val TAG = "HEART"

    private var binding: FragmentProfileBinding? = null
    private val sharedViewModel: UserModel by activityViewModels()
    lateinit var tempContext: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        tempContext = context as HomeActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileEditButton: Button? = binding?.profileEditButton
        profileEditButton?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
        }


        val kakaoLogoutButton: Button? = binding?.kakaoLogoutButton
        kakaoLogoutButton?.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(context, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    activity?.let {
                        val intent = Intent(it, com.heartsignal.hatalk.MainActivity::class.java)
                        it.startActivity(intent)
                    }
                }
            }
        }
        val kakaoUnlinkButton: Button? = binding?.kakaoUnlinkButton
        kakaoUnlinkButton?.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(context, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.Main).launch {
                        val deleteUserRequest = DeleteUserRequest(sharedViewModel.kakaoUserId)
                        val deleteUserResponse: Response<DeleteUserResponse> = UserApi.retrofitService.deleteUser("Bearer ${sharedViewModel.accessToken}" ,deleteUserRequest)
                        if (deleteUserResponse.body()?.msg.toString() == "success") {
                            Log.d(TAG, "Delete Success")
                        } else {
                            Log.d(TAG, "Delete Fail")
                        }
                        activity?.let {
                            val intent = Intent(it, com.heartsignal.hatalk.MainActivity::class.java)
                            it.startActivity(intent)
                        }
                    }
                }
            }
        }
        // Profile balloon 설정
        val profileImage: CircleImageView? = binding?.profileImage
        Glide.with(this)
            .load(sharedViewModel.photoUrl)
            .into(profileImage!!)

        val balloon = Balloon.Builder(tempContext)
            .setLayout(R.layout.profile_balloon)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPosition(0.5f)
            .setWidthRatio(0.55f)
            .setHeight(250)
            .setCornerRadius(4f)
            .setBackgroundColor(ContextCompat.getColor(tempContext, R.color.black))
            .setBalloonAnimation(BalloonAnimation.CIRCULAR)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()


        profileImage.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        profileImage.showAlignBottom(balloon)
                    }
                }
                return true
            }
        })




//        balloon.dismiss()


    }
}