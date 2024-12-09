package com.mobdeve.s12.pinpin.lord.emojisnapp

class Deck(private val title: String, private val emojis: ArrayList<Emoji>) {
    private val originalEmojis: List<Emoji> = emojis.toList()

    fun getEmojis(): ArrayList<Emoji> {
        return this.emojis;
    }

    fun getTitle(): String {
        return this.title;
    }

    fun copy(): Deck {
        val copiedEmojis = ArrayList(emojis.map { it.copy() }) // Deep copy of emojis
        return Deck(title, copiedEmojis)
    }

    // Shuffle the deck
    fun shuffle() {
        emojis.shuffle()
    }

    // Draw an emoji from the top of the deck
    fun draw(): Emoji? {
        return if (emojis.isNotEmpty()) {
            emojis.removeAt(0) // Remove and return the top emoji
        } else {
            null // Return null if the deck is empty
        }
    }

    // Reset the deck to its original state
    fun reset() {
        emojis.clear()
        emojis.addAll(originalEmojis)
    }

    // Check if the deck is empty
    fun isEmpty(): Boolean {
        return emojis.isEmpty()
    }

    fun sort() {
        emojis.sortWith(compareBy({ it.baseCost }, { it.basePower }))
    }

    // Get the remaining number of emojis in the deck
    fun size(): Int {
        return emojis.size
    }

    // Print all emojis in the deck (for debugging purposes)
    fun printDeck(): String {
        val builder = StringBuilder()

        for (i in emojis.indices) {
            if (i < 9) {
                builder.append("${emojis[i].icon} ")
            }
            if (i == 8) {
                builder.append("\n") // Move to the next line after the first row
            }
            if (i >= 9) {
                builder.append("${emojis[i].icon} ")
            }
        }

        // Convert to string and print
        val emojiDisplay = builder.toString()
        return emojiDisplay
    }

    fun showDeck() {
        println(printDeck())
    }


}