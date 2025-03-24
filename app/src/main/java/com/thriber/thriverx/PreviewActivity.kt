package com.thriber.thriverx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView

class PreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            PatientList { itemId ->
                val intent = Intent(this@PreviewActivity, ImageGalleryActivity::class.java).apply {
                    putExtra("itemId", itemId)
                }
                startActivity(intent)
            }
        }
    }
}