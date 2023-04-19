package com.nguyennhatminh614.musicapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nguyennhatminh614.musicapp.R
import com.nguyennhatminh614.musicapp.databinding.SongItemLayoutBinding
import com.nguyennhatminh614.musicapp.model.Song
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.createImageUriWithThisID
import com.nguyennhatminh614.musicapp.utils.GlobalExtensionFunction.Companion.loadGlideImageWithURI

class SongAdapter(
    private val listSong: ArrayList<Song>,
    private val context: Context,
    private val clickInterface: (Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        val binding = SongItemLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.song_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song: Song = listSong[position]

        holder.binding.apply {
            tvTitleSong.text = song.nameSong
            tvAuthor.text = song.author
            imgSong.loadGlideImageWithURI(context, song.imageID.createImageUriWithThisID())
            songItem.setOnClickListener {
                clickInterface(position)
            }
        }
    }

    override fun getItemCount(): Int = listSong.size
}