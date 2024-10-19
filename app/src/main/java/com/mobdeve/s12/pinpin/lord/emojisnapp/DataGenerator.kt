package com.mobdeve.s12.pinpin.lord.emojisnapp

class DataGenerator {
    companion object {
        fun loadData(): ArrayList<Emoji> {
            val data = ArrayList<Emoji>()

            // Adding 18 Emoji objects
            data.add(Emoji("😀", "Smiley Face", 1, 2, "A smile is a curve that sets things straight."))
            data.add(Emoji("😂", "Laughing Tears", 2, 4, "Laughing out loud crying."))
            data.add(Emoji("😍", "Heart Eyes", 3, 5, "All hearts on you."))
            data.add(Emoji("😎", "Cool Sunglasses", 4, 6, "What's behind those shades?"))
            data.add(Emoji("😢", "Crying Face", 5, 9, "Devastated. I'm devastated."))
            data.add(Emoji("😜", "Winking Face", 6, 12, "Wink along."))
            data.add(Emoji("🎉", "Party Popper", 7, 15, "LET'S CELEBRATE!"))
            data.add(Emoji("🔥", "Fire", 3, 5, "On Play: Destroys one of your own Emojis already placed here."))
            data.add(Emoji("💯", "Hundred Points", 5, 10, "On Play: All odd-Power Emojis get -1 power, gain +1 for each Emoji affected."))
            data.add(Emoji("🚀", "Rocket", 2, 5, "Trigger: When an Emoji is played here, move to another location."))
            data.add(Emoji("🌟", "Glowing Star", 5, 3, "Ongoing: Give all your Emojis in this location +2 power."))
            data.add(Emoji("🍀", "Four Leaf Clover", 1, 1, "On Play: Shuffle your deck and draw a card."))
            data.add(Emoji("🧩", "Puzzle Piece", 3, 9, "Condition: Can only be played as the last emoji in a location."))
            data.add(Emoji("🎯", "Bullseye", 8, 25, "Condition: Can only be played if you didn't play a card the previous turn."))
            data.add(Emoji("✨", "Sparkles", 2, 3, "On Play: Generate two Sparklets at two other locations."))
            data.add(Emoji("🕊️", "Peace Dove", 3, 3, "Ongoing: Emojis with equal or less than 3-Cost can be played in this location."))
            data.add(Emoji("🌈", "Rainbow", 5, 2, "Ongoing: All your other Emojis gain +1 power."))
            data.add(Emoji("💪", "Strong Arm", 4, 4, "Trigger: If this card gains additional Power, double it."))

            return data
        }

        fun loadAlternativeData(): ArrayList<Emoji> {
            val data = ArrayList<Emoji>()

            // Replacing the first six with new Emoji objects
            data.add(Emoji("😇", "Angel Face", 3, 3, "Ongoing: Your Emojis cannot lose power and regains any lost power."))
            data.add(Emoji("🤖", "Robot", 4, 2, "On Play: Create a Robot with no abilities on each location."))
            data.add(Emoji("🦄", "Unicorn", 6, 5, "On Play: If this is played on a location without any other Emojis, gain +10 power"))
            data.add(Emoji("👽", "Alien", 1, 3, "On Play: All Emojis including itself get -1 power."))
            data.add(Emoji("🤡", "Clown", 3, -3, "On Play: Switch sides."))
            data.add(Emoji("🐉", "Dragon", 5, 9, "On Play: Opposing Emojis get -1 power."))
            data.add(Emoji("🎉", "Party Popper", 7, 15, "LET'S CELEBRATE!"))
            data.add(Emoji("🔥", "Fire", 3, 5, "On Play: Destroys one of your own Emojis already placed here."))
            data.add(Emoji("💯", "Hundred Points", 5, 10, "On Play: All odd-Power Emojis get -1 power, gain +1 for each Emoji affected."))
            data.add(Emoji("🚀", "Rocket", 2, 5, "Trigger: When an Emoji is played here, move to another location."))
            data.add(Emoji("🌟", "Glowing Star", 5, 3, "Ongoing: Give all your Emojis in this location +2 power."))
            data.add(Emoji("🍀", "Four Leaf Clover", 1, 1, "On Play: Shuffle your deck and draw a card."))
            data.add(Emoji("🧩", "Puzzle Piece", 3, 9, "Condition: Can only be played as the last emoji in a location."))
            data.add(Emoji("🎯", "Bullseye", 8, 25, "Condition: Can only be played if you didn't play a card the previous turn."))
            data.add(Emoji("✨", "Sparkles", 2, 3, "On Play: Generate two Sparklets at two other locations."))
            data.add(Emoji("🕊️", "Peace Dove", 3, 3, "Ongoing: Emojis with equal or less than 3-Cost can be played in this location."))
            data.add(Emoji("🌈", "Rainbow", 5, 2, "Ongoing: All your other Emojis gain +1 power."))
            data.add(Emoji("💪", "Strong Arm", 4, 4, "Trigger: If this card gains additional Power, double it."))

            return data
        }

        // Function to return all unique emojis from both data sets
        fun getAllUniqueEmojis(): ArrayList<Emoji> {
            val allEmojis = HashSet<Emoji>() // Use a Set to automatically filter duplicates
            allEmojis.addAll(loadData())
            allEmojis.addAll(loadAlternativeData())

            return ArrayList(allEmojis) // Convert back to ArrayList
        }
    }
}