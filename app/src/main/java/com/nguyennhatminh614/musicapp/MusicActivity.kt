package com.nguyennhatminh614.musicapp

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.nguyennhatminh614.musicapp.databinding.ActivityMusicBinding
import com.nguyennhatminh614.musicapp.model.Song
import com.nguyennhatminh614.musicapp.presenter.MusicPresenter
import com.nguyennhatminh614.musicapp.presenter.contract.MusicContract
import com.nguyennhatminh614.musicapp.receiver.MusicBroadcastReceiver
import com.nguyennhatminh614.musicapp.service.MusicService
import com.nguyennhatminh614.musicapp.utils.Constant
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.createImageUriWithThisID
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.loadGlideImageWithCircleCropWithURI
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.toDateTimeMMSS
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.updateAngle
import com.nguyennhatminh614.musicapp.utils.MyMusicPlayer
import com.nguyennhatminh614.musicapp.utils.SendRequestToActivity
import java.util.*
import kotlin.concurrent.schedule

class MusicActivity : AppCompatActivity() {
    private val binding: ActivityMusicBinding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private val songList: ArrayList<Song> = ArrayList()
    private lateinit var updateUIReceiver: MusicBroadcastReceiver

    companion object {
        private const val ROTATE_ANGLE = 0.1f
        private const val FIRST_ANGLE = 0f
        private var currentAngle: Float = 0f
        private var currentPosition: Int = 0
        private var isChecked = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent.getParcelableArrayListExtra<Song>(Constant.KEY_SONG_OBJECT)?.apply {
            songList.addAll(this)
        }

        val musicPresenter by lazy {
            MusicPresenter(
                object : MusicContract.View {
                    override fun onLoadSongUpdateView(song: Song) {
                        with(binding) {
                            tvTitleSong.text = song.nameSong
                            imgSong.loadGlideImageWithCircleCropWithURI(
                                this@MusicActivity,
                                song.imageID.createImageUriWithThisID(),
                            )
                            imgSong.rotation = FIRST_ANGLE
                            tvCurrentTime.text = Constant.FIRST_LOAD_MUSIC
                            tvTotalTime.text = song.duration.toDateTimeMMSS()
                            sbProgressTime.progress = Constant.FIRST_TIME_SEEK_BAR
                            sbProgressTime.max = MyMusicPlayer.getInstance()?.duration!!
                            sbProgressTime.setOnSeekBarChangeListener(object :
                                SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                                    if (MyMusicPlayer.getInstance() != null && p2) {
                                        MyMusicPlayer.getInstance()?.seekTo(p1)
                                    }
                                }

                                override fun onStartTrackingTouch(p0: SeekBar?) {

                                }

                                override fun onStopTrackingTouch(p0: SeekBar?) {

                                }

                            })
                        }
                    }

                    override fun onRunningSongUpdateView(song: Song) {
                        Timer().schedule(0, 10) {
                            this@MusicActivity.runOnUiThread {
                                with(binding) {
                                    if (currentPosition != MyMusicPlayer.getInstance()?.currentPosition!!) {
                                        sbProgressTime.progress =
                                            MyMusicPlayer.getInstance()?.currentPosition!!
                                        MyMusicPlayer.getInstance()?.currentPosition?.toLong()
                                            ?.toDateTimeMMSS().also { tvCurrentTime.text = it }
                                        imgSong.rotation = currentAngle
                                        currentAngle = currentAngle.updateAngle(ROTATE_ANGLE)
                                        currentPosition =
                                            MyMusicPlayer.getInstance()?.currentPosition!!
                                    }
                                }
                            }
                        }
                    }
                }, songList
            )
        }

        //First Init
        musicPresenter.onLoadSong(songList[MyMusicPlayer.currentIndex])

        MusicService.sendRequestToActivity = object : SendRequestToActivity {
            override fun onUpdatePlayOrPauseSong(isPlaying: Boolean) {
                musicPresenter.onClickPlayPauseSong {
                    val isPlaying: Boolean = it
                    if (isPlaying) {
                        binding.btnPlayPauseSong.setImageResource(R.drawable.ic_baseline_play_circle_outline_64)
                    } else {
                        binding.btnPlayPauseSong.setImageResource(R.drawable.ic_baseline_pause_circle_outline_64)
                    }
                }
            }

            override fun onUpdateNextSong() {
                musicPresenter.onLoadSong(songList[MyMusicPlayer.currentIndex])
            }

            override fun onUpdatePreviousSong() {
                musicPresenter.onLoadSong(songList[MyMusicPlayer.currentIndex])
            }
        }

        with(binding) {
            btnPreviousSong.setOnClickListener {
                musicPresenter.onClickPreviousSong()
                sendBroadcast(Intent(this@MusicActivity, MusicBroadcastReceiver::class.java).apply {
                    putExtra(Constant.MUSIC_ACTION, Constant.ACTION_PREVIOUS_SONG)
                })
            }

            btnNextSong.setOnClickListener {
                musicPresenter.onClickNextSong()
                sendBroadcast(Intent(this@MusicActivity, MusicBroadcastReceiver::class.java).apply {
                    putExtra(Constant.MUSIC_ACTION, Constant.ACTION_NEXT_SONG)
                })
            }

            btnPlayPauseSong.setOnClickListener {
                musicPresenter.onClickPlayPauseSong {
                    val isPlaying: Boolean = it

                    if (isPlaying) {
                        btnPlayPauseSong.setImageResource(R.drawable.ic_baseline_play_circle_outline_64)

                    } else {
                        btnPlayPauseSong.setImageResource(R.drawable.ic_baseline_pause_circle_outline_64)
                    }
                }
            }
        }
    }
}