package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.VideoView
import com.mobdeve.s12.pinpin.lord.emojisnapp.R
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            // TODO: Add login logic here.

            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("USER_TYPE", "LOGIN")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.guestBtn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("USER_TYPE", "GUEST")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }



    }

}