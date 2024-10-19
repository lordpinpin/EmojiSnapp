package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var registerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                attemptRegister()
            }
        }

        binding.loginBtn.setOnClickListener {
            if(attemptLogin()){
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("USER_TYPE", "LOGIN")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // TODO: Failed login
            }
        }

        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            registerLauncher.launch(intent)
        }

        binding.guestBtn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("USER_TYPE", "GUEST")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun attemptLogin(): Boolean {
        // TODO: Login logic
        return true
    }

    private fun attemptRegister(): Boolean {
        // TODO: Login register
        return true
    }

}