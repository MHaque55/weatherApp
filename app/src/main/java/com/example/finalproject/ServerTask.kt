package com.example.finalproject

import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLConnection

class ServerTask : Thread {
    private lateinit var activity : MainActivity
    private var result : String = ""
    private val KEY : String = MainActivity.KEY
    private val MA : String = "MainActivity"
    private var settings : Settings = MainActivity.settings
    constructor( activity: MainActivity ) {
        this.activity = activity
    }
    override fun run() {
        super.run()
        try {
            //Log.w("MainActivity", "Starting?")
            var data : String = ""
            //var data = "key=" + KEY + "&q=" + MainActivity.latitude + "," + MainActivity.longitude
            //Log.w("MainActivity", data)
            if(settings.getLanguage() == "en") {
                data = "key=" + KEY + "&q=" + MainActivity.latitude + "," + MainActivity.longitude
                //Log.w("MainActivity", data)
            }

            else if (settings.getLanguage() != ""){
                data = "key=" + KEY + "&q=" + MainActivity.latitude + "," + MainActivity.longitude + "&lang=" + settings.getLanguage()
                //Log.w("MainActivity", data)
            }

            var url : URL = URL( MainActivity.URL )
            var connection : URLConnection = url.openConnection()
            //Log.w("MainActivity", "Starting2?")
            connection.doOutput = true
            var os : OutputStream = connection.getOutputStream()
            var osw : OutputStreamWriter = OutputStreamWriter( os )
            //Log.w("MainActivity", "Starting3?")
            osw.write( data )
            osw.flush( )
            var iStream : InputStream = connection.getInputStream()
            var isr : InputStreamReader = InputStreamReader( iStream )
            var br : BufferedReader = BufferedReader( isr )

            var line : String? = br.readLine()
            while (line != null )
            {
                result += line
                line = br.readLine()
            }
            var updateGui : UpdateGui = UpdateGui()
            activity.runOnUiThread( updateGui )
        } catch( e : Exception ) {
            //Log.w(MA, "Error?" + e.toString())
        }
    }
    inner class UpdateGui : Runnable {
        override fun run() {
            activity.updateView( result )
        }
    }
}