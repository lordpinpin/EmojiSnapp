package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random


class GameManager (
    private val playerDeck: Deck,
    private val oppDeck: Deck,
    private var againstBot: Boolean,
    private val onOpponentTurnComplete : () -> Unit
)
{
    private var fleedGame: (() -> Unit)? = null
    private var revealMoves: (() -> Unit)? = null
    private val instance = FirebaseDatabase.getInstance("https://mco3-7e1e6-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val database: DatabaseReference = instance.getReference("TODO_CHANGE_THIS")
    private val tieBreakerRef = database.child("tieBreakerResult")

    // Player-related variables
    private val emojisInHand = mutableListOf<Emoji>()
    private val playerEmojisInLocations = mutableListOf<MutableList<Emoji>>()
    private var playerName = Firebase.auth.currentUser?.uid ?: "Unknown UID"

    // Opponent-related variables
    private val oppEmojisInLocations = mutableListOf<MutableList<Emoji>>()
    private var oppName = ""

    // Game state variables
    private val locations: List<Location> = DataGenerator.loadLocations(true)
    private var gameTurn = GameTurn()
    private var myGameTurnUuid = UUID.randomUUID().toString()
    var currentTurn = 0
    var ante = 1
    private var alreadyAnte = false
    private var botAnte = false

    // Energy-related variables
    var currentEnergy = 0
    var botEnergy = 0

    // Bot-related variables
    private val botHand = mutableListOf<Emoji>()
    private var playedLastTurn = false

    // Error handling
    private var error = ""

    init {
        if(!againstBot) run {
            Matchmaker.getMatch({ opponentUid, prevState ->
                oppName = opponentUid
                fromUs = prevState
                Log.d("Match", "Opponent found: $oppName")
                MessageListener.onMessage(oppName, { messageParsed, isFlee ->
                    if(isFlee) {
                        // opponent fleed
                        MessageListener.endGameOfOpp(oppName)
                        fleedGame?.let { it() }
                        return@onMessage;
                    }

                    if(messageParsed == null) {
                        return@onMessage;
                    }

                    if(fromOpp == null || fromOpp!!.version != messageParsed.version) {
                        fromOpp = messageParsed
                        gameTurn.opponentEmojisPlaced = messageParsed.emojisPlaced
                    }

                    Log.e("DBG", (fromOpp?.version.toString()))
                    Log.e("DBG2", isFlee.toString())

                    if(fromOpp?.version == fromUs?.version) {
                        revealMoves?.let { it() }
                    }
                })
            }, {
                Log.e("Match", "Failed to find opponent. Defaulting to bot match.")
                againstBot = true
            })
        }

        playerDeck.shuffle()
        repeat(5) {
            playerDeck.draw()?.let { emojisInHand.add(it) }
        }

        Log.d("EmojisinHand", emojisInHand.toString())

        if(againstBot){
            botHand.shuffle()
            repeat(5) {
                oppDeck.draw()?.let { botHand.add(it) }
            }

            oppName = "Bot"
        }

        getOverallWinner()



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
        for (locationIndex in playerEmojisInLocations.indices) {
            val locationEmojis = playerEmojisInLocations[locationIndex]


            val locationTotalPower = locationEmojis
                .filterNot { emojisPlayedThisTurn.contains(it) }
                .sumOf { it.currentPower }

            // Location: Space Station Effect
            val spaceStationBonus = if (locations[locationIndex].name == "Space Station") {
                val otherEmojis = playerEmojisInLocations
                    .flatMap { it } // Flatten the list to get all emojis
                    .filterNot { it in locationEmojis }  // Exclude emojis in the current location
                    .filterNot { emojisPlayedThisTurn.contains(it) }  // Exclude emojis played this turn
                otherEmojis.size // +1 power for each emoji in other locations
            } else {
                0
            }

            // Add the bonus to the total power for this location
            playerTotals.add(locationTotalPower + spaceStationBonus)
        }

        return playerTotals
    }

    fun getOpponentTotals(): List<Int> {
        val opponentTotals = mutableListOf<Int>()

        // Iterate through each location's emojis
        for (locationIndex in oppEmojisInLocations.indices) {
            val locationEmojis = oppEmojisInLocations[locationIndex]


            val locationTotalPower = locationEmojis.sumOf { it.currentPower }

            // Location: Space Station Effect
            val spaceStationBonus = if (locations[locationIndex].name == "Space Station") {
                val otherEmojis = oppEmojisInLocations
                    .flatMap { it } // Flatten the list to get all emojis
                    .filterNot { it in locationEmojis }  // Exclude emojis in the current location
                otherEmojis.size // +1 power for each emoji in other locations
            } else {
                0
            }

            // Add the bonus to the total power for this location
            opponentTotals.add(locationTotalPower + spaceStationBonus)
        }

        return opponentTotals
    }

    fun resetGameTurn(){
        gameTurn.resetTurn()
    }

    private var fromOpp: MessageParsed? = null
    private var fromUs: MessageParsed? = null
    fun endTurn(): Boolean {
        if (againstBot) {
            // available immediately
            revealMoves?.let { it() }
        } else {
            //Send Player moves to opponent
            if(fromUs == null) {
                fromUs = MessageParsed(gameTurn.playerEmojisPlaced, 0)
            } else {
                fromUs = MessageParsed(gameTurn.playerEmojisPlaced, fromUs!!.version + 1)
            }
            MessageListener.send(oppName, fromUs!!)
        }

        return false
    }

    fun forfeitGame(){
        if (!againstBot) {
            gameTurn.flee()
            //Send to other player that I retreated.
            MessageListener.forfeitGame(oppName)
        }
    }

    fun saveMatch(points: Int, player1: MatchResult, player2: MatchResult) {
        val database = instance.getReference("matches")
        val gson = Gson()

        // Convert Deck objects to JSON strings
        val playerDeckJson = gson.toJson(playerDeck)
        val oppDeckJson = gson.toJson(oppDeck)

        // Prepare the match data
        val matchResult = mapOf(
            "player1" to playerName,
            "player2" to oppName,
            "player1Deck" to playerDeckJson, // JSON string
            "player2Deck" to oppDeckJson,       // JSON string
            "player1Points" to points,
            "player2Points" to -points,
            "player1Result" to player1,
            "player2Result" to player2,
            "date" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )

        // Save to Firebase
        val matchKey = database.push().key
        if (matchKey != null) {
            database.child(matchKey).setValue(matchResult)
                .addOnSuccessListener {
                    Log.d("Firebase", "Match saved successfully!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to save match: ${e.message}")
                }
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



    fun nextTurn(onComplete: (draw : Boolean) -> Unit) {
        currentTurn++
        currentEnergy = currentTurn
        gameTurn.resetTurn()
        var successfulDraw = true
        if (emojisInHand.size < 8) {
            playerDeck.draw()?.let { emojisInHand.add(it) }
        }
        else {
            successfulDraw = false
        }

        if (againstBot) {
            if(botHand.size < 8) { oppDeck.draw()?.let { botHand.add(it) } }
            getBotMoves()
        } else {


            tieBreakerRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GameManager", "Tiebreaker flushed successfully")

                    // Proceed to determine the winner
                    getOverallWinner()
                } else {
                    Log.e("GameManager", "Failed to flush tiebreaker: ${task.exception?.message}")
                    getOverallWinner()
                }
            }
            getOverallWinner()
        }
        onComplete(successfulDraw)
    }

    fun getMovesInOrder(): MutableList<Triple<Emoji, Int, Boolean>> {
        playedLastTurn = gameTurn.playerEmojisPlaced.isNotEmpty()

        return when (getOverallWinner()) {
            "Player" -> gameTurn.getPlayerThenOpp()
            "Opponent" -> gameTurn.getOppThenPlayer()
            else -> gameTurn.getPlayerThenOpp()
        }
    }

    fun checkEffects() {
        // Iterate over all player emojis in each location
        playerEmojisInLocations.forEachIndexed { index, emojiList ->
            val location = locations[index]
            emojiList.forEach { emoji ->
                // Reset effects for the emoji
                emoji.activeEffects.clear()

                // Apply location-specific effects
                when (location.name) {
                    "Dumpster" -> emoji.activeEffects["Dumpster"] = true
                    "Castle" -> emoji.activeEffects["Castle"] = true
                    "Galaxy" -> emoji.activeEffects["Galaxy"] = true
                    "Arena" -> emoji.activeEffects["Castle"] = true
                }

                when (emoji.name) {
                    "Hundred Points" -> {
                        // Hundred Points: All odd-Power Emojis get -1 power, gain +1 for odd-Power Emoji.
                        var affected = 0

                        playerEmojisInLocations[index].forEach { e ->
                            if (e.basePower % 2 != 0) {
                                e.activeEffects["Hundred Points"] = true
                                affected++
                            }
                        }
                        oppEmojisInLocations[index].forEach { e ->
                            if (e.basePower % 2 != 0) {
                                e.activeEffects["Hundred Points"] = true
                                affected++
                            }
                        }
                        emoji.additive = affected
                    }
                    "Glowing Star" -> {
                        // Glowing Star: Give all your Emojis in this location +2 power.
                        emojiList.forEach { e ->
                            e.activeEffects["Glowing Star"] = true
                        }
                    }
                    "Rainbow" -> {
                        // Rainbow: All your other Emojis gain +1 power.
                        val allOtherEmojis = mutableListOf<Emoji>()

                        // Loop through all locations
                        locations.forEachIndexed { index, _ ->
                            val otherEmojis = playerEmojisInLocations[index]
                            allOtherEmojis.addAll(otherEmojis)
                        }

                        val allOtherExceptRainbow = allOtherEmojis.filter { it.name == "Rainbow" }

                        // Now, iterate over all the other emojis and apply the Rainbow effect
                        allOtherExceptRainbow.forEach { e ->
                            e.activeEffects["Rainbow"] = true
                        }
                    }
                    "Dragon" -> {
                        // Dragon: Opposing Emojis get -1 power.
                        val opposingList = oppEmojisInLocations[index]
                        opposingList.forEach { e ->
                            e.activeEffects["Dragon"] = true
                        }
                    }
                    "Angel Face" -> {
                        // Rainbow: All your other Emojis gain +1 power.
                        val allOtherEmojis = mutableListOf<Emoji>()

                        // Loop through all locations
                        locations.forEachIndexed { index, _ ->
                            val otherEmojis = playerEmojisInLocations[index]
                            allOtherEmojis.addAll(otherEmojis)
                        }

                        // Now, iterate over all the other emojis and apply the Rainbow effect
                        allOtherEmojis.forEach { e ->
                            e.activeEffects["Angel Face"] = true
                        }
                    }
                }
            }
        }

        // Iterate over all opponent emojis in each location
        oppEmojisInLocations.forEachIndexed { index, emojiList ->
            val location = locations[index]
            emojiList.forEach { emoji ->
                // Reset effects for the emoji
                emoji.activeEffects.clear()

                // Apply location-specific effects
                when (location.name) {
                    "Dumpster" -> emoji.activeEffects["Dumpster"] = true
                    "Castle" -> emoji.activeEffects["Castle"] = true
                    "Galaxy" -> emoji.activeEffects["Galaxy"] = true
                    "Arena" -> emoji.activeEffects["Castle"] = true
                }

                when (emoji.name) {
                    "Hundred Points" -> {
                        // Hundred Points: All odd-Power Emojis get -1 power, gain +1 for each Emoji affected.
                        var affected = 0

                        playerEmojisInLocations[index].forEach { e ->
                            if (e.basePower % 2 != 0) {
                                e.activeEffects["Hundred Points"] = true
                                affected++
                            }
                        }
                        oppEmojisInLocations[index].forEach { e ->
                            if (e.basePower % 2 != 0) {
                                e.activeEffects["Hundred Points"] = true
                                affected++
                            }
                        }
                        emoji.activeEffects["Affected: $affected"]
                    }
                    "Glowing Star" -> {
                        // Glowing Star: Give all your Emojis in this location +2 power.
                        emojiList.forEach { e ->
                            e.activeEffects["Glowing Star"] = true
                        }
                    }
                    "Rainbow" -> {
                        // Rainbow: All your other Emojis gain +1 power.
                        val allOtherEmojis = mutableListOf<Emoji>()

                        // Loop through all locations
                        locations.forEachIndexed { index, _ ->
                            val otherEmojis = oppEmojisInLocations[index]
                            allOtherEmojis.addAll(otherEmojis)
                        }

                        val allOtherExceptRainbow = allOtherEmojis.filter { it.name == "Rainbow" }

                        // Now, iterate over all the other emojis and apply the Rainbow effect
                        allOtherExceptRainbow.forEach { e ->
                            e.activeEffects["Rainbow"] = true
                        }
                    }
                    "Dragon" -> {
                        // Dragon: Opposing Emojis get -1 power.
                        val opposingList = playerEmojisInLocations[index]
                        opposingList.forEach { e ->
                            e.activeEffects["Dragon"] = true
                        }
                    }
                    "Angel Face" -> {
                        // Rainbow: All your other Emojis gain +1 power.
                        val allOtherEmojis = mutableListOf<Emoji>()

                        // Loop through all locations
                        locations.forEachIndexed { index, _ ->
                            val otherEmojis = oppEmojisInLocations[index]
                            allOtherEmojis.addAll(otherEmojis)
                        }

                        // Now, iterate over all the other emojis and apply the Rainbow effect
                        allOtherEmojis.forEach { e ->
                            e.activeEffects["Angel Face"] = true
                        }
                    }
                }
            }
        }
    }

    fun checkOnPlayEffects(emoji: Emoji, locationIndex: Int, isPlayer: Boolean) {
        when (emoji.name) {
            "Four Leaf Clover" -> {
                // Shuffle the deck and draw a card (assuming `shuffleDeck` and `drawCard` are functions that do this)
                if(isPlayer) {
                    playerDeck.shuffle()
                    playerDeck.draw()?.let { emojisInHand.add(it) }
                } else if(againstBot){
                    botHand.shuffle()
                    oppDeck.draw()?.let { botHand.add(it) }
                }
            }

            "Unicorn" -> {
                // If played on an empty location, gain +12 power
                val playerEmojis = playerEmojisInLocations[locationIndex]
                val oppEmojis = oppEmojisInLocations[locationIndex]

                // Check if the location is empty
                if (playerEmojis.isEmpty() && oppEmojis.isEmpty()) {
                    // Apply +12 power to the Unicorn emoji
                    emoji.addModifier(12)
                }
            }

            "Alien" -> {
                // Apply -1 power to all emojis, including itself
                // Loop through both player and opponent emojis in all locations
                val allEmojis = mutableListOf<Emoji>()

                // Add all player and opponent emojis from all locations
                playerEmojisInLocations.forEach { allEmojis.addAll(it) }
                oppEmojisInLocations.forEach { allEmojis.addAll(it) }

                var emojisPlayedThisTurn = gameTurn.playerEmojisPlaced.map { it.first }

                allEmojis.filterNot { emojisPlayedThisTurn.contains(it) }

                // Apply -1 power to each emoji
                allEmojis.forEach { e ->
                    e.addModifier(-1)
                }

                Log.d("Game", "Alien effect applied: -1 power to all emojis!")
            }
        }
    }

    fun checkClown(emoji: Emoji, locationIndex: Int, isPlayer: Boolean) {
        // Determine the opposing side and their emojis at the current location
        val opposingEmojis = if (isPlayer) oppEmojisInLocations[locationIndex] else playerEmojisInLocations[locationIndex]

        var allMoves = gameTurn.getPlayerThenOpp()
        if(isPlayer){
            allMoves = allMoves.filter{ move ->
                move.second == locationIndex && !move.third
            }.toMutableList()
        } else {
            allMoves = allMoves.filter{ move ->
                move.second == locationIndex && move.third
            }.toMutableList()
        }

        // Count the number of emojis in the target location considering moves not yet played
        val availableSpace = 4 - opposingEmojis.size - allMoves.size

        if (availableSpace > 0) {
            if (isPlayer) {
                playerEmojisInLocations[locationIndex].remove(emoji)
            } else {
                oppEmojisInLocations[locationIndex].remove(emoji)
            }

            // Add the Clown to the opposing side
            if (isPlayer) {
                oppEmojisInLocations[locationIndex].add(emoji)
            } else {
                playerEmojisInLocations[locationIndex].add(emoji)
            }
        } else {
            Log.d("GameManager", "Clown unable to switch!")
        }
    }

    fun checkSparks(emoji: Emoji, locationIndex: Int, isPlayer: Boolean) {

        var allMoves = gameTurn.getPlayerThenOpp()
        var allMovesAbove = mutableListOf<Triple<Emoji, Int, Boolean>>()
        var allMovesBelow = mutableListOf<Triple<Emoji, Int, Boolean>>()
        if (isPlayer) {
            allMovesAbove = allMoves.filter { move ->
                move.second == locationIndex - 1 && !move.third
            }.toMutableList()
            allMovesBelow = allMoves.filter { move ->
                move.second == locationIndex + 1 && !move.third
            }.toMutableList()
        } else {
            allMovesAbove = allMoves.filter { move ->
                move.second == locationIndex - 1 && move.third
            }.toMutableList()
            allMovesBelow = allMoves.filter { move ->
                move.second == locationIndex + 1 && move.third
            }.toMutableList()
        }

        // Create two new 2-cost, 1-power emojis for the adjacent locations
        val newEmoji1 = Emoji("❇️", "Sparkle", "Ooooh. Sparkly.", 2, 1, 0) // Example emoji, adjust with actual Emoji constructor
        val newEmoji2 = Emoji("❇️", "Sparkle", "Ooooh. Sparkly.", 2, 1, 0)

        if (locationIndex > 0) {
            if (isPlayer && playerEmojisInLocations[locationIndex - 1].size + allMovesAbove.size < 4) {
                playerEmojisInLocations[locationIndex - 1].add(newEmoji1)
            } else if (!isPlayer && oppEmojisInLocations[locationIndex - 1].size + allMovesAbove.size < 4){
                oppEmojisInLocations[locationIndex - 1].add(newEmoji1)
            }
        }
        if (locationIndex < locations.size - 1) {
            if (isPlayer && playerEmojisInLocations[locationIndex + 1].size + allMovesAbove.size < 4) {
                playerEmojisInLocations[locationIndex + 1].add(newEmoji2)
            } else if (!isPlayer && oppEmojisInLocations[locationIndex + 1].size + allMovesAbove.size < 4) {
                oppEmojisInLocations[locationIndex + 1].add(newEmoji2)
            }
        }
    }

    fun checkRobot(emoji: Emoji, locationIndex: Int, isPlayer: Boolean) {
        val currentEmojis = if (isPlayer) playerEmojisInLocations[locationIndex] else oppEmojisInLocations[locationIndex]
        var allMoves = gameTurn.getPlayerThenOpp()
        if(isPlayer){
            allMoves = allMoves.filter{ move ->
                move.second == locationIndex && !move.third
            }.toMutableList()
        } else {
            allMoves = allMoves.filter{ move ->
                move.second == locationIndex && move.third
            }.toMutableList()
        }

        // Count the number of emojis in the target location considering moves not yet played
        val availableSpace = 4 - currentEmojis.size - allMoves.size
        val robotCopy = emoji.copy()
        if (availableSpace > 0) {
            // Add a clone to the location
            if (isPlayer) {
                playerEmojisInLocations[locationIndex].add(robotCopy)
            } else {
                oppEmojisInLocations[locationIndex].add(robotCopy)
            }
        } else {
            Log.d("GameManager", "Robot unable to duplicate.")
        }
    }

    fun checkTornado() {
        // Find all Tornado locations
        val tornadoLocations = locations.withIndex()
            .filter { it.value.name == "Tornado" }
            .map { it.index }

        // Apply a -1 modifier to all emojis in Tornado locations
        for (locationIndex in tornadoLocations) {
            val emojisInTornadoLocation = mutableListOf<Emoji>()

            // Collect all player and opponent emojis in the location
            emojisInTornadoLocation.addAll(playerEmojisInLocations[locationIndex])
            emojisInTornadoLocation.addAll(oppEmojisInLocations[locationIndex])

            // Apply the Tornado effect
            emojisInTornadoLocation.forEach { emoji ->
                emoji.addModifier(-1) // Reduce power
            }
        }

    }

    fun checkDestruction(emoji: Emoji, locationIndex: Int, isPlayer: Boolean): List<Triple<Int, Int, Boolean>> {
        val destroyedEmojis = mutableListOf<Triple<Int, Int, Boolean>>()

        if (emoji.name == "Fire") {
            // Get the list of emojis for the player or the opponent based on `isPlayer`
            val emojiList = if (isPlayer) playerEmojisInLocations[locationIndex] else oppEmojisInLocations[locationIndex]

            // Check if the emoji list is not empty
            if (emojiList.isNotEmpty()) {
                // Get the first emoji in the list (you can customize this to select the emoji to destroy)
                val emojiToDestroy = emojiList.first()  // Change logic here if you want a specific emoji
                val emojiPosition = emojiList.indexOf(emojiToDestroy)

                // Add the destroyed emoji's position and ownership info to the list
                destroyedEmojis.add(Triple(locationIndex, emojiPosition, isPlayer))

                // Remove the destroyed emoji from the list
                emojiList.remove(emojiToDestroy)

            }
        }

        // Return the list of destroyed emoji positions with ownership info
        return destroyedEmojis
    }


    fun checkVolcanoEffect(): List<Triple<Int, Int, Boolean>> {
        // Filter the locations that contain "Volcano"
        val volcanoLocations = locations.filter { it.name.contains("Volcano") }

        // List to hold the positions of the destroyed emojis
        val destroyedPositions = mutableListOf<Triple<Int, Int, Boolean>>() // Triple of (locationIndex, emojiPosition, isPlayerEmoji)

        // Check if any volcano locations exist
        if (volcanoLocations.isNotEmpty()) {
            for (volcanoLocation in volcanoLocations) {
                // Get the index of the current volcano location
                val locationIndex = locations.indexOf(volcanoLocation)

                // Combine emojis at the current location (player and opponent)
                val playerEmojis = playerEmojisInLocations[locationIndex]
                val oppEmojis = oppEmojisInLocations[locationIndex]
                val allEmojis = playerEmojis + oppEmojis

                // Sort emojis at this location by power (ascending)
                val sortedEmojis = allEmojis.sortedBy { it.currentPower }

                // Take up to three of the weakest emojis
                val weakestEmojis = sortedEmojis.take(3)

                // Iterate over the weakest emojis and trigger their destruction
                for (emoji in weakestEmojis) {
                    // Determine where the emoji is (either player or opponent)
                    val isPlayerEmoji = playerEmojis.contains(emoji)
                    val targetList = if (isPlayerEmoji) playerEmojis else oppEmojis

                    // Get the emoji position in the respective list
                    val emojiPosition = targetList.indexOf(emoji)

                    // Add the destroyed emoji's location index, position, and ownership to the list
                    destroyedPositions.add(Triple(locationIndex, emojiPosition, isPlayerEmoji))

                    // Trigger the destruction (remove the emoji from the respective list)
                    targetList.remove(emoji)
                }
            }
        }

        // Return the list of destroyed emoji positions with ownership info
        return destroyedPositions
    }

    private fun findValidLocationForEmoji(emoji: Emoji): Int {
        // List to store all valid location indices
        val validLocations = mutableListOf<Int>()

        // Loop through all locations to find valid ones
        for (i in oppEmojisInLocations.indices) {
            // Check if the location is not full (less than 4 emojis already)
            if (oppEmojisInLocations[i].size < 4) {

                if(!checkIfValidMove(emoji, i, isBot = true)){
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

    fun updateAllEmojis() {
        // Update current power of all player emojis
        playerEmojisInLocations.flatten().forEach { it.updateCurrentPower() }
        // Update current power of all opponent emojis
        oppEmojisInLocations.flatten().forEach { it.updateCurrentPower() }


    }


    fun getLocationWinner(locationIndex: Int): String {
        // Get total power for player and opponent at this location
        val playerLocationPower = playerEmojisInLocations[locationIndex].sumOf { it.currentPower }
        val opponentLocationPower = oppEmojisInLocations[locationIndex].sumOf { it.currentPower }

        return when {
            playerLocationPower > opponentLocationPower -> "Player"
            opponentLocationPower > playerLocationPower -> "Opponent"
            else -> "Neither"
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
            else -> {
                if(againstBot){
                    if (Random.nextBoolean()) "Player" else "Opponent" // Randomly pick if there's a tie
                } else { // TODO: TEST THIS
                    var result = "Player" // Default value in case of error
                    tieBreakerRef.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val existingResult = mutableData.getValue(String::class.java)
                            if (existingResult == null) {
                                // Generate a new random result if none exists
                                val newResult = if (Random.nextBoolean()) "Player" else "Opponent"
                                mutableData.value = newResult
                                result = newResult
                            } else {
                                // Use the existing result
                                result = existingResult
                            }
                            return Transaction.success(mutableData)
                        }

                        override fun onComplete(
                            databaseError: DatabaseError?,
                            committed: Boolean,
                            dataSnapshot: DataSnapshot?
                        ) {
                            if (databaseError != null) {
                                Log.e(
                                    "GameManager",
                                    "Error resolving tie: ${databaseError.message}"
                                )
                            }
                        }
                    })
                    result // Return the determined result
                }
            }
        }
    }

    fun getFinalResult(): String {
        var playerWins = 0
        var opponentWins = 0

        // Iterate through all locations and count wins
        for (i in locations.indices) {
            val winner = getLocationWinner(i)
            if (winner == "Player") {
                playerWins++
            } else if (winner == "Opponent") {
                opponentWins++
            }
            Log.d("GameManager", "Location $i winner: $winner")
        }

        // Determine overall winner based on number of locations won
        return when {
            playerWins > opponentWins -> "Win"
            opponentWins > playerWins -> "Loss"
            else -> "Draw"
        }
    }

    fun getBotMoves() {
        botEnergy = currentTurn

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

    fun checkIfValidMove(emoji: Emoji, locationIndex: Int, isBot: Boolean): Boolean {
        // Helper function to set the error only if not a bot
        fun setError(message: String) {
            if (!isBot) {
                error = message
            }
        }

        if (isBot) {
            if (emoji.baseCost > botEnergy) {
                return false
            }
        } else {
            if (emoji.baseCost > currentEnergy) {
                setError("NOT_ENOUGH_ENERGY")
                return false
            }
        }

        if (playerEmojisInLocations[locationIndex].size >= 4) {
            setError("NO_SPACE_IN_LOCATION")
            return false
        }

        // Check if the location is a Mountain and baseCost is less than or equal to 4
        if (locations[locationIndex].name == "Mountain" && emoji.baseCost <= 4) {
            setError("3_COST_BELOW_MOUNTAIN")
            return false
        }

        // If there's a Peace Dove in either player's or opponent's locations, baseCost should be less than 4
        if ((playerEmojisInLocations.flatten().any { it.name == "Peace Dove" } ||
                    oppEmojisInLocations.flatten().any { it.name == "Peace Dove" }) &&
            emoji.baseCost >= 4) {
            setError("4_COST_ABOVE_PEACE")
            return false
        }

        // You cannot play Bullseye if you played Emojis last turn.
        if (emoji.name == "Bullseye" &&
            playedLastTurn) {
            setError("BULLSEYE_RESTRICTION")
            return false
        }

        if (!isBot &&
            emoji.name == "Puzzle Piece" &&
            playerEmojisInLocations[locationIndex].size != 3){
            setError("PUZZLE_RESTRICTION")
            return false
        }

        if (isBot &&
            emoji.name == "Puzzle Piece" &&
            oppEmojisInLocations[locationIndex].size != 3){
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
            "4_COST_ABOVE_PEACE" -> "This emoji's cost is too high because a Peace Dove is in this location."
            "BULLSEYE_RESTRICTION" -> "You cannot play Bullseye if you played any last turn."
            "PUZZLE_RESTRICTION" -> "You must play Puzzle Piece as the last Emoji in a location."
            else -> "An unknown error occurred."
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

    fun removeMove(move : Triple<Emoji, Int, Boolean>){
        if(move.third){
            gameTurn.removePlayerMove(move)
        } else {
            gameTurn.removeOppMove(move)
        }
    }

    fun setOnOppMessageListener(fleedGame: () -> Unit, revealMoves: () -> Unit) {
        this.fleedGame = fleedGame
        this.revealMoves = revealMoves
    }


}