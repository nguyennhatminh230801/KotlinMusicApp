package com.nguyennhatminh614.musicapp.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.nguyennhatminh614.musicapp.R
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.loadGlideImageWithCircleCropWithURI
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.loadGlideImageWithURI
import java.util.concurrent.TimeUnit

class GlobalExtensionFunction {
    companion object {
        fun Long.createImageUriWithThisID(): Uri {
            return ContentUris.withAppendedId(Uri.parse(Constant.AUDIO_IMAGE_URI), this)
        }

        fun Long.toDateTimeMMSS(): String {
            return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(this) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(this) % TimeUnit.HOURS.toMinutes(1)
            )
        }

        fun ImageView.loadGlideImageWithURI(context: Context, uri: Uri) {
            Glide.with(context).load(uri).placeholder(R.drawable.ic_baseline_music_note_24)
                .into(this)
        }

        fun ImageView.loadGlideImageWithCircleCropWithURI(context: Context, uri: Uri) {
            Glide.with(context).load(uri).placeholder(R.drawable.ic_baseline_music_note_24)
                .circleCrop().into(this)
        }

        fun Float.updateAngle(rotateAngle: Float): Float {
            var x = this
            x += rotateAngle
            x %= 360
            return x
        }
    }
}