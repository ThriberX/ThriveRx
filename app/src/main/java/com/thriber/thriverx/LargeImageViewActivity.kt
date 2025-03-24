package com.thriber.thriverx

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class LargeImageViewActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_large_image_view)

        val imageView = findViewById<ImageView>(R.id.largeImageView)

        // Retrieve the image URI from the intent
        val imageUri = intent.getStringExtra("imageUri")
        val showuri = Uri.parse(imageUri)
        Log.d("LargeImageViewActivity", "Image URI: $imageUri")
       imageView.setImageURI(showuri)
    }
}
