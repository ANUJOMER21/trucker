package com.trucker.com

import Api.Api
import Api.ApiClient
import App
import DeliveryImage
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import viewmodel.LoginHandler
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    // In your Activity or Fragment
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    fun scheduleLocationWork(context: Context, driverId: String) {
        val data = Data.Builder()
            .putString("driverId", driverId)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val locationWorkRequest = PeriodicWorkRequestBuilder<LocationWorker>(5, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "LocationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            locationWorkRequest
        )
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }
    @Composable
    fun Adminpanel(){
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            AndroidView(factory = {
                WebView(it).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadUrl(Api.Admin.url)
                }
            }, update = {
                it.settings.javaScriptEnabled = true
                it.loadUrl(Api.Admin.url)
            })
        }
    }
    @Composable
    fun WebviewScreen() {
        val adminpanel = remember { mutableStateOf(false) }
        val loginpanel = remember { mutableStateOf(false) }

        if (adminpanel.value) {
            Adminpanel()
        } else if (loginpanel.value) {
            Appscreen()
        } else {
            val gradientBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(3, 17, 79, 255),
                    Color(2, 69, 140, 255)
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = gradientBrush),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    // DeliveryImage at the top
                    DeliveryImage()
                    Spacer(modifier = Modifier.height(50.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome",
                            color = Color.White,
                            style = MaterialTheme.typography.h4,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                adminpanel.value = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(4, 117, 255, 255))
                        ) {
                            Text(text = "Admin Login", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                loginpanel.value = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(4, 117, 255, 255))
                        ) {
                            Text(text = "Driver Login", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }




    @Composable
    fun Appscreen(){
        var driverid:String =""
        App(){
            Log.d("driverid",it)
            driverid=it
            val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            //scheduleLocationWork(applicationContext, "$driverid")
            val editor = sharedPref.edit()
            editor.putString("driver", "$driverid")

            editor.apply()

            //   scheduleLocationWork(applicationContext, "$driverid")
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLocationPermissions()
        val serviceIntent = Intent(this, LocationService::class.java)
        startForegroundService( serviceIntent)

        setContent {
            WebviewScreen()

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, you can now access location
            } else {
                // Permission denied, handle accordingly
            }
        }
    }


}

