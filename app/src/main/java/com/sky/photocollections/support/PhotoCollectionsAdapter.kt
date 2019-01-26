package com.sky.photocollections.support

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sky.photocollections.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_photo_collection.view.*

/**
 * Created by skyalligator on 1/25/19.
 * 10:35 PM
 */
class PhotoCollectionsAdapter(val photos: List<PictureData>) :
    RecyclerView.Adapter<PhotoCollectionsAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val itemV = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_collection, parent, false)
        return PhotoHolder(itemV)
    }

    override fun getItemCount() = photos.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        holder.setValToViews(photos[position])
    }

    class PhotoHolder(itemV: View) : RecyclerView.ViewHolder(itemV) {

        fun setValToViews(photo: PictureData) {
            Picasso.get()
                .load(Uri.parse(photo.picture))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(itemView.ui_item_PlaceList_IconV)
        }
    }
}