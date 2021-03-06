package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.model.Shift
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DbShiftServices(private var dbInstance: FirebaseFirestore?) {

    fun getAllShifts(): MutableLiveData<ArrayList<Shift>> {
        var result = MutableLiveData<ArrayList<Shift>>()
        var shiftList: ArrayList<Shift> = ArrayList()
        if (dbInstance != null) {
            dbInstance!!.collection("shifts")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Mapping firestore object to kotlin model
                        var shift: Shift = Shift(
                            document.id,
                            document["type"] as String,
                            document["startHour"] as String,
                            document["endHour"] as String
                        )
                        // Insert to list
                        shiftList.add(shift)
                    }

                    // Call function to return shift list after mapping complete
                    result.value = shiftList
                }
                .addOnFailureListener { exception ->
                    Log.d("xk", "get failed with ", exception)
                }
        }
        return result
    }

    suspend fun getShiftType(id: String): String {
        var type: String = ""
        if (dbInstance != null) {
            val result = dbInstance!!.collection("shifts")
                .document(id)
                .get()
                .await()
            type = result.data?.get("type") as String
        }
        return type
    }
    suspend fun getShiftRef(shiftID: String?): DocumentReference? {
        var shiftRef: DocumentReference? = null

        if (dbInstance != null) {
            val docSnap = dbInstance!!.collection("shifts")
                .document(shiftID!!)
                .get()

            shiftRef = docSnap.await().reference
        }

        return shiftRef
    }
}