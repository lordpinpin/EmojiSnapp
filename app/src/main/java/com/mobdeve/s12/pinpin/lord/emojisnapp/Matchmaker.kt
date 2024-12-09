package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.getValue
import com.google.gson.Gson

@IgnoreExtraProperties
class MatchmakerDocument(val requests: ArrayList<MatchmakerEntry> = ArrayList<MatchmakerEntry>()) {
}
class Matchmaker {
    companion object {
        private val currentUser = Firebase.auth.currentUser
        private val instance = FirebaseDatabase.getInstance("https://mco3-7e1e6-default-rtdb.asia-southeast1.firebasedatabase.app/")
        private val database: DatabaseReference = instance.getReference("matchmaking")

        fun getMatch(onMatchFound: (String, MessageParsed?) -> Unit, onError: () -> Unit) {
            if(currentUser?.uid == null) {
                onError()
                return
            }

            // add matchmaking entry
            database.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val docFromDb = currentData.getValue(MatchmakerDocument::class.java)
                    val doc = docFromDb ?: MatchmakerDocument()

                    val myPlayerEntry = doc.requests.find {
                        it.user == currentUser.uid && it.isOver == false
                    }

                    if(myPlayerEntry == null) {
                        doc.requests.add(MatchmakerEntry(currentUser.uid, null, "", false))
                    }

                    currentData.value = doc
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    Log.d("Matchmaker", error.toString())
                    Log.d("Matchmaker", committed.toString())
                }
            })

            // match finder
            database.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val docFromDb = currentData.getValue(MatchmakerDocument::class.java)
                    val doc = docFromDb ?: MatchmakerDocument()

                    val myPlayerEntry = doc.requests.find {
                        it.user == currentUser.uid
                    }
                    if(myPlayerEntry?.opp != null) {
                        // someone already matched with us
                        return Transaction.success(currentData)
                    }

                    val matchedPlayerEntry = doc.requests.find {
                        it.user != currentUser.uid && it.opp == null;
                    }

                    if(myPlayerEntry != null && matchedPlayerEntry != null) {
                        val matchedPlayerUser = matchedPlayerEntry?.user
                        if(matchedPlayerUser != null) {
                            myPlayerEntry.let {
                                it.opp = matchedPlayerUser
                            }
                            matchedPlayerEntry.let {
                                it.opp= currentUser.uid
                            }
                        }

                        currentData.value = doc
                        return Transaction.success(currentData)
                    } else {
                        return Transaction.abort()
                    }
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (committed) {
                        val doc = currentData?.getValue(MatchmakerDocument::class.java)
                        val myPlayerEntry = doc?.requests?.find {
                            it.user == currentUser.uid
                        }

                        if(myPlayerEntry?.opp != null) {
                            var parsed : MessageParsed? = null
                            try {
                                parsed = Gson().fromJson(myPlayerEntry?.emojiMessage, MessageParsed::class.java)
                            } catch (e: Exception) {

                            }
                            onMatchFound(myPlayerEntry.opp!!, parsed)
                            return
                        }

                    } else {
                        Log.e("Matchmaker", "Transaction failed: ${databaseError?.message}")
                    }

                    onError()
                }
            })
        }

        fun removeMatchmakingEntry() {
            if (currentUser?.uid == null) return

            database.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val docFromDb = currentData.getValue(MatchmakerDocument::class.java)
                    val doc = docFromDb ?: MatchmakerDocument()

                    // Remove the current user's entry
                    val iterator = doc.requests.iterator()
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        if (entry.user == currentUser.uid) {
                            iterator.remove()
                            break
                        }
                    }

                    currentData.value = doc
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (committed) {
                        Log.d("Matchmaker", "Matchmaking entry removed successfully")
                    } else {
                        Log.e("Matchmaker", "Failed to remove entry: ${databaseError?.message}")
                    }
                }
            })
        }
    }
}