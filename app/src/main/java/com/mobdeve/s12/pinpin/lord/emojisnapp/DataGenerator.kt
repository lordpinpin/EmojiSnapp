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
    }
}