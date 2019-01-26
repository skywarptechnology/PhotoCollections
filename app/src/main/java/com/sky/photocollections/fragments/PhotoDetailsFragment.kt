package com.sky.photocollections.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sky.photocollections.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photo_detail.*

/**
 * Created by skyalligator on 1/26/19.
 * 1:00 PM
 */
class PhotoDetailsFragment : ParentFragment() {

    lateinit var title: String
    lateinit var publishedAt: String
    lateinit var comment: String
    lateinit var imageUri: String

    companion object {
        fun getInstance(title: String, publishedAt: String, comment: String, imageUri: String): PhotoDetailsFragment {
            val fragment = PhotoDetailsFragment()
            fragment.title = title
            fragment.publishedAt = publishedAt
            fragment.comment = comment
            fragment.imageUri = imageUri
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui_frag_phDetail_titleV.text = title
        ui_frag_phDetail_commentV.text = comment
        ui_frag_phDetail_publishAtV.text = publishedAt

        Picasso.get()
            .load(Uri.parse(imageUri))
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(ui_frag_phDetail_imgV)

    }
}