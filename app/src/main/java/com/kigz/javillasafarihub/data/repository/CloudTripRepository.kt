package com.kigz.javillasafarihub.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kigz.javillasafarihub.data.model.SavedTrip
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CloudTripRepository {
    private val auth by lazy { try { FirebaseAuth.getInstance() } catch (e: Exception) { null } }
    private val database by lazy { try { FirebaseDatabase.getInstance() } catch (e: Exception) { null } }

    private fun userTripsRef(): DatabaseReference? =
        auth?.currentUser?.uid?.let { uid -> database?.reference?.child("users")?.child(uid)?.child("trips") }

    fun getTrips(): Flow<List<SavedTrip>> = callbackFlow {
        val ref = userTripsRef()
        if (ref == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val trips = snapshot.children.mapNotNull { child ->
                    child.getValue(SavedTrip::class.java)
                }
                trySend(trips.sortedByDescending { it.id })
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        try {
            ref.addValueEventListener(listener)
        } catch (_: Exception) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        awaitClose { try { ref.removeEventListener(listener) } catch (_: Exception) {} }
    }

    suspend fun saveTrip(trip: SavedTrip) {
        val ref = userTripsRef() ?: return
        val cloudId = ref.push().key ?: return
        ref.child(cloudId).setValue(trip).await()
    }

    suspend fun deleteTrip(trip: SavedTrip) {
        val ref = userTripsRef() ?: return
        val snapshot = ref.get().await()
        snapshot.children.filter { it.getValue(SavedTrip::class.java) == trip }
            .forEach { it.ref.removeValue().await() }
    }
}
