package com.nguyennhatminh614.musicapp.presenter

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.MediaStore
import com.nguyennhatminh614.musicapp.MusicActivity
import com.nguyennhatminh614.musicapp.model.Song
import com.nguyennhatminh614.musicapp.presenter.contract.SongContract
import com.nguyennhatminh614.musicapp.service.MusicService
import com.nguyennhatminh614.musicapp.utils.Constant
import com.nguyennhatminh614.musicapp.utils.MyMusicPlayer

class SongPresenter : SongContract.Presenter {
    override fun loadMusic(contentResolver: ContentResolver): ArrayList<Song> {
        val selection: String = MediaStore.Audio.Media.IS_MUSIC + " != 0 "
        val cursor: Cursor by lazy {
            contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                selection,
                null,
                null,
                null
            )!!
        }

        val songList: ArrayList<Song> = ArrayList()
        while (cursor.moveToNext()) {
            with(cursor) {
                val linkSong: String =
                    if (getColumnIndex(MediaStore.Audio.Media.DATA) < 0) "" else getString(
                        getColumnIndexOrThrow(
                            MediaStore.Audio.Media.DATA
                        )
                    )
                val imageID: Long =
                    if (getColumnIndex(MediaStore.Audio.Media.ALBUM_ID) < 0) 0 else getLong(
                        getColumnIndexOrThrow(
                            MediaStore.Audio.Media.ALBUM_ID
                        )
                    )
                val nameSong: String =
                    if (getColumnIndex(MediaStore.Audio.Media.TITLE) < 0) "" else getString(
                        getColumnIndexOrThrow(
                            MediaStore.Audio.Media.TITLE
                        )
                    )
                val author: String =
                    if (getColumnIndex(MediaStore.Audio.Media.ARTIST) < 0) "" else getString(
                        getColumnIndexOrThrow(
                            MediaStore.Audio.Media.ARTIST
                        )
                    )
                val duration: Long =
                    if (getColumnIndex(MediaStore.Audio.Media.DURATION) < 0) 0 else getLong(
                        getColumnIndexOrThrow(
                            MediaStore.Audio.Media.DURATION
                        )
                    )

                songList.add(Song(linkSong, imageID, nameSong, author, duration))
            }
        }

        return songList
    }

    override fun navigateToMusicActivityAndStartMusicService(
        position: Int,
        songList: ArrayList<Song>,
        context: Context
    ) {
        MyMusicPlayer.currentIndex = position
        val intent = Intent(context, MusicActivity::class.java)
        intent.putExtra(Constant.KEY_SONG_OBJECT, songList)
        context.startService(Intent(context, MusicService::class.java).apply { putExtra(Constant.KEY_SONG_OBJECT, songList) })
        context.startActivity(intent)
    }
}