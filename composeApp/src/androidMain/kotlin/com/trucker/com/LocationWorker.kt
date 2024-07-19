package com.trucker.com

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import kotlinx.coroutines.runBlocking
import org.lighthousegames.logging.Log

class LocationWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val driverId = inputData.getString("driverId") ?: return Result.failure()

        // Get location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, returning failure
            return Result.failure()
        }

        val locationTask: Task<Location> = fusedLocationClient.lastLocation
        locationTask.addOnSuccessListener(OnSuccessListener { location ->
            if (location != null) {
                // Send location and driverId to the server
                runBlocking {   sendLocationToServer(driverId, location.latitude, location.longitude)}

            }
        })

        locationTask.addOnFailureListener(OnFailureListener {
            // Handle the failure
        })

        return Result.success()
    }

    private suspend fun sendLocationToServer(driverId: String, latitude: Double, longitude: Double) {
        // Your implementation to send location data to the server
       val res= Api.ApiClient().sendlocation(driverId,latitude.toString(),longitude.toString())
        Log.d("sendLocationToServer", res.toString())

    }
}
