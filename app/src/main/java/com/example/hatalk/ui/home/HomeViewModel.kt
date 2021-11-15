package com.example.hatalk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text=MutableLiveData<String>().apply {
        value="매칭하기"
    }
    val text: LiveData<String> = _text
}