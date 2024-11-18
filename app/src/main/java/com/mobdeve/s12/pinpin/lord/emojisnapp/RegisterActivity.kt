package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMainBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMenuBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.registerConfirmBtn.setOnClickListener {
            val email = binding.registerEmailEt.text.toString()
            val password = binding.registerPasswordEt.text.toString()
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val resultIntent = Intent()
                    resultIntent.putExtra("REGISTER_STATUS", "success")
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

        binding.registerBackBtn.setOnClickListener {
            finish()
        }
    }
}