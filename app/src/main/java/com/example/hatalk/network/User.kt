package com.example.hatalk.network

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
        val email: String
        )

data class LoginResponse (
        val accessToken: String,
//        val refreshToken: String
        )

data class SignUpRequest (
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
        val refreshToken: String
        )

