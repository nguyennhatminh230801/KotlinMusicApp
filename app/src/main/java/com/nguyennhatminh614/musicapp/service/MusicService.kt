package com.nguyennhatminh614.musicapp.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.nguyennhatminh614.musicapp.MusicActivity
import com.nguyennhatminh614.musicapp.MyApplication
import com.nguyennhatminh614.musicapp.R
import com.nguyennhatminh614.musicapp.model.Song
import com.nguyennhatminh614.musicapp.receiver.MusicBroadcastReceiver
import com.nguyennhatminh614.musicapp.utils.Constant
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.createImageUriWithThisID
import com.nguyennhatminh614.musicapp.utils.MyMusicPlayer
import com.nguyennhatminh614.musicapp.utils.SendRequestToActivity

class MusicService : Service() {
    private val listSong: ArrayList<Song> by lazy { ArrayList() }
    private val mediaPlayer: MediaPlayer? = MyMusicPlayer.getInstance()

    companion object{
       lateinit var sendRequestToActivity: SendRequestToActivity
       private var isChecked = false
    }
    override fun onBind(p0: Intent?): IBinder? {
        return MyLocalBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getParcelableArrayListExtra<Song>(Constant.KEY_SONG_OBJECT)
            ?.let { listSong.addAll(it) }
        createNotification()
        val actionMusic = intent?.getIntExtra(Constant.MUSIC_ACTION_SERVICE, Constant.DEFAULT_VALUE_MUSIC_ACTION_SERVICE)
        actionMusic?.let { handleMusicAction(it) }
        return START_STICKY
    }

    private fun handleMusicAction(action: Int) {
        when (action) {
            Constant.ACTION_PAUSE -> sendRequestToActivity.onUpdatePlayOrPauseSong(false)

            Constant.ACTION_NEXT_SONG -> {
                nextSong()
                sendRequestToActivity.onUpdateNextSong()
            }
            Constant.ACTION_PREVIOUS_SONG -> {
                previousSong()
                sendRequestToActivity.onUpdatePreviousSong()
            }
            Constant.ACTION_PLAY -> sendRequestToActivity.onUpdatePlayOrPauseSong(true)
        }
    }

    private fun createNotification() {
        val song = listSong[MyMusicPlayer.currentIndex]
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this@MusicService, MusicActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val remoteView = RemoteViews(packageName, R.layout.current_song_layout).apply {
            setTextViewText(R.id.tvTitleSong, song.nameSong)
            setTextViewText(R.id.tvAuthor, song.author)

            if (MyMusicPlayer.getInstance()?.isPlaying == true) {
                setOnClickPendingIntent(
                    R.id.btnPlayPauseSong,
                    getPendingIntent(this@MusicService, Constant.ACTION_PAUSE)
                )
                setImageViewResource(
                    R.id.btnPlayPauseSong,
                    R.drawable.ic_baseline_play_circle_outline_64
                )
            } else {
                setOnClickPendingIntent(
                    R.id.btnPlayPauseSong,
                    getPendingIntent(this@MusicService, Constant.ACTION_PLAY)
                )
                setImageViewResource(
                    R.id.btnPlayPauseSong,
                    R.drawable.ic_baseline_pause_circle_outline_64
                )
            }

            if(!isChecked){
                setImageViewResource(
                    R.id.btnPlayPauseSong,
                    R.drawable.ic_baseline_pause_circle_outline_64
                )
                isChecked = true
            }
            setOnClickPendingIntent(
                R.id.btnNextSong,
                getPendingIntent(this@MusicService, Constant.ACTION_NEXT_SONG)
            )
            setOnClickPendingIntent(
                R.id.btnPreviousSong,
                getPendingIntent(this@MusicService, Constant.ACTION_PREVIOUS_SONG)
            )
        }

        val notification = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_baseline_music_note_24)
            setContentIntent(pendingIntent)
            setCustomContentView(remoteView)
        }.build()

        val notificationTarget = NotificationTarget(
            this,
            R.id.imgSong,
            remoteView,
            notification,
            Constant.NOTIFICATION_ID
        )

        Glide.with(applicationContext)
            .asBitmap()
            .placeholder(R.drawable.ic_baseline_music_note_24)
            .load(song.imageID.createImageUriWithThisID())
            .into(notificationTarget)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
    private fun getPendingIntent(context: Context, action: Int): PendingIntent? {
        val intent = Intent(context, MusicBroadcastReceiver::class.java).apply {
            putExtra(Constant.MUSIC_ACTION, action)
        }
        return PendingIntent.getBroadcast(
            context.applicationContext,
            action,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun nextSong(){
        if (MyMusicPlayer.currentIndex == listSong.size - 1) {
            return
        }
        MyMusicPlayer.currentIndex++
        mediaPlayer?.reset()
        createNotification()
        isChecked = false
    }

    private fun previousSong(){
        if (MyMusicPlayer.currentIndex == 0) {
            return
        }
        MyMusicPlayer.currentIndex--
        mediaPlayer?.reset()
        createNotification()
        isChecked = false
    }

    inner class MyLocalBinder : Binder(){
        fun getService(): MusicService = this@MusicService
    }
}
