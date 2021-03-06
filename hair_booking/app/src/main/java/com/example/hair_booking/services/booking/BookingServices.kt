package com.example.hair_booking.services.booking

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.hair_booking.model.Appointment
import com.example.hair_booking.model.Shift
import com.example.hair_booking.model.Stylist
import com.example.hair_booking.services.db.dbServices
import com.example.hair_booking.services.local.SalonServices
import com.example.hair_booking.services.local.StylistServices
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.collections.ArrayList

class BookingServices {
    companion object {

        // The average time between item in list of time display for user to pick
        // is based on the average time of services
        // In this case: the average time of services = 30 min => format in float will be: 0.5
        private val averageTimeBetweenService: Float = TimeServices.round(0.5F, 4)
        fun generateTimeBasedOnShift(shift: Shift, serviceDuration: Int, isToday: Boolean): ArrayList<String> {
            // Set the minimum time in list of time display for user to pick to the start hour of the shift
            val minTime = shift.startHour!!.toFloat()

            // Convert serviceDuration from minute to hour
            // Ex: 45 min (int type) => 0.75 hour
            var serviceDurationInHour: Float = TimeServices.minuteToHour(serviceDuration)
            var maxTime = TimeServices.minusTimeInHour(TimeServices.timeToDisplayToTimeInHour(shift.endHour!!.toFloat()), serviceDurationInHour)


            // Init available time with minTime
            var availableTime: ArrayList<String> = arrayListOf()

            var currentInHour = minTime!!
            while(currentInHour < maxTime) {
                // Ex: current = 15.2 (float) => current = "15.20" (string)
                var tmp: Float = TimeServices.timeInHourToTimeForDisplay(currentInHour)
                var timeToDisplay: String = TimeServices.setPrecision(tmp, 2).toString()

                if(timeToDisplay.toFloat() < 10)
                    timeToDisplay = "0$timeToDisplay" // Add leading zero for hour
                availableTime.add(timeToDisplay)

                currentInHour = TimeServices.addTimeInHour(currentInHour, averageTimeBetweenService)


            }
            return availableTime
        }

        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun getDisabledTimePositions(
            chosenServiceDuration: Int,
            availableTime: ArrayList<String>,
            bookingSalon: String,
            bookingDate: String,
            bookingShiftId: String,
            isToday: Boolean)
        : ArrayList<Int> {
            var disabledTimePositions: ArrayList<Int> = ArrayList()

            GlobalScope.async {
                var appointmentList: ArrayList<Appointment> = ArrayList()
                async {
                    appointmentList = dbServices.getAppointmentServices()!!.findAll()
                }.await()

                var filteredAppointmentList = filterAppointment(appointmentList, bookingSalon, bookingDate, bookingShiftId)

                // The first element in pair is bookingTime, the second one is service duration
                var appointmentTimeRanges: ArrayList<Pair<Float, Int>> = dbServices.getAppointmentServices()!!
                    .getAppointmentTimeRanges(appointmentList)


                // Compare each time range can be picked to all time range of appointment booked in database
                for(timeCanBePicked in availableTime) {
                    val timeIsAvailable: Boolean = timeRangeIsAvailable(
                        timeCanBePicked.toFloat(),
                        chosenServiceDuration,
                        bookingSalon,
                        bookingDate,
                        bookingShiftId,
                        isToday,
                        appointmentTimeRanges,
                        filteredAppointmentList
                    )

                    if(!timeIsAvailable) { // If time is not available
                        disabledTimePositions.add(availableTime.indexOf(timeCanBePicked) + 1) // plus 1 because of the placeholder is at index 0
                    }
                }

            }.await()

            return disabledTimePositions
        }


        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun timeRangeIsAvailable(startTime: Float,
                                         duration: Int,
                                         bookingSalon: String,
                                         bookingDate: String,
                                         bookingShiftId: String,
                                         isToday: Boolean,
                                         appointmentTimeRanges: ArrayList<Pair<Float, Int>>,
                                         appointmentList: ArrayList<Appointment>): Boolean {
            var isAvailable: Boolean = true

            // startTime is in display format, ex: 21h20m => 21.20

            // Compare time range to be checked to all time range of appointment booked in database
            // Convert from display format to hour, ex: 21.30 (21h30) => 21.5 hour
            var startTimeInHour: Float = TimeServices.timeToDisplayToTimeInHour(startTime)

            // Convert duration from minute to hour
            // Ex: 45 min (int type) => 0.75 hour
            var chosenServiceDurationInHour = TimeServices.minuteToHour(duration)

            // Estimated end time = time user picked + service picked duration
            var estimatedEndTime = TimeServices.addTimeInHour(startTimeInHour, chosenServiceDurationInHour)

            for (timeRange in appointmentTimeRanges) {
                // Time database returned is in display format => convert to hour
                val userPickedTimeInHour = TimeServices.timeToDisplayToTimeInHour(timeRange.first)
                val userPickedServiceDurationInHour = TimeServices.minuteToHour(timeRange.second)

                // Appointment end time = time user picked + service picked duration
                var appointmentEndTime = TimeServices.addTimeInHour(
                    userPickedTimeInHour,
                    userPickedServiceDurationInHour
                )

                // Compare time range can be picked to time range of appointment booked in database
                // If they conflict => the time can be picked became disabled
                if (TimeServices.checkTimeInHourConflict(Pair(startTimeInHour, estimatedEndTime), Pair(userPickedTimeInHour, appointmentEndTime))) {
                    isAvailable = false
                }
            }

            GlobalScope.async {
                // Check if there is any free stylist
                val busyStylistsIds: ArrayList<String> = getIdOfBusyStylistsAtSpecificTime(Pair(startTimeInHour, estimatedEndTime), appointmentList)

                val allStylist: ArrayList<Stylist> = dbServices.getStylistServices()!!.findAll()

                var availableStylist: ArrayList<Stylist> = ArrayList(allStylist)
                availableStylist.removeIf { stylist ->
                    busyStylistsIds.contains(stylist.id)
                            || stylist.workPlace!!.id != bookingSalon
                            || !StylistServices.isWorking(bookingShiftId, stylist.shifts!!)
                }
                if(availableStylist.size > 0)
                    isAvailable = true
            }.await()


            if (isToday) {
                // Disable all the time before the current time (user cannot pick the time that already passed)
                val currentTimeToDisplay: Float = TimeServices.getCurrentTimeInFloatFormat()
                val currentTimeInHour: Float =
                    TimeServices.timeToDisplayToTimeInHour(currentTimeToDisplay)

                if (startTimeInHour < currentTimeInHour)
                    isAvailable = false
            }

            return isAvailable
        }

        fun getIdOfBusyStylistsAtSpecificTime(timeRangeChosenInHour: Pair<Float, Float>, appointmentList: ArrayList<Appointment>): ArrayList<String> {
            var stylistIds: ArrayList<String> = ArrayList()

            for(appointment in appointmentList) {
                // Calculate appointment time range
                val appointmentStartTimeInHour: Float = TimeServices.timeToDisplayToTimeInHour(appointment.bookingTime!!.toFloat())
                var appointmentServiceDuration: Int = (appointment.service?.get("duration") as Long).toInt()
                val appointmentServiceDurationInHour: Float = TimeServices.minuteToHour(appointmentServiceDuration)
                val appointmentEndTimeInHour: Float = TimeServices.addTimeInHour(appointmentStartTimeInHour, appointmentServiceDurationInHour)

                val appointmentTimeRangeInHour: Pair<Float, Float> = Pair(appointmentStartTimeInHour, appointmentEndTimeInHour)
                if(TimeServices.checkTimeInHourConflict(timeRangeChosenInHour, appointmentTimeRangeInHour)) {
                    // If conflict
                    stylistIds.add((appointment.stylist?.get("id") as DocumentReference).id)
                }

            }
            return stylistIds
        }

        fun filterAppointment(appointmentList: ArrayList<Appointment>,
                              chosenSalon:String,
                              chosenDate: String,
                              chosenShiftId: String): ArrayList<Appointment> {
            var tmp: ArrayList<Appointment> = ArrayList(appointmentList)
            var result: ArrayList<Appointment> = ArrayList()
            for(appointment in tmp) {
                val appointmentChosenSalonId: String = (appointment.hairSalon?.get("id") as DocumentReference).id
                val appointmentChosenDate: String = appointment.bookingDate!!
                val appointmentChosenShiftId: String = appointment.bookingShift!!.id
                if(appointmentChosenSalonId == chosenSalon
                    && appointmentChosenDate == chosenDate
                    && appointmentChosenShiftId == chosenShiftId) {
                    result.add(appointment)
                }
            }

            return result
        }

        @RequiresApi(Build.VERSION_CODES.N)
        suspend fun getAvailableStylists(timeRangeChosenInHour: Pair<Float, Float>,
                                         chosenSalon:String,
                                         chosenDate: String,
                                         chosenShiftId: String): ArrayList<Stylist> {
            var appointmentList: ArrayList<Appointment> = ArrayList()

            GlobalScope.async {
                appointmentList = dbServices.getAppointmentServices()!!.findAll()
            }.await()

            var filteredAppointmentList = filterAppointment(appointmentList, chosenSalon, chosenDate, chosenShiftId)
            val busyStylistsIds: ArrayList<String> = getIdOfBusyStylistsAtSpecificTime(timeRangeChosenInHour, filteredAppointmentList)

            val allStylist: ArrayList<Stylist> = dbServices.getStylistServices()!!.findAll()

            var availableStylist: ArrayList<Stylist> = ArrayList(allStylist)
            availableStylist.removeIf { stylist ->
                busyStylistsIds.contains(stylist.id)
                        || stylist.workPlace!!.id != chosenSalon
                        || !StylistServices.isWorking(chosenShiftId, stylist.shifts!!)
            }

            return availableStylist
        }

        fun serializeAppointmentSaved(docSaved: HashMap<String, *>?): HashMap<String, Any?> {
            return hashMapOf(
                "userFullName" to (docSaved?.get("userFullName") ?: null),
                "hairSalonName" to (docSaved?.get("hairSalon") as HashMap<String, *>)["name"],
                "hairSalonAddress" to SalonServices.addressToString((docSaved?.get("hairSalon") as HashMap<String, *>)["address"] as HashMap<String, *>),
                "hairSalonPhoneNumber" to (docSaved?.get("hairSalon") as HashMap<String, *>)["phoneNumber"],
                "serviceTitle" to (docSaved?.get("service") as HashMap<String, *>)["title"],
                "stylist" to (docSaved?.get("stylist") as HashMap<String, *>)["fullName"],
                "bookingDate" to (docSaved?.get("bookingDate") ?: null),
                "bookingTime" to (docSaved?.get("bookingTime") ?: null),
                "discountTitle" to ((docSaved?.get("discountApplied") as HashMap<String, *>?)?.get("title") ?: null),
                "notes" to (docSaved?.get("notes") ?: null),
                "totalPrice" to (docSaved?.get("totalPrice") ?: null)
            )
        }
    }
}

