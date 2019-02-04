package com.sky.photocollections.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sky.photocollections.R
import com.sky.photocollections.support.*
import kotlinx.android.synthetic.main.activity_base_layout.*
import kotlinx.android.synthetic.main.content_collection.*
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.*


class PhotoCollectionActivity : CoroutineActivity() {
    private val REQUEST_TAKE_PHOTO = 1
    private var pictureList = ArrayList<PictureData>()

    private var mCurrentPhotoPath: String? = null
    private var adapter: PhotoCollectionsAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_layout)

        layoutInflater.inflate(R.layout.content_collection, ui_base_includeV, true)
        setSupportActionBar(toolbar)

        photoDir = applicationContext.filesDir.toString()
        fab.setOnClickListener { dispatchTakePictureIntent() }
        addCapturedPhotos()
        initList()
        fetchData()
    }

    private fun addCapturedPhotos() {
        val file = File(LARGE_IMG_PATH)
        if (file.exists()) {
            val list = file.list { dir, name ->
                name.contains(CAPTURE_NAME)
            }.mapNotNull {
                getCapturedPictureData(it)
            }
            pictureList.addAll(list)
        }
    }

    private fun getCapturedPictureData(it: String) = try {
        val format = it.split("_")[1]
        val date = DateFormat.getDateTimeInstance().parse(format)
        PictureData("My Picture", it, "-99", date.toString(), it)
    } catch (e: Exception) {
        null
    }

    private fun fetchData() = launchWithoutEx {
        val elements = picturesApi.getPictures().call()
        pictureList.addAll(elements)
        saveImages(pictureList)
        setListData(pictureList)
    }

    suspend fun saveImages(pictureList: List<PictureData>) {
        ui_photoCollection_progContainerV.visibility = View.VISIBLE
        ui_photoCollection_progressV.max = pictureList.size

        for ((index, data) in pictureList.withIndex()) {
            ui_photoCollection_progressV.progress = index

            if (File(getLargeImgPath(data.title)).exists())
                continue

            val bitmap = downloadImageBackground(data.picture)
            resizeAndWriteImage(bitmap, data.title)
        }
        ui_photoCollection_progContainerV.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                deleteAll()
                fetchData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteAll() {
        pictureList.clear()
        adapter?.notifyDataSetChanged()

        val largeDir = File(LARGE_IMG_PATH)
        val smallDir = File(SMALL_IMG_PATH)
        largeDir.deleteRecursively()
        smallDir.deleteRecursively()
    }

    /**
     * sets list data to the list view adapter
     */
    private fun setListData(list: List<PictureData>) {
        list.let { lst ->
            adapter = PhotoCollectionsAdapter(lst) {

                val intent = Intent(this, PhotoDetailActivity::class.java)
                intent.putExtra("title", it.title)
                intent.putExtra("publishedAt", it.publishedAt)
                intent.putExtra("comment", it.comment)
                intent.putExtra("imageUri", it.picture)
                startActivity(intent)
            }
            ui_photoCollection_recyclerV.adapter = adapter
        }
    }

    /**
     * list view initialization
     */
    private fun initList() {
        ui_photoCollection_recyclerV.layoutManager = LinearLayoutManager(this)
    }

    private fun dispatchTakePictureIntent() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val componentName = intent.resolveActivity(packageManager)
            val imageFile = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                packageName,
                imageFile
            )

            grantUriPermission(
                componentName.packageName,
                photoURI,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_CapturedImage_", ".jpg", storageDir).apply {
            mCurrentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            val fileName = generateName()
            resizeAndWriteImage(bitmap, fileName)
            pictureList.add(PictureData("My Picture", fileName, "-99", getTimeStamp(), fileName))
            adapter?.notifyDataSetChanged()
        }
    }
}
