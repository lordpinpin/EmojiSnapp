package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityGameBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DialogEmojiDetailsBinding

class GameActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityGameBinding
    private lateinit var registerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var locationAdapter = LocationAdapter(DataGenerator.loadLocations()) { emoji ->
            showEmojiDetailsPopup(emoji)
        }
        binding.locationRv.layoutManager = NoScrollLinearLayoutManager(this)
        binding.locationRv.isNestedScrollingEnabled = false
        binding.locationRv.setHasFixedSize(true)
        binding.locationRv.adapter = locationAdapter


        var handAdapter = GameEmojiAdapter(DataGenerator.loadFiveEmojis()) { emoji ->
            showEmojiDetailsPopup(emoji)
        }
        binding.handRv.layoutManager = NoScrollGridLayoutManager(this, 4)
        binding.handRv.isNestedScrollingEnabled = false
        binding.handRv.setHasFixedSize(true)
        binding.handRv.adapter = handAdapter

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
}