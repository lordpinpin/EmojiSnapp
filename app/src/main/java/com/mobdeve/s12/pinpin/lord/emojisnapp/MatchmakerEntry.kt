package com.mobdeve.s12.pinpin.lord.emojisnapp;

import com.google.firebase.database.IgnoreExtraProperties

//data class Message(var emojisPlaced: String = "", var version: Int = 0);

data class MessageParsed(var emojisPlaced: MutableList<Pair<Emoji, Int>> = mutableListOf(), var version: Int = 0)
@IgnoreExtraProperties
data class MatchmakerEntry(val user: String? = null, var opp: String? = null, var emojiMessage: String = "", val retreat: Boolean? = null, var isOver: Boolean? = false)
