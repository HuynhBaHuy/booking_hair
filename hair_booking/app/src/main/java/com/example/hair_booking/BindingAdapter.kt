package com.example.hair_booking

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hair_booking.model.*
import com.example.hair_booking.ui.admin.managers_list.ManagersListAdapter
import com.example.hair_booking.ui.admin.users_list.UsersListAdapter
import com.example.hair_booking.ui.normal_user.booking.DiscountListAdapter
import com.example.hair_booking.ui.normal_user.booking.SalonListAdapter
import com.example.hair_booking.ui.normal_user.booking.ServiceListAdapter
import com.example.hair_booking.ui.normal_user.booking.StylistListAdapter
import com.example.hair_booking.ui.normal_user.home.SalonAdapter
@BindingAdapter("data")
fun bindBookingStylistListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Stylist>?) {
    val adapter = recyclerView.adapter as StylistListAdapter?
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("bookingSalonData")
fun bindBookingSalonListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Salon>?) {
    val adapter = recyclerView.adapter as SalonListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("data")
fun bindBookingServiceListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Service>?) {
    val adapter = recyclerView.adapter as ServiceListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}

@BindingAdapter("data")
fun bindBookingDiscountListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Discount>?) {
    val adapter = recyclerView.adapter as DiscountListAdapter
    if (data != null) {
        adapter?.setData(data)
    }
}


@BindingAdapter("data")
fun bindSalonListRecyclerView(recyclerView: RecyclerView, data: ArrayList<Salon>?) {
    val adapter = recyclerView.adapter as SalonAdapter
    if (data != null) {


        data.forEach { salon ->
            Log.d("huy-test-bind",salon.name.toString())
            Log.d("huy-test-bind",salon.avatar.toString())
            Log.d("huy-test-bind",salon.rate.toString())
        }

        adapter.setData(data)
    }
    else
    {
        Log.d("huy-test-bind","data in binding null")
    }
}

@BindingAdapter("usersListData")
fun bindUserListRecyclerView(recyclerView: RecyclerView, dataUser: ArrayList<NormalUser>?) {
    val adapter = recyclerView.adapter as UsersListAdapter
    if (dataUser != null) {
        adapter?.setData(dataUser)
        }
}

@BindingAdapter("managersListData")
fun bindManagerListRecyclerView(recyclerView: RecyclerView, dataAccount: ArrayList<Account>?) {
    val adapter = recyclerView.adapter as ManagersListAdapter
    if (dataAccount != null) {
        adapter?.setData(dataAccount)
    }

}


@BindingAdapter("list", "selected")
fun setSelectedItem(spinner: Spinner, list: ArrayList<Salon>?, selected: String?) {
    if (list != null && selected != null) {
        for (i in list?.indices!!) {
            if (list[i].id == selected) {
                spinner.setSelection(i)
            }
        }
    }
    else if (list != null && selected == null){
        spinner.setSelection(0)
    }
    else {
        Log.i("AdapterBindingError", "Set selected fail")
    }
}
