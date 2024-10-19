package com.mobdeve.s12.pinpin.lord.emojisnapp

import java.util.Date

class MatchGenerator {
    fun generateMatches(): ArrayList<Match> {
        val matches = ArrayList<Match>()

        // Load decks using DeckGenerator
        val decks = DeckGenerator.loadDecks()

        // Assuming the first two decks are used for the player and opponent
        val playerDeck1 = decks[0] // "Basic Deck"
        val playerDeck2 = decks[1] // "Alt Deck"

        // Create matches using different combinations of player and opponent decks
        matches.add(
            Match(
                opponent = "PlayerA",
                date = Date(),
                value = "+8",
                result = MatchResult.WON,
                deck = playerDeck1, // Player's Deck
                oppDeck = playerDeck2 // Opponent's Deck
            )
        )

        matches.add(
            Match(
                opponent = "PlayerB",
                date = Date(),
                value = "-4",
                result = MatchResult.FORFEIT,
                deck = playerDeck2, // Player's Deck
                oppDeck = playerDeck1 // Opponent's Deck
            )
        )

        matches.add(
            Match(
                opponent = "PlayerC",
                date = Date(),
                value = "-1",
                result = MatchResult.FLEE,
                deck = playerDeck1, // Player's Deck
                oppDeck = playerDeck2 // Opponent's Deck
            )
        )

        return matches
    }
}