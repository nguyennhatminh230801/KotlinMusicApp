package com.nguyennhatminh614.musicapp.presenter.contract

import com.nguyennhatminh614.musicapp.model.Song

class MusicContract {
    interface View {
        fun onLoadSongUpdateView(song: Song)
        fun onRunningSongUpdateView(song: Song)
    }

    interface Presenter {
        fun onLoadSong(song: Song)
        fun onClickPreviousSong()
        fun onClickNextSong()
        fun onClickPlayPauseSong(onChangeIcon: (Boolean) -> Unit)
    }
}