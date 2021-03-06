package com.example.hair_booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hair_booking.firebase.Database
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.ui.manager.stylist.ManagerStylistDetailActivity
import com.example.hair_booking.ui.manager.stylist.ManagerStylistListActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    // Get firestore instance
    private val firebaseInstance: FirebaseFirestore = Database.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        val intent = Intent(this, ManagerStylistListActivity::class.java)
        startActivity(intent)
    }

}