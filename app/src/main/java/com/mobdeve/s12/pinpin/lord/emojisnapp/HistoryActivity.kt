package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityHistoryBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DialogEmojiDetailsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Makes B&W
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        binding.historyBackBtn.paint.colorFilter = colorFilter

        binding.historyBackBtn.setOnClickListener {
            finish()
        }

        var matches = emptyList<Match>()



        binding.matchRv.layoutManager = LinearLayoutManager(this)
        binding.matchRv.adapter = MatchAdapter(matches) { emoji ->
            showEmojiDetailsPopup(emoji)
        }


    }

    private fun showEmojiDetailsPopup(emoji: Emoji) {

        val binding = DialogEmojiDetailsBinding.inflate(layoutInflater)
        binding.detailEmojiIconTx.text = emoji.icon
        binding.detailEmojiCostTx.text = "${emoji.baseCost}"
        binding.detailEmojiPowerTx.text = "${emoji.basePower}"
        binding.detailEmojiNameTx.text = emoji.name
        binding.detailEmojiDescTx.setText(emoji.description)

        binding.addBtn.visibility = View.GONE
        binding.modifyBtn.visibility = View.GONE
        binding.removeBtn.visibility = View.GONE


        val dialog = AlertDialog.Builder(this, R.style.TransparentAlertDialog)
            .setView(binding.root)
            .create()

        dialog.show()
    }


    fun convertMatchResultToMatches(matchResults: List<Map<String, Any>>, currentUuid: String): List<Match> {
        val gson = Gson()

        return matchResults.filter { matchResult ->
            // Check if player1 is the current UUID
            matchResult["player1"] == currentUuid
        }.map { matchResult ->
            val playerName = matchResult["player1"] as String
            val oppName = matchResult["player2"] as String
            val playerDeckJson = matchResult["player1Deck"] as String
            val oppDeckJson = matchResult["player2Deck"] as String
            val points = matchResult["player1Points"] as Int
            val player1Result = matchResult["player1Result"] as String
            val player2Result = matchResult["player2Result"] as String
            val dateStr = matchResult["date"] as String

            // Parse the date string into Date object
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr) ?: Date()

            // Convert the JSON strings into Deck objects
            val playerDeck = gson.fromJson(playerDeckJson, Deck::class.java)
            val oppDeck = gson.fromJson(oppDeckJson, Deck::class.java)

            // Determine the result based on the match outcome
            val matchResult = player1Result

            // Create a new Match object
            Match(
                opponent = oppName,
                date = date,
                value = "$points",  // Assuming points is the value you want to store
                result = matchResult,
                deck = playerDeck,
                oppDeck = oppDeck
            )
        }
    }


}