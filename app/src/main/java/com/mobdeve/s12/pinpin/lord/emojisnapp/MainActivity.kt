package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var registerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = Firebase.auth
        if(auth.currentUser != null) {
            loadMenu()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadMenu()
            }
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    loadMenu()
                } else {
                    // TODO: Failed login
                    Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
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

    fun loadMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("USER_TYPE", "LOGIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}