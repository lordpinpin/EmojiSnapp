package com.mobdeve.s12.pinpin.lord.emojisnapp


// TODO: Make Emojis into EmojiFactory to avoid creating too many instances of Emojis
// TODO: Keep the current variable immutable, while game-related variables should be mutable between copies
data class Emoji(val icon: String, val name: String, val cost: Int, val power: Int, val description: String)
