package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log

class GameTurn {
    // Holds the emojis placed during the turn and their locations
    val playerEmojisPlaced = mutableListOf<Pair<Emoji, Int>>()  // Pair of Emoji and its location index
    val opponentEmojisPlaced = mutableListOf<Pair<Emoji, Int>>()  // Same for opponent, if applicable

    // Method to record a player's emoji placement
    fun recordPlayerEmojiPlacement(emoji: Emoji, locationIndex: Int) {
        playerEmojisPlaced.add(Pair(emoji, locationIndex))
    }

    // Method to record an opponent's emoji placement (if relevant)
    fun recordOpponentEmojiPlacement(emoji: Emoji, locationIndex: Int) {
        opponentEmojisPlaced.add(Pair(emoji, locationIndex))
    }

    fun getPlayerThenOpp(): MutableList<Triple<Emoji, Int, Boolean>> {
        // Add 'true' for player moves and 'false' for opponent moves
        Log.d("GameTurn", "bot moves: $opponentEmojisPlaced")

        val playerMoves = playerEmojisPlaced.map { Triple(it.first, it.second, true) }
        val opponentMoves = opponentEmojisPlaced.map { Triple(it.first, it.second, false) }

        return (playerMoves + opponentMoves).toMutableList()
    }

    fun getOppThenPlayer(): MutableList<Triple<Emoji, Int, Boolean>> {
        // Add 'false' for opponent moves and 'true' for player moves
        Log.d("GameTurn", "bot moves: $opponentEmojisPlaced")
        val opponentMoves = opponentEmojisPlaced.map { Triple(it.first, it.second, false) }
        val playerMoves = playerEmojisPlaced.map { Triple(it.first, it.second, true) }

        return (opponentMoves + playerMoves).toMutableList()
    }

    // Method to clear the current turn data
    fun resetPlayerTurn() {
        playerEmojisPlaced.clear()
    }

    fun resetTurn() {
        playerEmojisPlaced.clear()
        opponentEmojisPlaced.clear()
    }
}