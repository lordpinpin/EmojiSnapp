package com.mobdeve.s12.pinpin.lord.emojisnapp

class DeckGenerator {
    companion object {
        fun loadDecks(): ArrayList<Deck> {
            val data = ArrayList<Deck>()
            val empty_deck = ArrayList<Emoji>()
            data.add(Deck("Basic Deck", DataGenerator.loadBasicData()))
            data.add(Deck("Alt Deck", DataGenerator.loadAlternativeData()))
            data.add(Deck("Empty Deck", empty_deck))
            return data
        }
    }
}