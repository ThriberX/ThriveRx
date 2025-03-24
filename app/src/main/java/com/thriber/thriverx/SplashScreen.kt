package com.thriber.thriverx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.thriber.thriverx.FirebaseClass.FirebaseDao
import com.thriber.thriverx.user_creation.IntroActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({

              val currretUserId=FirebaseDao().UserId()

            if (currretUserId.isNotEmpty()) {
                startActivity(Intent(this,PreviewActivity::class.java))
            } else {
                startActivity(Intent(this,IntroActivity::class.java))
            }
            finish()
        }, 1500)
    }
}