package com.mobdeve.s12.pinpin.lord.emojisnapp

data class GameState(
    val emojisInHand: MutableList<Emoji>,
    val playerEmojisInLocations: MutableList<MutableList<Emoji>>,
    val oppEmojisInLocations: MutableList<MutableList<Emoji>>
)