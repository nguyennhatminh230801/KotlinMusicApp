package com.nguyennhatminh614.musicapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nguyennhatminh614.musicapp.service.MusicService
import com.nguyennhatminh614.musicapp.utils.Constant

class MusicBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val actionMusic = p1?.getIntExtra(Constant.MUSIC_ACTION, -1)
        val intent = Intent(p0, MusicService::class.java).apply {
            putExtra(Constant.MUSIC_ACTION_SERVICE, actionMusic)
        }
        p0?.startService(intent)
    }
}