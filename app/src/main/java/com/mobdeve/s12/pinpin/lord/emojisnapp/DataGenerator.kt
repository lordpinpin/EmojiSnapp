package com.mobdeve.s12.pinpin.lord.emojisnapp

class DataGenerator {
    companion object {
        fun loadData(): ArrayList<Emoji> {
            val data = ArrayList<Emoji>()

            // Adding 18 Emoji objects
            data.add(Emoji(
                "😀", "Smiley Face",
                1, 2,
                "A smile is a curve that sets things straight."))

            data.add(Emoji(
                "😂", "Laughing Tears",
                2, 4,
                "Laughing out loud crying."))

            data.add(Emoji(
                "😍", "Heart Eyes",
                3, 5,
                "All hearts on you."))

            data.add(Emoji(
                "😎", "Cool Sunglasses",
                4, 6,
                "What's behind those shades?"))

            data.add(Emoji(
                "😢", "Crying Face",
                5, 9,
                "Devastated. I'm devastated."))

            data.add(Emoji(
                "😜", "Winking Face",
                6, 12,
                "Wink along."))

            data.add(Emoji(
                "🎉", "Party Popper",
                7, 15,
                "LET'S CELEBRATE!"))

            data.add(Emoji(
                "🔥", "Fire",
                3, 5,
                "On Play: Destroys one of your own Emojis already placed in this location."))

            data.add(Emoji(
                "💯", "Hundred Points",
                5, 10,
                "Ongoing: All Emojis in this location with odd-costed power lose -1 power, while even-costed powers lose -2 power."))

            data.add(Emoji(
                "🚀", "Rocket",
                2, 5,
                "Trigger: When an Emoji is played in this location, move to another location."))

            data.add(Emoji(
                "🌟", "Glowing Star",
                5, 3,
                "Ongoing: Give all your Emojis in this location +2 power."))

            data.add(Emoji(
                "🍀", "Four Leaf Clover",
                1, 1,
                "On Play: Shuffle your deck and draw a card."))

            data.add(Emoji(
                "🧩", "Puzzle Piece",
                3, 9,
                "Condition: Can only be played as the last emoji in a location."))

            data.add(Emoji(
                "🎯", "Bullseye",
                8, 25,
                "Condition: Can only be played if you didn't play a card the previous turn."))

            data.add(Emoji(
                "✨", "Sparkles",
                2, 3,
                "On Play: Generate two Sparklets at two other locations."))

            data.add(Emoji(
                "🕊️", "Peace Dove",
                3, 3,
                "Ongoing: Emojis with equal or less than 3-Cost can be played in this location."))

            data.add(Emoji(
                "🌈", "Rainbow",
                5, 2,
                "Ongoing: All your other Emojis gain +1 power."))

            data.add(Emoji(
                "💪", "Strong Arm",
                4, 4,
                "Trigger: If this card gains additional Power, double it."))

            return data
        }
    }
}