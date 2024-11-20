package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityGameBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DialogEmojiDetailsBinding

class GameActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityGameBinding
    private lateinit var gameManager: GameManager
    private lateinit var handAdapter: GameEmojiAdapter
    private lateinit var locationAdapter: LocationAdapter
    private var savedTurnState: GameState? = null
    private var currentEmojiBeingDragged: Emoji? = null // Track the emoji being dragged
    private val overlayViews = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Get saved deck and opp deck
        var playerDeck = Deck("Basic Deck", DataGenerator.loadActiveBasicData());
        var oppDeck = Deck("Alternative Deck", DataGenerator.loadActiveAlternativeData());
        /// TODO: Currently only against bot
        gameManager = GameManager(playerDeck, oppDeck, true) {
            // hack to make this render later
            runOnUiThread({
                revealMoves()
            })
        }

        binding.snapTx.setOnClickListener {
            if(gameManager.ante()){
                val scaleX = ObjectAnimator.ofFloat(binding.snapTx, "scaleX", 1f, 1.1f, 1f)
                val scaleY = ObjectAnimator.ofFloat(binding.snapTx, "scaleY", 1f, 1.1f, 1f)
                val rotate = ObjectAnimator.ofFloat(binding.snapTx, "rotation", 0f, 15f, -15f, 0f)

                val animatorSet = AnimatorSet()
                animatorSet.playTogether(scaleX, scaleY, rotate)
                animatorSet.duration = 250 // Snappy duration
                animatorSet.start()
            }
        }

        locationAdapter = LocationAdapter(gameManager.getLocations(), MutableList(5) { mutableListOf<Emoji>() }, MutableList(5) { mutableListOf<Emoji>() }, gameManager.getPlayerTotals(), gameManager.getOpponentTotals()) { emoji ->
            showEmojiDetailsPopup(emoji)
        }
        binding.locationRv.layoutManager = NoScrollLinearLayoutManager(this)
        binding.locationRv.isNestedScrollingEnabled = false
        binding.locationRv.setHasFixedSize(true)
        binding.locationRv.adapter = locationAdapter

        Log.d("Current emojis", gameManager.getHandEmojis().toString())

        handAdapter = GameEmojiAdapter(gameManager.getHandEmojis()) { emoji ->
            showEmojiDetailsPopup(emoji)
        }
        binding.handRv.layoutManager = NoScrollGridLayoutManager(this, 4)
        binding.handRv.isNestedScrollingEnabled = false
        binding.handRv.setHasFixedSize(true)
        binding.handRv.adapter = handAdapter

        binding.undoBtn.setOnClickListener {
            undoLastAction()
        }

        binding.endBtn.setOnClickListener {
            var oppRetreat = gameManager.endTurn()
            if(oppRetreat){
                fleedGame()
            } else {
                revealMoves()
            }
        }

        binding.escBtn.setOnClickListener {
            retreatGame()
        }

        startNewTurn()


    }

    private fun startNewTurn() {
        if(gameManager.currentTurn < 8) {
            gameManager.nextTurn {
                val emojisInHand = gameManager.getHandEmojis()
                val playerEmojisInLocations = gameManager.getPlayerEmojisInLocations()
                val oppEmojisInLocations = gameManager.getOppEmojisInLocations()

                Log.d("GameState", "Hand emojis: $emojisInHand")
                Log.d("GameState", "Player emojis: $playerEmojisInLocations")
                Log.d("GameState", "Opponent emojis: $oppEmojisInLocations")

                savedTurnState = GameState(
                    emojisInHand = emojisInHand.map { it.copy() }.toMutableList(),
                    oppEmojisInLocations = oppEmojisInLocations.map {
                        it.map { emoji -> emoji.copy() }.toMutableList()
                    }.toMutableList(),
                    playerEmojisInLocations = playerEmojisInLocations.map {
                        it.map { emoji -> emoji.copy() }.toMutableList()
                    }.toMutableList()
                )

                Log.d("GameState", "Saved State: $savedTurnState")

                binding.roundTx.text = "Round " + gameManager.currentTurn.toString()
                binding.energyTx.text = "Energy: " + gameManager.currentEnergy.toString()
                binding.anteTx.text = gameManager.ante.toString()

                handAdapter.notifyItemInserted(emojisInHand.size - 1)
                binding.handRv.post {
                    updateEmojiOverlays()
                }

                binding.locationRv.post {
                    locationAdapter.updateAllTotals(
                        gameManager.getPlayerTotals(),
                        gameManager.getOpponentTotals()
                    )
                }
            }
        } else {
            endGame()
        }
    }

    private fun endGame() {
        val result = gameManager.getFinalResult()

        // First, add a delay before showing the dialog
        val dialogDelayMillis = 2000L
        Handler(Looper.getMainLooper()).postDelayed({
            when (result) {
                "Win" -> {
                    showWinResultDialog(gameManager.ante * 2) // Show win dialog
                }
                "Loss" -> {
                    showLoseResultDialog(gameManager.ante * 2) // Show lose dialog
                }
                "Draw" -> {
                    showDrawResultDialog() // Show draw dialog
                }
            }

            // After showing the dialog, add a delay before exiting the activity
            val exitDelayMillis = 4000L
            Handler(Looper.getMainLooper()).postDelayed({
                finish() // Exit the activity
            }, exitDelayMillis)

        }, dialogDelayMillis)
    }

    private fun retreatGame() {

        gameManager.forfeitGame()

        val dialogDelayMillis = 2000L
        Handler(Looper.getMainLooper()).postDelayed({
            showEscapeResultDialog(gameManager.ante) // Show lose dialog


            // After showing the dialog, add a delay before exiting the activity
            val exitDelayMillis = 4000L
            Handler(Looper.getMainLooper()).postDelayed({
                finish() // Exit the activity
            }, exitDelayMillis)

        }, dialogDelayMillis)
    }

    private fun fleedGame() {

        gameManager.forfeitGame()

        val dialogDelayMillis = 2000L
        Handler(Looper.getMainLooper()).postDelayed({
            showFleeResultDialog(gameManager.ante) // Show lose dialog


            // After showing the dialog, add a delay before exiting the activity
            val exitDelayMillis = 4000L
            Handler(Looper.getMainLooper()).postDelayed({
                finish() // Exit the activity
            }, exitDelayMillis)

        }, dialogDelayMillis)
    }

    private fun revealMoves() {
        val moves = gameManager.getMovesInOrder() // Get all moves (player + opponent)
        Log.d("GameActivity", "Moves: $moves")
        gameManager.resetGameTurn()

        // Reset the emojis in the adapter to the saved state
        locationAdapter.setLocationsEmojis(
            savedTurnState!!.playerEmojisInLocations.toMutableList(),
            savedTurnState!!.oppEmojisInLocations.toMutableList()
        )

        // Disable UI actions during reveal
        binding.endBtn.isEnabled = false
        binding.undoBtn.isEnabled = false

        // Reveal moves with a delay
        var moveIndex = 0
        val handler = Handler(Looper.getMainLooper())
        val revealRunnable = object : Runnable {
            override fun run() {
                if (moveIndex < moves.size) {
                    val move = moves[moveIndex]

                    // Animate and update the emoji placement
                    Log.d("RevealMoves", "Revealing move: $move")
                    locationAdapter.addToLocation(move.first, move.second, move.third)
                    if(!move.third){
                        gameManager.getOppEmojisInLocations()[move.second].add(move.first)
                    }
                    // Proceed to the next move
                    moveIndex++
                    handler.postDelayed(this, 700) // Adjust delay as needed
                } else {
                    // All moves revealed, start the next turn
                    startNewTurn()
                    binding.endBtn.isEnabled = true
                    binding.undoBtn.isEnabled = true
                }
            }
        }

        // Start revealing moves
        handler.post(revealRunnable)
    }

    private fun updateAdapters() {
        handAdapter.setEmojis(gameManager.getHandEmojis())
        locationAdapter.setLocationsEmojis(gameManager.getPlayerEmojisInLocations(), gameManager.getOppEmojisInLocations())
    }

    fun undoLastAction() {
        if (savedTurnState != null) {

            runOnUiThread {
                handAdapter.setEmojis(savedTurnState!!.emojisInHand.toMutableList())
            }

            // Update the location adapter with the player and opponent emojis from the saved turn state
            locationAdapter.setLocationsEmojis(
                savedTurnState!!.playerEmojisInLocations.toMutableList(),
                savedTurnState!!.oppEmojisInLocations.toMutableList()
            )
            gameManager.undoToPreviousState(savedTurnState!!)
            binding.energyTx.text = "Energy: " + gameManager.currentEnergy.toString()
            updateEmojiOverlays()
        } else {
            Toast.makeText(this, "No actions to undo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEmojiOverlays() {
        val rootLayout = findViewById<FrameLayout>(android.R.id.content)
        binding.handRv.post {
            overlayViews.forEach { rootLayout.removeView(it) }
            overlayViews.clear()

            // Get the global position of the RecyclerView
            val recyclerViewRect = Rect()
            binding.handRv.getGlobalVisibleRect(recyclerViewRect)

            // Loop through all visible items in the RecyclerView
            for (i in 0 until binding.handRv.childCount) {
                val itemView = binding.handRv.getChildAt(i)
                val position = binding.handRv.getChildAdapterPosition(itemView)

                if (position == RecyclerView.NO_POSITION) continue

                // Get the emoji data for this position
                val emoji = gameManager.getHandEmojis()[position]

                // Get the global position of the itemView
                val itemRect = Rect()
                itemView.getGlobalVisibleRect(itemRect)

                // Adjust the position to make it relative to the root layout
                val offsetX = itemRect.left + 22
                val offsetY = itemRect.top - 58

                // Create an overlay FrameLayout
                val overlay = FrameLayout(this).apply {
                    layoutParams = FrameLayout.LayoutParams(itemView.width, itemView.height).apply {
                        leftMargin = offsetX
                        topMargin = offsetY
                    }
                    setBackgroundColor(Color.TRANSPARENT) // Transparent background
                }

                // Add the emoji as a TextView inside the FrameLayout
                val emojiView = TextView(this).apply {
                    text = emoji.icon
                    textSize = 25f
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(context, R.font.noto_color_emoji_compat)
                }
                overlay.addView(emojiView)

                // Add touch listener to enable dragging
                overlay.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // Optional: handle touch-down if needed
                        }
                        MotionEvent.ACTION_MOVE -> {
                            // Dynamically move the overlay as the user drags it
                            val params = overlay.layoutParams as FrameLayout.LayoutParams
                            params.leftMargin = (event.rawX - overlay.width / 2).toInt() + 15
                            params.topMargin = (event.rawY - overlay.height / 2).toInt() - 65
                            overlay.layoutParams = params
                        }
                        MotionEvent.ACTION_UP -> {
                            currentEmojiBeingDragged = emoji
                            val dropX = event.rawX.toInt()
                            val dropY = event.rawY.toInt()

                            // Check if the drop position is within the original item bounds
                            if (itemRect.contains(dropX, dropY)) {
                                // Show details popup if dropped on itself
                                showEmojiDetailsPopup(emoji)
                                updateEmojiOverlays()
                            } else {
                                // Handle normal drop behavior
                                handleDrop(event.rawX, event.rawY)
                            }
                            overlayViews.remove(overlay)
                            rootLayout.removeView(overlay) // Remove the overlay after drop
                        }
                    }
                    true
                }

                // Add the overlay to the root layout
                rootLayout.addView(overlay)
                overlayViews.add(overlay)
            }
        }
    }

    private fun handleDrop(x: Float, y: Float) {
        // Check which location the emoji was dropped on
        val locationIndex = getLocationIndexAtPosition(x, y)
        if (locationIndex != -1) {
            if (currentEmojiBeingDragged?.let { gameManager.checkIfValidMove(it, locationIndex) } == true) {
                // Add emoji to the corresponding location (this part is game-specific)
                gameManager.moveEmojiToLocation(currentEmojiBeingDragged!!, locationIndex, true)

                // Update the adapter to reflect the changes
                locationAdapter.updateLocationEmojis(locationIndex, gameManager.getPlayerEmojisInLocations()[locationIndex], gameManager.getOppEmojisInLocations()[locationIndex])
                handAdapter.setEmojis(gameManager.getHandEmojis())
                gameManager.recordPlayerTurn(currentEmojiBeingDragged!!.copy(), locationIndex)
                binding.energyTx.text = "Energy: " + gameManager.currentEnergy.toString()
                updateEmojiOverlays()
            } else {
                showInvalidMoveDialog(gameManager.getErrorMessage())
            }
        }
        currentEmojiBeingDragged = null
        updateEmojiOverlays()
    }

    // Check if the drop position corresponds to a location's view
    private fun getLocationIndexAtPosition(x: Float, y: Float): Int {
        // Iterate through the locations in the RecyclerView and check if the drop position
        // is within the bounds of one of the location views
        val layoutManager = binding.locationRv.layoutManager as NoScrollLinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()

        for (i in firstVisiblePosition until locationAdapter.itemCount) {
            val view = layoutManager.findViewByPosition(i) ?: continue
            if (isViewUnderDrop(view, x, y)) {
                return i // Location index where the emoji was dropped
            }
        }
        return -1 // No valid location found
    }

    // Check if the drop position is within the bounds of the given view
    private fun isViewUnderDrop(view: View, x: Float, y: Float): Boolean {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        return rect.contains(x.toInt(), y.toInt())
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

    private fun showInvalidMoveDialog(string : String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Invalid Move")
            .setMessage(string)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()
    }

    private fun showWinResultDialog(points : Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Congratulations.")
            .setMessage("You won $points points!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                                                         finish() }
            .create()

        dialog.show()
    }

    private fun showLoseResultDialog(points : Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Unfortunate.")
            .setMessage("You lost $points points!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                                                         finish() }
            .create()

        dialog.show()
    }

    private fun showEscapeResultDialog(points : Int) {
        var doublePoints = points * 2
        val dialog = AlertDialog.Builder(this)
            .setTitle("Forfeited.")
            .setMessage("You lost $points instead of $doublePoints points.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                finish() }
            .create()

        dialog.show()
    }

    private fun showFleeResultDialog(points : Int) {
        var doublePoints = points * 2
        val dialog = AlertDialog.Builder(this)
            .setTitle("Opponent flees!")
            .setMessage("You gain $points instead of $doublePoints points.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                finish() }
            .create()

        dialog.show()
    }

    private fun showDrawResultDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("A TIE!")
            .setMessage("No points gained or lost!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                                                         finish() }
            .create()

        dialog.show()
    }
}