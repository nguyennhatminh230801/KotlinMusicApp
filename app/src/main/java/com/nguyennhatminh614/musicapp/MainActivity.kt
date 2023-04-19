package com.nguyennhatminh614.musicapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.nguyennhatminh614.musicapp.adapter.SongAdapter
import com.nguyennhatminh614.musicapp.databinding.ActivityMainBinding
import com.nguyennhatminh614.musicapp.model.Song
import com.nguyennhatminh614.musicapp.presenter.MusicPresenter
import com.nguyennhatminh614.musicapp.presenter.SongPresenter
import com.nguyennhatminh614.musicapp.presenter.contract.MusicContract
import com.nguyennhatminh614.musicapp.utils.Constant
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.createImageUriWithThisID
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.loadGlideImageWithURI
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
        setContentView(binding.root)

        grantPermission()

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val songPresenter by lazy { SongPresenter() }
            val songList: ArrayList<Song> = ArrayList()
            songList.addAll(songPresenter.loadMusic(contentResolver))

            with(binding) {
                if (songList.isNotEmpty()) {
                    rcSong.adapter = SongAdapter(songList, this@MainActivity) {
                        val position: Int = it
                        val thisSong = songList[position]
                        val musicPresenter = MusicPresenter(object : MusicContract.View {
                            override fun onLoadSongUpdateView(song: Song) {
                                tvAuthor.text = song.author
                                tvTitleSong.text = song.nameSong
                                imgSong.loadGlideImageWithURI(
                                    this@MainActivity,
                                    song.imageID.createImageUriWithThisID()
                                )
                            }

                            override fun onRunningSongUpdateView(song: Song) {

                            }

                        }, songList)

                        currentSongLayout.visibility = View.VISIBLE
                        tvAuthor.text = thisSong.author
                        tvTitleSong.text = thisSong.nameSong
                        imgSong.loadGlideImageWithURI(
                            this@MainActivity,
                            thisSong.imageID.createImageUriWithThisID()
                        )

                        btnPlayPauseSong.setOnClickListener {
                            musicPresenter.onClickPlayPauseSong {
                                val isPlaying: Boolean = it

                                if (isPlaying) {
                                    binding.btnPlayPauseSong.setImageResource(R.drawable.ic_baseline_play_circle_outline_64)
                                } else {
                                    binding.btnPlayPauseSong.setImageResource(R.drawable.ic_baseline_pause_circle_outline_64)
                                }
                            }
                        }

                        musicPresenter.onLoadSong(songList[position])

                        btnPreviousSong.setOnClickListener {
                            musicPresenter.onClickPreviousSong()
                        }

                        btnNextSong.setOnClickListener {
                            musicPresenter.onClickNextSong()
                        }

                        songPresenter.navigateToMusicActivityAndStartMusicService(
                            position,
                            songList,
                            this@MainActivity
                        )
                    }

                    rcSong.layoutManager = LinearLayoutManager(this@MainActivity)

                } else {
                    tvNoSong.isVisible = true
                }
            }
        }
    }

    private fun grantPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constant.REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constant.REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder(this).apply {
                        setTitle(Constant.PERMISSION_DENIED_ALERT_DIALOG_TITLE)
                        setMessage(Constant.PERMISSION_DENIED_ALERT_DIALOG_MESSAGE)
                        setPositiveButton(Constant.BUTTON_OK) { dialog, id ->
                            run {
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                                    Constant.REQUEST_CODE_READ_EXTERNAL_STORAGE
                                )
                            }
                        }
                        setNegativeButton(Constant.BUTTON_DENIED) { dialog, id ->
                            run {
                                exitProcess(0)
                            }
                        }
                    }.create().show()

                }
            }
        }
    }
}
