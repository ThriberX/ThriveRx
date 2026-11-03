package com.thriber.thriverx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import com.thriber.thriverx.user_creation.navigation.RegistrationNavHost

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseDao().UserId().isNotEmpty()) {
                openPreview()
            } else {
                setContent {
                    MaterialTheme {
                        RegistrationNavHost(onAuthenticated = { openPreview() })
                    }
                }
            }
        }, 1500)
    }

    private fun openPreview() {
        startActivity(Intent(this, PreviewActivity::class.java))
        finish()
    }
}