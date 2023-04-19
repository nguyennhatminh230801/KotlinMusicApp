package com.nguyennhatminh614.musicapp.presenter.contract

import android.content.ContentResolver
import android.content.Context
import com.nguyennhatminh614.musicapp.model.Song

class SongContract {
    interface Presenter {
        fun loadMusic(contentResolver: ContentResolver): ArrayList<Song>
        fun navigateToMusicActivityAndStartMusicService(position: Int, songList: ArrayList<Song>, context: Context)
    }
}