package com.nguyennhatminh614.musicapp.presenter

import android.media.MediaPlayer
import com.nguyennhatminh614.musicapp.model.Song
import com.nguyennhatminh614.musicapp.presenter.contract.MusicContract
import com.nguyennhatminh614.musicapp.utils.MyMusicPlayer

class MusicPresenter() : MusicContract.Presenter {
    private lateinit var mMusicView: MusicContract.View
    private lateinit var listSong: ArrayList<Song>
    private val mediaPlayer: MediaPlayer? = MyMusicPlayer.getInstance()

    constructor(listSong: ArrayList<Song>) : this() {
        this.listSong = listSong
    }

    constructor(mMusicView: MusicContract.View, listSong: ArrayList<Song>) : this(listSong) {
        this.mMusicView = mMusicView
        this.listSong = listSong
    }

    override fun onLoadSong(song: Song) {
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(song.linkSong)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        mMusicView.onLoadSongUpdateView(song)
        mMusicView.onRunningSongUpdateView(song)
    }

    override fun onClickPreviousSong() {
        if (MyMusicPlayer.currentIndex == 0) {
            return
        }
        MyMusicPlayer.currentIndex--
        mediaPlayer?.reset()
        onLoadSong(listSong[MyMusicPlayer.currentIndex])
    }

    override fun onClickNextSong() {
        if (MyMusicPlayer.currentIndex == listSong.size - 1) {
            return
        }
        MyMusicPlayer.currentIndex++
        mediaPlayer?.reset()
        onLoadSong(listSong[MyMusicPlayer.currentIndex])
    }

    override fun onClickPlayPauseSong(onChangeIcon: (Boolean) -> Unit) {
        onChangeIcon(mediaPlayer?.isPlaying == true)

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer.pause()
        } else {
            mediaPlayer?.start()
        }
    }
}