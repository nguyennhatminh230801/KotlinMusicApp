package com.nguyennhatminh614.musicapp.utils

import android.media.MediaPlayer

object MyMusicPlayer {
    var currentIndex: Int = Constant.FIRST_MUSIC_INDEX
    private var instance: MediaPlayer? = null

    @JvmStatic
    fun getInstance() = if (instance != null) instance else MediaPlayer().also { instance = it }
}