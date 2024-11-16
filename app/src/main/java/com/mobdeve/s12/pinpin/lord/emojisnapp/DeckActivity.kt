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
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s12.pinpin.lord.emojisnapp.EmojiType
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityDeckBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMenuBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityTrackBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DialogEmojiDetailsBinding

class DeckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Deck Title
        binding.deckTitleTx.visibility = View.VISIBLE
        binding.deckTitleEt.visibility = View.GONE

        binding.deckTitleTx.setOnClickListener {
            binding.deckTitleTx.visibility = View.GONE
            binding.deckTitleEt.visibility = View.VISIBLE
            binding.deckTitleEt.setText(binding.deckTitleEt.text)
            binding.deckTitleEt.requestFocus()
        }

        // Makes B&W
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        binding.deckBackBtn.paint.colorFilter = colorFilter


        binding.deckTitleEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.deckTitleTx.text = binding.deckTitleEt.text
                binding.deckTitleTx.visibility = View.VISIBLE
                binding.deckTitleEt.visibility = View.GONE
            }
        }

        var deckData = DeckGenerator.loadDecks()
        deckData.forEach { it.sort() }

        var deckEmojiAdapter = DeckEmojiAdapter(deckData[0].getEmojis()) { emoji ->
            showEmojiDetailsPopup(emoji, EmojiType.DECK_EMOJI)
        }
        binding.recyclerDeckEmoji.layoutManager = GridLayoutManager(this, 6)
        binding.recyclerDeckEmoji.adapter = deckEmojiAdapter
        binding.recyclerDeckEmoji.isNestedScrollingEnabled = false
        binding.deckTitleTx.text = deckData[0].getTitle()
        deckEmojiAdapter.notifyItemRangeChanged(0, deckData[0].getEmojis().size)

        val deckBackAdapter = DeckBackAdapter(deckData) { selectedDeck ->
            updateEmojiRecycler(selectedDeck)
        }
        binding.recyclerDecks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerDecks.adapter = deckBackAdapter

        binding.recyclerEmojiList.layoutManager = GridLayoutManager(this, 4)


        // TODO: Replace getAllUniqueEmojis with currentUnlockedEmojis
        val allEmojis = DataGenerator.getAllUniqueEmojis()
        allEmojis.sortWith(compareBy({ it.cost }, { it.power }))
        binding.recyclerEmojiList.adapter = EmojiListAdapter(allEmojis) { emoji ->
            showEmojiDetailsPopup(emoji, EmojiType.LIST_EMOJI)
        }


        binding.createBtn.setOnClickListener {
            updateEmojiRecycler(Deck("Empty Deck", arrayListOf<Emoji>()))
        }

        val progress = intent.getIntExtra("CUR_LEVEL", 17);

        binding.deckBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun updateEmojiRecycler(deck: Deck) {
        var deckEmojiAdapter = DeckEmojiAdapter(deck.getEmojis()) { emoji ->
            showEmojiDetailsPopup(emoji, EmojiType.DECK_EMOJI)
        }
        binding.recyclerDeckEmoji.adapter = deckEmojiAdapter
        binding.deckTitleTx.text = deck.getTitle()
        deckEmojiAdapter.notifyItemRangeChanged(0, deck.getEmojis().size)
    }

    private fun showEmojiDetailsPopup(emoji: Emoji, type: EmojiType) {

        val binding = DialogEmojiDetailsBinding.inflate(layoutInflater)
        binding.detailEmojiIconTx.text = emoji.icon
        binding.detailEmojiCostTx.text = "${emoji.cost}"
        binding.detailEmojiPowerTx.text = "${emoji.power}"
        binding.detailEmojiNameTx.text = emoji.name
        binding.detailEmojiDescTx.setText(emoji.description)

        if(type === EmojiType.DECK_EMOJI) {
            binding.addBtn.visibility = View.GONE
        } else {
            binding.removeBtn.visibility = View.GONE
        }

        val dialog = AlertDialog.Builder(this, R.style.TransparentAlertDialog)
            .setView(binding.root)
            .create()

        dialog.show()
    }
}