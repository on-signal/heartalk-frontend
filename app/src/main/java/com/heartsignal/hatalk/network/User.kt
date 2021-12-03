package com.heartsignal.hatalk.network

data class User (
        val name: String,
        val nickname: String,
        val gender: Int,
        val age: Int,
        val photoUrl: String,
        val accessToken: String,
        val refreshToken: String
        )

data class LoginRequest (
        val kakaoUserId: String
)

data class LoginResponse (
        val nickname: String,
        val accessToken: String
//        val refreshToken: String
        )

data class SignUpRequest (
        val kakaoUserId: String,
        val email: String,
        val name: String,
        val socialNumber: String,
        val carrier: String,
        val phoneNumber: String,
        val nickname: String,
        val photoUrl: String
        )

data class SignUpResponse (
        val accessToken: String,
//        val refreshToken: String
        )

data class GetProfileResponse(
        val kakaoUserId: String,
        val email: String,
        val name: String,
        val nickname: String,
        val photoUrl: String,
        val gender: Int,
        val age: Int
)


data class DeleteUserRequest(
        val kakaoUserId: String
)

data class DeleteUserResponse(
        val msg: String
)

data class UpdateUserRequest(
        val kakaoUserId: String,
        val nickname: String,
        val email: String,
        val phoneNumber: String
)

data class UpdateUserResponse(
        val msg: String
)