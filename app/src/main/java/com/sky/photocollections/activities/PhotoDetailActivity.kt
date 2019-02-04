package com.sky.photocollections.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sky.photocollections.R
import com.sky.photocollections.support.getLargeImgPath
import kotlinx.android.synthetic.main.activity_base_layout.*
import kotlinx.android.synthetic.main.content_detail.*

/**
 * Created by skyalligator on 2/3/19.
 * 6:40 PM
 */
class PhotoDetailActivity : AppCompatActivity() {
    lateinit var title: String
    lateinit var publishedAt: String
    lateinit var comment: String
    lateinit var imageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_layout)

        layoutInflater.inflate(R.layout.content_detail, ui_base_includeV, true)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Photo Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab.hide()

        val extras = intent.extras
        if (extras == null) {
            Toast.makeText(this, "No Data to show", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        title = extras.getString("title")!!
        publishedAt = extras.getString("publishedAt")!!
        comment = extras.getString("comment")!!
        imageUri = extras.getString("imageUri")!!

        ui_frag_phDetail_titleV.text = title
        ui_frag_phDetail_commentV.text = comment
        ui_frag_phDetail_publishAtV.text = publishedAt
        ui_frag_phDetail_imgV.setImageBitmap(BitmapFactory.decodeFile(getLargeImgPath(title)))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home ->
                finish()
        }
        return super.onOptionsItemSelected(item)
    }
}