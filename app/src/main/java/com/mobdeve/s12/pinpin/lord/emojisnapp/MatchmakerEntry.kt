package com.mobdeve.s12.pinpin.lord.emojisnapp;

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MatchmakerEntry(val user: String? = null, var found: String? = null, val retreat: Boolean? = null)