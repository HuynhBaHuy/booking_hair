package com.example.hair_booking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hair_booking.firebase.Database
import com.example.hair_booking.model.Salon
import com.example.hair_booking.services.db.dbServices
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    // Get firestore instance
    private val firebaseInstance: FirebaseFirestore = Database.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbServices.getNormalUserServices()?.foo()

//        firebaseInstance.collection("stylists")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val test: DocumentReference = document.data["workPlace"] as DocumentReference
//                    test.get().addOnSuccessListener { documents ->
//                        var test2 = documents.data?.get("address") as HashMap<String, String>
//
//                        var test3 = test2["ward"]
//                    }
//                }
//            }

//        firebaseInstance.collection("hairSalons")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val test = document.data
//                    var salon: Salon = Salon(document.id,
//                        test["name"] as String,
//                        test["salonAvatar"] as String,
//                        test["description"] as String,
//                        test["rate"] as Long,
//                        test["openHour"] as String,
//                        test["closeHour"] as String,
//                        test["address"] as HashMap<String, String>,
//                        test["appointments"] as ArrayList<HashMap<String, *>>,
//                        test["stylists"] as ArrayList<HashMap<String, *>>
//                    )
//
//                    val test2 = salon
//
//                    val salonTest = Salon(document.id, test["name"] as String)
//                    val test3 = salon
//                }
//            }
    }

}