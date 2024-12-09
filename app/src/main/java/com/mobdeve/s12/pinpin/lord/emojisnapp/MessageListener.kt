package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.mobdeve.s12.pinpin.lord.emojisnapp.Matchmaker.Companion

class MessageListener {
    companion object {
        private val currentUser = Firebase.auth.currentUser
        private val instance = FirebaseDatabase.getInstance("https://mco3-7e1e6-default-rtdb.asia-southeast1.firebasedatabase.app/")
        private val database: DatabaseReference = instance.getReference("matchmaking")
        fun onMessage(opp: String, callback: (message: MessageParsed, flee: Boolean) -> Unit) {
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val doc = dataSnapshot.getValue(MatchmakerDocument::class.java)

                    if(doc != null) {
                        val entry = doc.requests.find {
                            it.user == opp && it.opp == currentUser?.uid && it.isOver != true
                        }
                        try {
                            val messageParsed = Gson().fromJson(entry?.emojiMessage, MessageParsed::class.java)
                            Log.e("TEST", (messageParsed == null).toString());
                            if(messageParsed != null) {
                                var retreat = false
                                if(entry?.retreat != null) {
                                    retreat = entry.retreat
                                }
                                callback(messageParsed, retreat)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.addValueEventListener(listener)
        }
        fun send(opp: String, message: MessageParsed) {
            if(currentUser?.uid == null) {
                return
            }

            database.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val docFromDb = currentData.getValue(MatchmakerDocument::class.java)
                    val doc = docFromDb ?: return Transaction.success(currentData);

                    val myPlayerEntry = doc.requests.find {
                        it.user == currentUser.uid && it.isOver == false
                    }

                    if(myPlayerEntry != null) {
                        myPlayerEntry.emojiMessage = Gson().toJson(message)
                    }

                    currentData.value = doc
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    Log.d("send", error.toString())
                    Log.d("send", committed.toString())
                }
            })
        }
    }
}