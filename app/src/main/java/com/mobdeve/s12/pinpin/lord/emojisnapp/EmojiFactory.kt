package com.mobdeve.s12.pinpin.lord.emojisnapp

object EmojiFactory {

    private val emojiCache: MutableMap<String, Emoji> = mutableMapOf()

    init {
        // Hard-coded unique emojis with the correct order: icon, name, description, baseCost, basePower, unlockThreshold
        val emojis = listOf(
            Emoji("😀", "Smiley Face",
                description = "A smile is a curve that sets things straight.",
                baseCost = 1,
                basePower = 2,
                unlockThreshold = 0
            ),
            Emoji("😂", "Laughing Tears",
                description = "Laughing out loud crying.",
                baseCost = 2,
                basePower = 4,
                unlockThreshold = 0
            ),
            Emoji("😍", "Heart Eyes",
                description = "All hearts on you.",
                baseCost = 3,
                basePower = 5,
                unlockThreshold = 0
            ),
            Emoji("😎", "Cool Sunglasses",
                description = "What's behind those shades?",
                baseCost = 4,
                basePower = 6,
                unlockThreshold = 0
            ),
            Emoji("😢", "Crying Face",
                description = "Devastated. I'm devastated.",
                baseCost = 5,
                basePower = 9,
                unlockThreshold = 0
            ),
            Emoji("😜", "Winking Face",
                description = "Wink along.",
                baseCost = 6,
                basePower = 12,
                unlockThreshold = 0
            ),
            Emoji("🎉", "Party Popper",
                description = "LET'S CELEBRATE!",
                baseCost = 7,
                basePower = 15,
                unlockThreshold = 0
            ),
            Emoji("🔥", "Fire",
                description = "On Play: Destroys one of your own Emojis already placed here.",
                baseCost = 3,
                basePower = 5,
                unlockThreshold = 0
            ),
            Emoji("💯", "Hundred Points",
                description = "On Play: All odd-Power Emojis get -1 power, gain +1 for each Emoji affected.",
                baseCost = 5,
                basePower = 10,
                unlockThreshold = 0
            ),
            Emoji("🚀", "Rocket",
                description = "Trigger: When an Emoji is played here, move to another location.",
                baseCost = 2,
                basePower = 5,
                unlockThreshold = 0
            ),
            Emoji("🌟", "Glowing Star",
                description = "Ongoing: Give all your Emojis in this location +2 power.",
                baseCost = 5,
                basePower = 3,
                unlockThreshold = 0
            ),
            Emoji("🍀", "Four Leaf Clover",
                description = "On Play: Shuffle your deck and draw a card.",
                baseCost = 1,
                basePower = 1,
                unlockThreshold = 0
            ),
            Emoji("🧩", "Puzzle Piece",
                description = "Condition: Can only be played as the last emoji in a location.",
                baseCost = 3,
                basePower = 9,
                unlockThreshold = 0
            ),
            Emoji("🎯", "Bullseye",
                description = "Condition: Can only be played if you didn't play a card the previous turn.",
                baseCost = 8,
                basePower = 25,
                unlockThreshold = 0
            ),
            Emoji("✨", "Sparkles",
                description = "On Play: Generate two Sparklets at two other locations.",
                baseCost = 2,
                basePower = 3,
                unlockThreshold = 0
            ),
            Emoji("🕊️", "Peace Dove",
                description = "Ongoing: Emojis with equal or less than 3-Cost can be played in this location.",
                baseCost = 3,
                basePower = 3,
                unlockThreshold = 0
            ),
            Emoji("🌈", "Rainbow",
                description = "Ongoing: All your other Emojis gain +1 power.",
                baseCost = 5,
                basePower = 2,
                unlockThreshold = 0
            ),
            Emoji("💪", "Strong Arm",
                description = "Trigger: If this card gains additional Power, double it.",
                baseCost = 4,
                basePower = 4,
                unlockThreshold = 0
            ),
            Emoji("😇", "Angel Face",
                description = "Ongoing: Your Emojis cannot lose power and regains any lost power.",
                baseCost = 3,
                basePower = 3,
                unlockThreshold = 5
            ),
            Emoji("🤖", "Robot",
                description = "On Play: Create a Robot with no abilities on each location.",
                baseCost = 4,
                basePower = 2,
                unlockThreshold = 10
            ),
            Emoji("🦄", "Unicorn",
                description = "On Play: If this is played on a location without any other Emojis, gain +10 power",
                baseCost = 6,
                basePower = 5,
                unlockThreshold = 15
            ),
            Emoji("👽", "Alien",
                description = "On Play: All Emojis including itself get -1 power.",
                baseCost = 1,
                basePower = 3,
                unlockThreshold = 20
            ),
            Emoji("🤡", "Clown",
                description = "On Play: Switch sides.",
                baseCost = 3,
                basePower = -3,
                unlockThreshold = 25
            ),
            Emoji("🐉", "Dragon",
                description = "On Play: Opposing Emojis get -1 power.",
                baseCost = 5,
                basePower = 9,
                unlockThreshold = 30
            )
        )

        // Populate the emojiCache with these hard-coded emojis
        for (emoji in emojis) {
            val key = "${emoji.name}-${emoji.icon}" // Unique key based on name and icon
            emojiCache[key] = emoji
        }
    }


    fun getEmoji(icon: String, name: String): Emoji {
        val key = "$name-$icon"  // Unique key based on name and icon

        return emojiCache[key]!!
    }

    // Method to create a modifiable copy of an Emoji
    fun getEmojiCopy(name: String): Emoji {
        val cachedEmoji = emojiCache[name]
            ?: throw IllegalArgumentException("Emoji with name $name not found in the cache")

        return cachedEmoji.copy()
    }

    fun getEmojisWithPositiveUnlockThreshold(): List<Emoji> {
        return emojiCache.values.filter { it.unlockThreshold > 0 }
            .sortedBy { it.unlockThreshold }
    }

    // Function to get all emojis, sorted by name (alphabetical order)
    fun getEmojisSortedByName(): List<Emoji> {
        return emojiCache.values.sortedBy { it.name }
    }
}