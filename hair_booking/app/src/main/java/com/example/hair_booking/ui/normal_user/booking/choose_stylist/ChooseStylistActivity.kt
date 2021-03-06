package com.example.hair_booking.ui.normal_user.booking.choose_stylist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.R
import com.example.hair_booking.databinding.ActivityChooseStylistBinding

class ChooseStylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseStylistBinding
    private lateinit var stylistListAdapter: StylistListAdapter

    private var chosenSalonId: MutableLiveData<String> = MutableLiveData()
    private var chosenShiftId: MutableLiveData<String> = MutableLiveData()
    private var chosenDate: MutableLiveData<String> = MutableLiveData()
    private var chosenTimeRange: MutableLiveData<Pair<Float, Float>> = MutableLiveData()
    // "by viewModels()" is the auto initialization of viewmodel made by the library
    private val viewModel: ChooseStylistViewModel by viewModels{ ChooseStylistViewModelFactory(chosenSalonId, chosenShiftId, chosenTimeRange, chosenDate) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get chosen salon id
        chosenSalonId.value = intent.getStringExtra("chosenSalonId")

        // Get chosen shift id
        chosenShiftId.value = intent.getStringExtra("chosenShiftId")

        // Get chosen date
        chosenDate.value = intent.getStringExtra("chosenDate")

        // Get chosen time range
        val chosenTime: Float = intent.getFloatExtra("chosenTime", 0.0F)
        val estimatedEndTime: Float = intent.getFloatExtra("estimatedEndTime", 0.0F)
        var tmp: Pair<Float, Float> = Pair(chosenTime, estimatedEndTime)
        chosenTimeRange.value = tmp

        // Setup binding with xml file
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_stylist)

        // Assign view model to binding
        binding.viewModel = viewModel

        // Tell binding to observe the life cycle of this activity
        binding.lifecycleOwner = this

        // Create adapter for stylist recyclerview
        stylistListAdapter = StylistListAdapter()
        binding.stylistRecyclerView.adapter = stylistListAdapter

        // Assign linear layout for stylist recyclerview
        binding.stylistRecyclerView.layoutManager = LinearLayoutManager(this)

        // Add item decoration
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.stylistRecyclerView.addItemDecoration(itemDecoration)

        // Enable back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set onclick event on item in stylist list
        setOnStylistItemClickedEvent()

    }

    private fun setOnStylistItemClickedEvent() {
        stylistListAdapter.onItemClick = {position: Int ->
            // Create an intent to send data back to previous activity
            val replyIntent = Intent()

            val stylistId: String = viewModel.stylistList.value?.get(position)?.id ?: ""
            val stylistName: String = viewModel.stylistList.value?.get(position)?.fullName ?: ""

            // send chosen service id and name back to previous activity
            replyIntent.putExtra("stylistId", stylistId)
            replyIntent.putExtra("stylistName", stylistName)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }
    // Back to main screen when click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}