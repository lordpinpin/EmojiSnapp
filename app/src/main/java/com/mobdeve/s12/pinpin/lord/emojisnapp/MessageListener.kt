package com.mobdeve.s12.pinpin.lord.emojisnapp

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class MessageListener {
    companion object {
        private val currentUser = Firebase.auth.currentUser
        private val instance = FirebaseDatabase.getInstance("https://mco3-7e1e6-default-rtdb.asia-southeast1.firebasedatabase.app/")
        private val database: DatabaseReference = instance.getReference("matchmaking")
        fun onMessage(opp: String, callback: (message: MessageParsed) -> Unit) {
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val doc = dataSnapshot.getValue(MatchmakerDocument::class.java)

                    if(doc != null) {
                        val entry = doc.requests.find {
                            it.user == currentUser?.uid && it.opp == opp
                        }
                        if(entry != null) {
                            val messageParsed = Gson().fromJson(entry.emojiMessage.emojisPlaced, MessageParsed::class.java)
                            callback(messageParsed)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.addValueEventListener(listener)
        }
    }
}