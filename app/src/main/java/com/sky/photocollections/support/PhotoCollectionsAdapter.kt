package com.sky.photocollections.support

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sky.photocollections.R
import kotlinx.android.synthetic.main.item_photo_collection.view.*

/**
 * Created by skyalligator on 1/25/19.
 * 10:35 PM
 */
class PhotoCollectionsAdapter(val photos: List<PictureData>, val onClick: (PictureData) -> Unit) :
    RecyclerView.Adapter<PhotoCollectionsAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val itemV = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_collection, parent, false)
        return PhotoHolder(itemV, onClick)
    }

    override fun getItemCount() = photos.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        holder.setValToViews(photos[position])
    }

    class PhotoHolder(itemV: View, val onClick: (PictureData) -> Unit) : RecyclerView.ViewHolder(itemV) {

        fun setValToViews(photo: PictureData) {
            val bitmap = BitmapFactory.decodeFile(getSmallImgPath(photo.title))

            itemView.ui_item_PlaceList_nameV.text = photo.title
            itemView.ui_item_PlaceList_IconV.setImageBitmap(bitmap)
            itemView.ui_item_PlaceList_IconV.setOnClickListener {
                onClick(photo)
            }
        }
    }
}