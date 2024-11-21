package com.mobdeve.s12.pinpin.lord.emojisnapp

class DataGenerator {
    companion object {
        fun loadBasicData(): ArrayList<Emoji> {
            val data = ArrayList<Emoji>()

            // Adding emojis using only icon and name, relying on EmojiFactory
            data.add(EmojiFactory.getEmoji("😀", "Smiley Face"))
            data.add(EmojiFactory.getEmoji("😂", "Laughing Tears"))
            data.add(EmojiFactory.getEmoji("😍", "Heart Eyes"))
            data.add(EmojiFactory.getEmoji("😎", "Cool Sunglasses"))
            data.add(EmojiFactory.getEmoji("😢", "Crying Face"))
            data.add(EmojiFactory.getEmoji("😜", "Winking Face"))
            data.add(EmojiFactory.getEmoji("🎉", "Party Popper"))
            data.add(EmojiFactory.getEmoji("🔥", "Fire"))
            data.add(EmojiFactory.getEmoji("💯", "Hundred Points"))
            data.add(EmojiFactory.getEmoji("🚀", "Rocket"))
            data.add(EmojiFactory.getEmoji("🌟", "Glowing Star"))
            data.add(EmojiFactory.getEmoji("🍀", "Four Leaf Clover"))
            data.add(EmojiFactory.getEmoji("🧩", "Puzzle Piece"))
            data.add(EmojiFactory.getEmoji("🎯", "Bullseye"))
            data.add(EmojiFactory.getEmoji("✨", "Sparkles"))
            data.add(EmojiFactory.getEmoji("🕊️", "Peace Dove"))
            data.add(EmojiFactory.getEmoji("🌈", "Rainbow"))
            data.add(EmojiFactory.getEmoji("💪", "Strong Arm"))

            return data
        }

        fun loadAlternativeData(): ArrayList<Emoji> {
            val data = ArrayList<Emoji>()

            // Replacing the first six with new Emoji objects using only icon and name
            data.add(EmojiFactory.getEmoji("😇", "Angel Face"))
            data.add(EmojiFactory.getEmoji("🤖", "Robot"))
            data.add(EmojiFactory.getEmoji("🦄", "Unicorn"))
            data.add(EmojiFactory.getEmoji("👽", "Alien"))
            data.add(EmojiFactory.getEmoji("🤡", "Clown"))
            data.add(EmojiFactory.getEmoji("🐉", "Dragon"))
            data.add(EmojiFactory.getEmoji("🎉", "Party Popper"))
            data.add(EmojiFactory.getEmoji("🔥", "Fire"))
            data.add(EmojiFactory.getEmoji("💯", "Hundred Points"))
            data.add(EmojiFactory.getEmoji("🚀", "Rocket"))
            data.add(EmojiFactory.getEmoji("🌟", "Glowing Star"))
            data.add(EmojiFactory.getEmoji("🍀", "Four Leaf Clover"))
            data.add(EmojiFactory.getEmoji("🧩", "Puzzle Piece"))
            data.add(EmojiFactory.getEmoji("🎯", "Bullseye"))
            data.add(EmojiFactory.getEmoji("✨", "Sparkles"))
            data.add(EmojiFactory.getEmoji("🕊️", "Peace Dove"))
            data.add(EmojiFactory.getEmoji("🌈", "Rainbow"))
            data.add(EmojiFactory.getEmoji("💪", "Strong Arm"))

            return data
        }

        fun loadActiveBasicData(): ArrayList<Emoji> {
            val data = ArrayList<Emoji>()

            // Adding emojis using only name and using the updated getEmojiCopy
            data.add(EmojiFactory.getEmojiCopy("Smiley Face"))
            data.add(EmojiFactory.getEmojiCopy("Laughing Tears"))
            data.add(EmojiFactory.getEmojiCopy("Heart Eyes"))
            data.add(EmojiFactory.getEmojiCopy("Cool Sunglasses"))
            data.add(EmojiFactory.getEmojiCopy("Crying Face"))
            data.add(EmojiFactory.getEmojiCopy("Winking Face"))
            data.add(EmojiFactory.getEmojiCopy("Party Popper"))
            data.add(EmojiFactory.getEmojiCopy("Fire"))
            data.add(EmojiFactory.getEmojiCopy("Hundred Points"))
            data.add(EmojiFactory.getEmojiCopy("Rocket"))
            data.add(EmojiFactory.getEmojiCopy("Glowing Star"))
            data.add(EmojiFactory.getEmojiCopy("Four Leaf Clover"))
            data.add(EmojiFactory.getEmojiCopy("Puzzle Piece"))
            data.add(EmojiFactory.getEmojiCopy("Bullseye"))
            data.add(EmojiFactory.getEmojiCopy("Sparkles"))
            data.add(EmojiFactory.getEmojiCopy("Peace Dove"))
            data.add(EmojiFactory.getEmojiCopy("Rainbow"))
            data.add(EmojiFactory.getEmojiCopy("Strong Arm"))

            return data
        }

        fun loadActiveAlternativeData(): ArrayList<Emoji> {
            val data = ArrayList<Emoji>()

            // Replacing the first six with new Emoji objects using only name and using the updated getEmojiCopy
            data.add(EmojiFactory.getEmojiCopy("Angel Face"))
            data.add(EmojiFactory.getEmojiCopy("Robot"))
            data.add(EmojiFactory.getEmojiCopy("Unicorn"))
            data.add(EmojiFactory.getEmojiCopy("Alien"))
            data.add(EmojiFactory.getEmojiCopy("Clown"))
            data.add(EmojiFactory.getEmojiCopy("Dragon"))
            data.add(EmojiFactory.getEmojiCopy("Party Popper"))
            data.add(EmojiFactory.getEmojiCopy("Fire"))
            data.add(EmojiFactory.getEmojiCopy("Hundred Points"))
            data.add(EmojiFactory.getEmojiCopy("Rocket"))
            data.add(EmojiFactory.getEmojiCopy("Glowing Star"))
            data.add(EmojiFactory.getEmojiCopy("Four Leaf Clover"))
            data.add(EmojiFactory.getEmojiCopy("Puzzle Piece"))
            data.add(EmojiFactory.getEmojiCopy("Bullseye"))
            data.add(EmojiFactory.getEmojiCopy("Sparkles"))
            data.add(EmojiFactory.getEmojiCopy("Peace Dove"))
            data.add(EmojiFactory.getEmojiCopy("Rainbow"))
            data.add(EmojiFactory.getEmojiCopy("Strong Arm"))

            return data
        }

        fun loadLocations(): ArrayList<Location> {
            val allLocations = arrayListOf(
                Location(
                    icon = "🚯",
                    name = "Dumpster",
                    description = "Emojis here have -3 power."
                ),
                Location(
                    icon = "🏔️",
                    name = "Mountain",
                    description = "Emojis with less than 4 power cannot be played here."
                ),
                Location(
                    icon = "📄",
                    name = "Blank Page",
                    description = "No effect."
                ),
                Location(
                    icon = "🏰",
                    name = "Castle",
                    description = "Ongoing: All Emojis here gain +2 power."
                ),
                Location(
                    icon = "🚀",
                    name = "Space Station",
                    description = "+1 power for each Emoji in other locations."
                ),
                Location(
                    icon = "🌋",
                    name = "Volcano",
                    description = "Destroy the three weakest Emojis at the end of the game."
                ),
                Location(
                    icon = "🏟️",
                    name = "Arena",
                    description = "Ongoing: Double the power of all Emojis here."
                ),
                Location(
                    icon = "🌉",
                    name = "Bridge",
                    description = "On play: Move one Emoji from here to another location."
                ),
                Location(
                    icon = "🌌",
                    name = "Galaxy",
                    description = "Ongoing: Emojis here cannot be affected by abilities."
                ),
                Location(
                    icon = "💡",
                    name = "Idea Lab",
                    description = "On play: Draw a card from your deck."
                )
            )

            // Shuffle and pick 5 random locations
            allLocations.shuffle()
            return ArrayList(allLocations.take(5))
        }

        fun loadFiveEmojis() : List<Emoji> {
            return EmojiFactory.getEmojisSortedByName().shuffled().take(5)
        }

        fun loadFourEmojis() : List<Emoji> {
            return EmojiFactory.getEmojisSortedByName().shuffled().take(4)
        }

        fun loadThreeEmojis() : List<Emoji> {
            return EmojiFactory.getEmojisSortedByName().shuffled().take(3)
        }
    }
}