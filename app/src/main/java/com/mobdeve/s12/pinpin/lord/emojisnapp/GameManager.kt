package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.UUID
import kotlin.random.Random


class GameManager (
    private val playerDeck: Deck,
    private val oppDeck: Deck,
    private val againstBot: Boolean,
    private val onOpponentTurnComplete : () -> Unit
)
{
    private val instance = FirebaseDatabase.getInstance("https://mco3-7e1e6-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val database: DatabaseReference = instance.getReference("TODO_CHANGE_THIS")
    private val emojisInHand = mutableListOf<Emoji>()
    private val locations : List<Location> = DataGenerator.loadLocations()
    private val playerEmojisInLocations = mutableListOf<MutableList<Emoji>>()
    private val oppEmojisInLocations = mutableListOf<MutableList<Emoji>>()
    private val botHand = mutableListOf<Emoji>()
    private var alreadyAnte = false;
    private var botAnte = false;
    var ante = 1
    var currentTurn = 0
    private var myGameTurnUuid = UUID.randomUUID().toString();
    private var gameTurn = GameTurn(myGameTurnUuid)
    var currentEnergy = 0
    private var error = ""

    init {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recvGameTurn = snapshot.getValue(GameTurn::class.java) ?: return
                if(recvGameTurn.uuid != myGameTurnUuid) { // change was from other client (not us)
                    gameTurn.opponentEmojisPlaced.clear()
                    gameTurn.opponentEmojisPlaced.addAll(recvGameTurn.opponentEmojisPlaced) // other client already puts it on opponentEmojisPlaced
                    onOpponentTurnComplete()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading currentTurn: ${error.message}")
            }
        })

        playerDeck.shuffle()
        repeat(5) {
            playerDeck.draw()?.let { emojisInHand.add(it) }
        }

        Log.d("EmojisinHand", emojisInHand.toString())

        if(againstBot){
            repeat(5) {
                oppDeck.draw()?.let { botHand.add(it) }
            }
        }



        playerEmojisInLocations.addAll(List(5) { mutableListOf<Emoji>() }) // Add five empty lists
        oppEmojisInLocations.addAll(List(5) { mutableListOf<Emoji>() })   // Add five empty lists
    }

    fun getHandEmojis(): MutableList<Emoji> = emojisInHand
    fun getLocations(): List<Location> = locations
    fun getPlayerEmojisInLocations(): MutableList<MutableList<Emoji>> = playerEmojisInLocations
    fun getOppEmojisInLocations(): MutableList<MutableList<Emoji>> = oppEmojisInLocations

    fun getPlayerTotals(): List<Int> {
        val playerTotals = mutableListOf<Int>()

        // Get the list of player emojis that were played in the current turn
        val emojisPlayedThisTurn = gameTurn.playerEmojisPlaced.map { it.first }

        // Iterate through each location's emojis
        for (locationEmojis in playerEmojisInLocations) {
            // Filter out the emojis that were played this turn
            val locationTotalPower = locationEmojis
                .filterNot { emojisPlayedThisTurn.contains(it) }  // Exclude emojis played in the current turn
                .sumOf { it.currentPower }

            playerTotals.add(locationTotalPower)
        }

        return playerTotals
    }

    fun getOpponentTotals(): List<Int> {
        val opponentTotals = mutableListOf<Int>()

        // Iterate through each location's emojis for the opponent
        for (locationEmojis in oppEmojisInLocations) {
            // Sum up the currentPower for the opponent's emojis in this location
            val locationTotalPower = locationEmojis.sumOf { it.currentPower }
            opponentTotals.add(locationTotalPower)
        }

        return opponentTotals
    }

    fun resetGameTurn(){
        gameTurn.resetTurn()
    }

    fun endTurn(): Boolean {
        if (againstBot) {
            // available immediately

        } else {
            val test = database.setValue(gameTurn.toOtherPlayersPOV())
            test.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d("GameManager", "Successful")
                } else {
                    Log.d("GameManager", task.exception.toString())
                }
            }
            Log.d("GameManager", "Writing")
            //TODO: Wait for opponent endTurn
            //TODO: Send Player moves to opponent
            //TODO: Recieve opp moves

            //TODO: Check if opponent retreated
            if (false){ // Replace with opponent flee check
                return true
            }

            //TODO: Send to opponent if you ante'd
            //TODO: Check if opponent ante'd
            //TODo: Add opp moves to GameTurn.
        }
        onOpponentTurnComplete()
        return false
    }

    fun forfeitGame(){
        if (!againstBot) {
            gameTurn.flee()
            //TODO: Send to other player that I retreated.
        }
    }

    fun ante(): Boolean {
        if(!alreadyAnte) {
            ante *= 2
            alreadyAnte = true
            return true
        } else {
            return false
        }
    }

    fun nextTurn(onComplete: () -> Unit) {
        currentTurn++
        currentEnergy = currentTurn
        gameTurn.resetTurn()
        playerDeck.draw()?.let { emojisInHand.add(it) }

        if (againstBot) {
            oppDeck.draw()?.let { botHand.add(it) }
            getBotMoves()
        }

        // Invoke the callback after the turn setup is complete
        onComplete()
    }

    fun getMovesInOrder(): MutableList<Triple<Emoji, Int, Boolean>> {
        if(getOverallWinner() == "Player"){
            return gameTurn.getPlayerThenOpp()
        } else {
            return gameTurn.getOppThenPlayer()
        }
    }

    fun getLocationWinner(locationIndex: Int): String {
        // Get total power for player and opponent at this location
        val playerLocationPower = playerEmojisInLocations[locationIndex].sumOf { it.currentPower }
        val opponentLocationPower = oppEmojisInLocations[locationIndex].sumOf { it.currentPower }

        return when {
            playerLocationPower > opponentLocationPower -> "Player"
            opponentLocationPower > playerLocationPower -> "Opponent"
            else -> if (Random.nextBoolean()) "Player" else "Opponent" // Randomly pick if it's a tie
        }
    }

    fun getOverallWinner(): String {
        var playerWins = 0
        var opponentWins = 0

        // Iterate through all locations and count wins
        for (i in playerEmojisInLocations.indices) {
            val winner = getLocationWinner(i)
            if (winner == "Player") {
                playerWins++
            } else if (winner == "Opponent") {
                opponentWins++
            }
        }

        // Determine overall winner based on number of locations won
        return when {
            playerWins > opponentWins -> "Player"
            opponentWins > playerWins -> "Opponent"
            else -> if (Random.nextBoolean()) "Player" else "Opponent" // Randomly pick if there's a tie
        }
    }

    fun getFinalResult(): String {
        var playerWins = 0
        var opponentWins = 0

        // Iterate through all locations and count wins
        for (i in playerEmojisInLocations.indices) {
            val winner = getLocationWinner(i)
            if (winner == "Player") {
                playerWins++
            } else if (winner == "Opponent") {
                opponentWins++
            }
        }

        // Determine overall winner based on number of locations won
        return when {
            playerWins > opponentWins -> "Win"
            opponentWins > playerWins -> "Loss"
            else -> "Draw"
        }
    }

    fun getBotMoves() {
        var botEnergy = currentTurn

        // Filter emojis that can be played within the current energy and shuffle them
        Log.d("GameManager", "bot hand: $botHand")
        val playableEmojis = botHand.filter { it.baseCost <= botEnergy }.shuffled()


        Log.d("GameManager", "playable bot emojis: $playableEmojis")

        // Iterate over the shuffled list and play emojis until energy runs out or all are considered
        for (emoji in playableEmojis) {
            // Check for a valid location to play the emoji
            val validLocationIndex = findValidLocationForEmoji(emoji)

            if (validLocationIndex != -1) {
                // If a valid location is found, play the emoji
                recordOpponentTurn(emoji, validLocationIndex)
                botHand.remove(emoji)
                Log.d("GameManager", "Bot plays: $emoji at $validLocationIndex")
                // Deduct the emoji's cost from the bot's energy
                botEnergy -= emoji.baseCost

                // If energy is depleted, stop the process
                if (botEnergy <= 0) break
            }

        }

        Log.d("GameManager", "Current Moves: " + gameTurn.getPlayerThenOpp().toString())

        if(getFinalResult() == "Lose" && !botAnte){
            ante *= 2
            botAnte = true
        }
    }

    fun recordPlayerTurn(emoji: Emoji, locationIndex: Int) {
        gameTurn.recordPlayerEmojiPlacement(emoji, locationIndex)
    }

    fun recordOpponentTurn(emoji: Emoji, locationIndex: Int) {
        gameTurn.recordOpponentEmojiPlacement(emoji, locationIndex)
        Log.d("GameManager", "gameTurn: $gameTurn")
    }

    fun checkIfValidMove(emoji: Emoji, locationIndex: Int): Boolean {
        if (emoji.baseCost > currentEnergy){
            error = "NOT_ENOUGH_ENERGY"
            return false
        }

        if (playerEmojisInLocations[locationIndex].size >= 4) {
            error = "NO_SPACE_IN_LOCATION"
            return false
        }

        // Check if the location is a Mountain and baseCost is less than or equal to 4
        if (locations[locationIndex].name == "Mountain" && emoji.baseCost <= 4) {
            error = "3_COST_BELOW_MOUNTAIN"
            return false
        }

        // If there's an Angel in either player's or opponent's locations, baseCost should be less than 4
        if ((playerEmojisInLocations.flatten().any { it.name == "Angel" } ||
                    oppEmojisInLocations.flatten().any { it.name == "Angel" }) &&
            emoji.baseCost >= 4) {
            error = "4_COST_ABOVE_ANGEL"
            return false
        }

        // If all conditions pass, return true
        return true
    }

    fun getErrorMessage(): String {
        return when (error) {
            "NOT_ENOUGH_ENERGY" -> "You don't have enough energy to play this emoji."
            "NO_SPACE_IN_LOCATION" -> "There's no space left in this location."
            "3_COST_BELOW_MOUNTAIN" -> "This emoji's cost is too low to play at the Mountain."
            "4_COST_ABOVE_ANGEL" -> "This emoji's cost is too high because an Angel is in play."
            else -> "An unknown error occurred."
        }
    }

    fun findValidLocationForEmoji(emoji: Emoji): Int {
        // List to store all valid location indices
        val validLocations = mutableListOf<Int>()

        // Loop through all locations to find valid ones
        for (i in oppEmojisInLocations.indices) {
            // Check if the location is not full (less than 4 emojis already)
            if (oppEmojisInLocations[i].size < 4) {

                // Check if location is a Mountain, and baseCost should be greater than 4
                if (locations[i].name == "Mountain" && emoji.baseCost <= 4) {
                    continue
                }

                // If there is an Angel present (either in player's or opponent's emojis),
                // baseCost should be less than 4
                if ((playerEmojisInLocations.flatten().any { it.name == "Angel" } ||
                    oppEmojisInLocations.flatten().any { it.name == "Angel" }) &&
                    emoji.baseCost >= 4) {
                    continue
                }

                // If passed all checks, this is a valid location
                validLocations.add(i)
            }
        }

        // If we have valid locations, pick one randomly
        return if (validLocations.isNotEmpty()) {
            validLocations.random()  // Randomly selects a valid location index
        } else {
            -1  // No valid location found
        }
    }

    fun moveEmojiToLocation(emoji: Emoji, locationIndex: Int, isPlayer: Boolean) {
        if (emojisInHand.remove(emoji)) {
            val targetLocations = if (isPlayer) playerEmojisInLocations else oppEmojisInLocations
            if (isPlayer){
                currentEnergy -= emoji.baseCost
            }
            targetLocations.getOrNull(locationIndex)?.add(emoji)
        }
    }

    fun undoToPreviousState(previousState: GameState) {
        // Clear and copy the state from previousState
        Log.d("GameManager", "$previousState")
        emojisInHand.clear()
        emojisInHand.addAll(previousState.emojisInHand.toMutableList())
        Log.d("GameManager", "$emojisInHand")

        playerEmojisInLocations.clear()
        playerEmojisInLocations.addAll(previousState.playerEmojisInLocations.map { it.toMutableList() })

        oppEmojisInLocations.clear()
        oppEmojisInLocations.addAll(previousState.oppEmojisInLocations.map { it.toMutableList() })

        currentEnergy = currentTurn
        gameTurn.resetPlayerTurn()
    }
}