package com.nguyennhatminh614.musicapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val linkSong: String,
    val imageID: Long,
    val nameSong: String,
    val author: String,
    val duration: Long
) : Parcelable
