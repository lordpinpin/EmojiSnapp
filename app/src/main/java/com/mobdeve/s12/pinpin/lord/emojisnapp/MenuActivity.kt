package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userType = intent.getStringExtra("USER_TYPE")

        // Configure the screen based on the user type
        if (userType == "LOGIN") {
            // Do something specific for logged-in users
            binding.welcomeTx.text = "Welcome back, User! \uD83D\uDE0A"
        } else if (userType == "GUEST") {
            // Do something specific for guest users
            binding.welcomeTx.text = "Welcome, Guest! \uD83D\uDE0A"
        }

        binding.editBtn.setOnClickListener {
            val intent = Intent(this, DeckActivity::class.java)
            intent.putExtra("CUR_LEVEL", 17)
            startActivity(intent)
        }

        binding.trackBtn.setOnClickListener {
            val intent = Intent(this, TrackActivity::class.java)
            intent.putExtra("CUR_LEVEL", 17)
            startActivity(intent)
        }

        binding.historyBtn.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


    }
}