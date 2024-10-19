package com.mobdeve.s12.pinpin.lord.emojisnapp

import java.util.Date

data class Match (val opponent : String, val date : Date, val value : String, val result : MatchResult, val deck : Deck, val oppDeck: Deck){

}
