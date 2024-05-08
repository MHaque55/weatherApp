package com.example.finalproject
class Location {
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    private var temp : Double = 0.0
    private var city : String = ""
    private var region : String = ""
    private var country : String = ""
    private var wind : Double = 0.0
    private var pressure : Double = 0.0
    private var humidity : Double = 0.0
    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
    fun setLocation(latitude : Double, longitude : Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun getLatitude() : Double {
        return this.latitude
    }

    fun getLongitude() : Double {
        return this.longitude
    }

    fun setTemp(temp : Double) {
        this.temp = temp
    }
    fun getTemp() : Double {
        return this.temp
    }

    fun setCity (city : String) {
        this.city = city
    }

    fun getCity() : String {
        return this.city
    }

    fun setRegion (reg : String) {
        this.region = region
    }

    fun getRegion() : String {
        return this.region
    }
    fun setCountry (country : String) {
        this.country = country
    }

    fun getCountry() : String {
        return this.country
    }
}