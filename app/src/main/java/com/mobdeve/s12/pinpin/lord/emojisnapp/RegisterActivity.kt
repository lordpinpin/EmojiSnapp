package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMenuBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.registerConfirmBtn.setOnClickListener {
            val isRegistrationSuccessful = registerUser()
            if (isRegistrationSuccessful) {
                val resultIntent = Intent()
                resultIntent.putExtra("REGISTER_STATUS", "success")
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        binding.registerBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(): Boolean {
        // TODO: Check registration
        return true
    }
}