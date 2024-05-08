package com.example.finalproject

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    var settings : Settings = MainActivity.settings
    private lateinit var radioMetric : RadioButton
    private lateinit var radioUS : RadioButton
    private lateinit var radioEnglish : RadioButton
    private lateinit var radioBengali : RadioButton
    private lateinit var radioEspanyol : RadioButton
    private lateinit var radioFrench : RadioButton
    private lateinit var radioHindi : RadioButton
    private lateinit var saveSettings : Button
    private lateinit var closeButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        radioMetric = findViewById(R.id.metric)
        radioUS = findViewById(R.id.us)
        radioEnglish = findViewById(R.id.english)
        radioBengali = findViewById(R.id.bengali)
        radioEspanyol = findViewById(R.id.spanish)
        radioFrench = findViewById(R.id.french)
        radioHindi = findViewById(R.id.hindi)

        updateView()

        saveSettings = findViewById(R.id.save_settings)
        closeButton = findViewById(R.id.close_button)

        saveSettings.setOnClickListener{goBack()}
        closeButton.setOnClickListener{close()}
    }

    fun goBack() {
        updateModel()
        finish()
    }

    fun close() {
        finish()
    }

    fun updateView() {
        if(settings.getUnitSyst() == "Metric") {
            radioMetric.isChecked = true
        }
        else if (settings.getUnitSyst() == "US") {
            radioUS.isChecked = true
        }

        if(settings.getLanguage() == "en") {
            radioEnglish.isChecked = true
        }
        else if (settings.getLanguage() == "bn") {
            radioBengali.isChecked = true
        }
        else if (settings.getLanguage() == "es") {
            radioEspanyol.isChecked = true
        }
        else if (settings.getLanguage() == "fr") {
            radioFrench.isChecked = true
        }
        else if (settings.getLanguage() == "hi") {
            radioHindi.isChecked = true
        }
    }

    fun updateModel() {
        if(radioMetric.isChecked) {
            settings.setUnitSys("Metric")
            settings.setPreferences(this)
        }
        else if(radioUS.isChecked) {
            settings.setUnitSys("US")
            settings.setPreferences(this)
        }

        if(radioEnglish.isChecked) {
            settings.setLanguage("en")
            settings.setPreferences(this)
        }
        else if(radioBengali.isChecked) {
            settings.setLanguage("bn")
            settings.setPreferences(this)
        }
        else if(radioEspanyol.isChecked) {
            settings.setLanguage("es")
            settings.setPreferences(this)
        }
        else if(radioFrench.isChecked) {
            settings.setLanguage("fr")
            settings.setPreferences(this)
        }
        else if(radioHindi.isChecked) {
            settings.setLanguage("hi")
            settings.setPreferences(this)
        }

    }
}