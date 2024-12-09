package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val user = FirebaseAuth.getInstance().currentUser;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userType = intent.getStringExtra("USER_TYPE")

        val decksReference = FirebaseDatabase.getInstance().getReference("currentDecksChosen")
        val currentUserUid = Firebase.auth.currentUser?.uid

        if (currentUserUid != null) {
            decksReference.child(currentUserUid).get().addOnSuccessListener { snapshot ->
                // Check if the user already has a deck saved
                val savedDeckJson = snapshot.getValue(String::class.java)

                val currentDeck = if (savedDeckJson != null) {
                    var savedDeck = Gson().fromJson(savedDeckJson, Deck::class.java)
                    savedDeck // use the saved deck
                } else {
                    // If no saved deck, use the dummy deck
                    Deck("Basic Deck", DataGenerator.loadActiveBasicData())
                }

                // Proceed with using the currentDeck (either saved or dummy)
                // Example usage:
                Log.d("MenuActivity", "Current deck: ${currentDeck.getTitle()}")

                // Now you can pass the deck to other activities if needed
                // Example: startActivity(intent.putExtra("DECK", currentDeck))
            }
                .addOnFailureListener { exception ->
                    Log.e("MenuActivity", "Error fetching deck: ${exception.message}")
                }
        }

        // Configure the screen based on the user type
        if (userType == "LOGIN") {
            // Do something specific for logged-in users
            binding.welcomeTx.text = "Welcome back, ${user?.email ?: "User"}! \uD83D\uDE0A"
        } else if (userType == "GUEST") {
            // Do something specific for guest users
            binding.welcomeTx.text = "Welcome, Guest! \uD83D\uDE0A"
        }

        binding.playBtn.setOnClickListener {
            val intent = Intent(this, WaitActivity::class.java)
            startActivity(intent)
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
            Firebase.auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


    }
}