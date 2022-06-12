package com.bogdanov.movika

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val _currentVideo = MutableLiveData<Int>(R.raw.first_video)
    val currentVideo: LiveData<Int>
        get() = _currentVideo

    private var videoIndex = 0

    private val videos = listOf<Int>(R.raw.first_video, R.raw.second_video, R.raw.third_video)

    fun clickedButton(){
        if (videoIndex == videos.lastIndex) videoIndex = 0
        else videoIndex ++

        _currentVideo.value = videos[videoIndex]
    }

}