package com.nguyennhatminh614.musicapp.utils

interface SendRequestToActivity {
    fun onUpdatePlayOrPauseSong(isPlaying: Boolean)
    fun onUpdateNextSong()
    fun onUpdatePreviousSong()
}