package com.example.finalproject

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.text.SimpleDateFormat
import com.bumptech.glide.Glide
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {
    private lateinit var dateView : TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val MA : String = "MainActivity"
//    private var latitude : Double = 0.0
//    private var longitude : Double = 0.0
    private lateinit var locView : TextView
    private lateinit var img : ImageView
    private lateinit var tempView : TextView
    private lateinit var windView: TextView
    private lateinit var pressureView : TextView
    private lateinit var humidityView: TextView
    private lateinit var weatherCondView: TextView

    private lateinit var settingButton : Button
    private lateinit var searchButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dateView = findViewById(R.id.current_date_view)
        locView = findViewById(R.id.location_view)
        img = findViewById(R.id.picture_view)
        tempView = findViewById(R.id.temp_view)
        windView = findViewById(R.id.wind_view)
        pressureView = findViewById(R.id.pressure_view)
        humidityView = findViewById(R.id.humidity_view)
        weatherCondView = findViewById(R.id.weather_cond_view)
        settingButton = findViewById(R.id.settings_button)
        searchButton = findViewById(R.id.search_button)

        val formattedDate = getCurrentDate()
        dateView.setText(formattedDate)

        settings = Settings(this)
        settingButton.setOnClickListener{modifySettings()}

        searchButton.setOnClickListener{searchCity()}

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkLocationPermission()) {
            val locationRequest = requestLocation()
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                        val lastLocation: Location? = locationResult.lastLocation
                        //Log.w(MA, "Something: " + lastLocation.toString())
                        // Do something with the location
                        if (lastLocation != null) {
                            latitude = lastLocation.latitude
                            longitude = lastLocation.longitude
                            //Log.w(MA, "Lat $latitude and Long $longitude")
                        }
                        else {
                            Log.w(MA, "LastLocation null")
                        }
                        // Here you can use the latitude and longitude to do whatever you need
                    }
                },
                null
            )
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        val timer = Timer()
        val delayInMillis = 0L // Delay before the first execution (in milliseconds)
        val periodInMillis = 5000L // Interval between executions (in milliseconds)

        timer.scheduleAtFixedRate(delayInMillis, periodInMillis) {
            var task : ServerTask = ServerTask( this@MainActivity )
            task.start()
        }
//        var task : ServerTask = ServerTask( this )
//        task.start()
    }

    fun checkLocationPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocation() : LocationRequest {
        val interval = 50000
        val locationFastestInterval = 10000
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,
            interval.toLong()
        )
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(locationFastestInterval.toLong())
            .build()
        return locationRequest
    }
    fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH)
        return dateFormat.format(currentDate)
    }

    fun updateView(json : String) {
        try {
            var jsonObject : JSONObject = JSONObject(json)
            var location : JSONObject = jsonObject.getJSONObject("location")
            var city : String = location.getString("name")
            var region : String = location.getString("region")
            var country : String = location.getString("country")
            if(country == "United States of America") {
                country = "USA"
            }
            locView.text = "$city, $region, $country"

            var current : JSONObject = jsonObject.getJSONObject("current")
            if(settings.getUnitSyst() == "Metric") {
                var temp : Double = current.getDouble("temp_c") //change it to temp_c if settings in celsius
                tempView.text = "$temp\u00B0 C"
                var wind : Double = current.getDouble("wind_kph")
                var pressure : Double = current.getDouble("pressure_mb")

                windView.text = "$wind kph"
                pressureView.text = "$pressure mb"
            }
            else {
                var temp : Double = current.getDouble("temp_f") //change it to temp_c if settings in celsius
                tempView.text = "$temp\u00B0 F"
                var wind : Double = current.getDouble("wind_mph")
                var pressure : Double = current.getDouble("pressure_in")

                windView.text = "$wind mph"
                pressureView.text = "$pressure inHg"
            }

            var condition : JSONObject = current.getJSONObject("condition")
            var icon : String = condition.getString("icon")
            var iconFullUrl = "https:$icon"

            Glide.with(this)
                .load(iconFullUrl)
                .into(img)

            var text : String = condition.getString("text")
            weatherCondView.text = text

            var humidity : Double = current.getDouble("humidity")
            humidityView.text = "$humidity%"

            //tvTextView.text = language
        } catch ( e : JSONException) {
            // do something here
        }
    }
    override fun onRestart() {
        super.onRestart()
        Log.w("MainActivity", "onRestart")
    }
    override fun onStart() {
        super.onStart()
        Log.w("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.w("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.w("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.w("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w("MainActivity", "onDestroy")
    }

    fun modifySettings() {
        var myIntent : Intent = Intent (this, SettingsActivity::class.java)
        startActivity(myIntent)
    }

    fun searchCity() {
        var myIntent : Intent = Intent (this, SearchActivity::class.java)
        startActivity(myIntent)
    }
    companion object {
        const val KEY : String = "08b1c8b34f6b4cdaa7a30735242804"
        const val URL : String = "https://api.weatherapi.com/v1/current.json"//?key=$KEY"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        var latitude : Double = 0.0
        var longitude : Double = 0.0
        lateinit var settings : Settings
        lateinit var loc : Location
    }
}