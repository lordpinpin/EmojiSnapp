package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log

class GameTurn () {
    // uuid used to determine who changed it last
    var uuid: String = ""

    // need to separate constructor for Firebase to work
    constructor(uuid: String) : this() {
        this.uuid = uuid
    }

    // Holds the emojis placed during the turn and their locations
    val playerEmojisPlaced = mutableListOf<Pair<Emoji, Int>>()  // Pair of Emoji and its location index
    val opponentEmojisPlaced = mutableListOf<Pair<Emoji, Int>>()  // Same for opponent, if applicable
    var fleed = false

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

    fun removePlayerMove(move: Triple<Emoji, Int, Boolean>) {
        playerEmojisPlaced.removeAll { it.first == move.first && it.second == move.second }
        Log.d("GameTurn", "Player move removed: $move")
    }

    // Method to remove a specific opponent move (identified by the Triple)
    fun removeOppMove(move: Triple<Emoji, Int, Boolean>) {
        opponentEmojisPlaced.removeAll { it.first == move.first && it.second == move.second }
        Log.d("GameTurn", "Opponent move removed: $move")
    }

    // Method to clear the current turn data
    fun resetPlayerTurn() {
        playerEmojisPlaced.clear()
    }

    fun resetTurn() {
        playerEmojisPlaced.clear()
        opponentEmojisPlaced.clear()
    }

    fun flee(){
        fleed = true
    }

    fun toOtherPlayersPOV() : GameTurn {
        val ret = GameTurn(this.uuid)
        ret.opponentEmojisPlaced.addAll(this.playerEmojisPlaced)
        ret.playerEmojisPlaced.addAll(this.opponentEmojisPlaced)
        return ret
    }
}