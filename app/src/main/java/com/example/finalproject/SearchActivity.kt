package com.example.finalproject

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
//import com.example.firebasedemo.MainActivity
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    var settings : Settings = MainActivity.settings
    private var MA : String = "MainActivity"
    private var locationArrayList : ArrayList<String> = ArrayList<String>()
    private var checkArrayList :ArrayList<String> = ArrayList<String>()
    private var inList : Boolean = false
    private var isActivityStarted : Boolean = true
    private lateinit var arrayList: ArrayList<*>
    private lateinit var arraySet : Set<String>
    private lateinit var backButton : Button
    private lateinit var clearButton: Button
    private lateinit var searchView: SearchView
    private lateinit var weatherText1: TextView
    private lateinit var weatherText2: TextView
    private lateinit var weatherText3: TextView
    private lateinit var weatherText4: TextView
    private lateinit var weatherText5: TextView
    private lateinit var weatherText6: TextView
    private var arrListTv : ArrayList<TextView> = ArrayList<TextView>()
    private lateinit var currentDisplayView : TextView

    private lateinit var weatherIcon1: ImageView
    private lateinit var weatherIcon2: ImageView
    private lateinit var weatherIcon3: ImageView
    private lateinit var weatherIcon4: ImageView
    private lateinit var weatherIcon5: ImageView
    private lateinit var weatherIcon6: ImageView
    private var arrListIv : ArrayList<ImageView> = ArrayList<ImageView>()
    private lateinit var currImg : ImageView
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.back_button)
        clearButton = findViewById(R.id.clear_button)
        backButton.setOnClickListener { goBack() }
        weatherText1 = findViewById(R.id.text_view_1)
        arrListTv.add(weatherText1)
        weatherIcon1 = findViewById(R.id.img_view_1)
        arrListIv.add(weatherIcon1)
        weatherText2 = findViewById(R.id.text_view_2)
        arrListTv.add(weatherText2)
        weatherIcon2 = findViewById(R.id.img_view_2)
        arrListIv.add(weatherIcon2)
        weatherText3 = findViewById(R.id.text_view_3)
        arrListTv.add(weatherText3)
        weatherIcon3 = findViewById(R.id.img_view_3)
        arrListIv.add(weatherIcon3)
        weatherText4 = findViewById(R.id.text_view_4)
        arrListTv.add(weatherText4)
        weatherIcon4 = findViewById(R.id.img_view_4)
        arrListIv.add(weatherIcon4)
        weatherText5 = findViewById(R.id.text_view_5)
        arrListTv.add(weatherText5)
        weatherIcon5 = findViewById(R.id.img_view_5)
        arrListIv.add(weatherIcon5)
        weatherText6 = findViewById(R.id.text_view_6)
        arrListTv.add(weatherText6)
        weatherIcon6 = findViewById(R.id.img_view_6)
        arrListIv.add(weatherIcon6)
        currentDisplayView = findViewById(R.id.curr_res_view)
        currImg = findViewById(R.id.curr_img)

        var firebase : FirebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebase.getReference("locations")
        var listener : SearchActivity.DataListener = DataListener()
        reference.addValueEventListener( listener )
        backButton.setOnClickListener{goBack()}
        clearButton.setOnClickListener{clearAll()}

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            getLocation()
        } else {
            // Request location permission
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    fetchWeatherData(query, true, currentDisplayView, currImg)
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
    inner class DataListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var key :String? = snapshot.key
            Log.w( MA, "Activity Started " +  isActivityStarted)
            var valueObject : Any? = snapshot.value
            if( valueObject != null ) {
                var value : String = valueObject.toString()
                if (valueObject is ArrayList<*> && isActivityStarted) {
                    Log.w( MA, "It is is ")
                    arrayList = valueObject as ArrayList<*>
                    for(i in arrayList) {
                        //Log.w( MA, "value is " + i)
                        locationArrayList.add(i.toString())
                        //Log.w(MA, "size of the array = " + arrayList.size)
                    }
                    if(isActivityStarted) {
                        showList(0)
                    }
                    isActivityStarted = false
                }
            } else {
                isActivityStarted = false
                Log.w( MA, "No value found" )
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w( MA, "reading failure: " + error.message )
        }
    }

    private fun fetchWeatherData(city: String, isAdding : Boolean, tView : TextView, imgView: ImageView) {
        Log.w("fetchWeatherData", "Fetching weather data for city: $city")

        val url = "https://api.weatherapi.com/v1/current.json?key=6a86cb3210d442be873172901230110&q=$city"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.w("fetchWeatherData", "Response received: $response")
                handleResponse(response, isAdding, tView, imgView)
            },
            { error ->
                Log.e("Volley Error", "Error fetching data: $error")
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun getLocation() {
        // Get the last known location from the fused location provider
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Get city name from coordinates
                    val cityName = getCityName(location.latitude, location.longitude)
                    Toast.makeText(this, "Current city: $cityName", Toast.LENGTH_SHORT).show()
                    fetchWeatherData(cityName, false, currentDisplayView, currImg)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val city = addresses[0].locality
                return city ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
    private fun handleResponse(response: JSONObject, isAdding: Boolean, tv: TextView, img: ImageView) {

        val locationInfo = response.getJSONObject("location")
        val city:String = locationInfo.getString("name")
        val region:String = locationInfo.getString("region")
        var country:String = locationInfo.getString("country")
        if(country == "United States of America") {
            country = "USA"
        }
        if(country == "United Kingdom") {
            country = "UK"
        }
        val locationInfoString:String ="$city, $region, $country"
        if(isAdding) {
            for(i in locationArrayList) {
                if (i == city) {
                    inList = true
                }
            }
            if(!inList && locationArrayList.size < 6) {
                locationArrayList.add(city)
                for(i in 5 downTo 1) {
                    arrListTv[i].text = arrListTv[i-1].text
                    val drawableResId = arrListIv[i-1].drawable
                    arrListIv[i].setImageDrawable(drawableResId)
                }
                fetchWeatherData(city, false, arrListTv[0], arrListIv[0])
            }
            else if(!inList && locationArrayList.size == 6) {
                locationArrayList.removeAt(0)
                locationArrayList.add(city)
                for(i in 5 downTo 1) {
                    arrListTv[i].text = arrListTv[i - 1].text
                    val drawableResId = arrListIv[i-1].drawable
                    arrListIv[i].setImageDrawable(drawableResId)
                }
                fetchWeatherData(city, false, arrListTv[0], arrListIv[0])
            }
            inList = false
            reference.setValue(locationArrayList)
            Log.w("MainActivity", locationArrayList.toString())
        }


        val main = response.getJSONObject("current")
        val temp_C = main.getDouble("temp_c")
        val temp_f = main.getDouble("temp_f")
        val condition = main.getJSONObject("condition").getString("text")
        val weatherInfoString:String = "$condition - $temp_C°C ($temp_f°F)\n"

        var iconUrl:String = main.getJSONObject("condition").getString("icon")
        iconUrl = "https:$iconUrl"
        loadImageFromUrl(iconUrl, currImg)

        // Update the weatherText

        showText("$locationInfoString\n$weatherInfoString", currentDisplayView)
        if(!isAdding) {
            tv.text = "$locationInfoString\n$weatherInfoString"
            loadImageFromUrl(iconUrl, img)
        }
    }

    private fun showText(text : String, tv : TextView) {
        tv.text = text
    }

    private fun loadImageFromUrl(url: String, img: ImageView) {
        val imageLoader = ImageLoader(Volley.newRequestQueue(this), object : ImageLoader.ImageCache {
            override fun getBitmap(url: String): Bitmap? {
                return null
            }
            override fun putBitmap(url: String, bitmap: Bitmap) {
            }
        })
        val imageListener = ImageLoader.getImageListener(img, 0, android.R.drawable.ic_dialog_alert)
        imageLoader.get(url, imageListener)
    }

    fun clearAll() {
        locationArrayList = ArrayList<String>()
        reference.setValue(locationArrayList)
        clearList()
    }

    fun clearList(){
        for(i in arrListTv) {
            i.text = ""
        }
        for(i in arrListIv) {
            i.setImageDrawable(null)
        }
    }

    fun showList(idx : Int) {
        var index = idx
        for (i in locationArrayList) {
            fetchWeatherData(i, false, arrListTv[index], arrListIv[index])
            index += 1
        }
    }
    fun goBack() {
        finish()
    }

}