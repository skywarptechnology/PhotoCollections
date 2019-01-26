package com.sky.photocollections.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sky.photocollections.R
import com.sky.photocollections.support.*
import kotlinx.android.synthetic.main.activity_photo_collection.*
import kotlinx.android.synthetic.main.content_main.*

class PhotoCollectionActivity : CoroutineActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        initList()
        fetchData()
    }

    private fun fetchData() = launchWithoutEx {
        val future = picturesApi.getPictures()
        val pictureList = future.callApi()
        pictureList.lg()
        setListData(pictureList)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * sets list data to the list view adapter
     */
    private fun setListData(list: List<PictureData>?) {
        list?.let {
            ui_photoCollection_recyclerV.adapter = PhotoCollectionsAdapter(list)
        }
    }

    /**
     * list view initialization
     */
    private fun initList() {
        ui_photoCollection_recyclerV.layoutManager = GridLayoutManager(this, 2)
    }
}
