package dev.mrbe.mztips.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed class TipsResponse
data class OnSuccess(val querySnapshot: QuerySnapshot?) : TipsResponse()
data class OnError(val exception: FirebaseFirestoreException?): TipsResponse()


class OddsRepo {
    private val firestore = FirebaseFirestore.getInstance()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTips() = callbackFlow {
        val  collection = firestore.collection("odds")

        val snapshotListener = collection.addSnapshotListener{ value, error ->
            val response = if (error == null) {
                Log.d(
                    "Tag",
                    "Response is not error. No of documents received is -> ${value?.documents}"
                )
                OnSuccess(value)
            } else {
                Log.d("TAg", "Response is error -> $error")
                OnError(error)
            }

            trySend(response)
        }
        awaitClose { snapshotListener.remove() }
    }
}
