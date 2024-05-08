package com.example.finalproject

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class Settings {
    private var lang : String = ""
    private var unitSys : String = ""
    constructor(context : Context) {
        val pref: SharedPreferences = context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE)

        pref.getString(LANGUAGE, "")?.let { setLanguage(it) }
        pref.getString(UNIT_SYSTEM, "")?.let { setUnitSys(it) }
    }

    fun setLanguage(lang : String) {
        this.lang = lang
    }

    fun setUnitSys(unitSys : String) {
        this.unitSys = unitSys
    }

    fun getLanguage() : String {
        return this.lang
    }

    fun getUnitSyst() : String {
        return this.unitSys
    }

    fun setPreferences(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
        val edit: SharedPreferences.Editor = pref.edit()
        edit.putString(LANGUAGE, this.lang)
        edit.putString(UNIT_SYSTEM, this.unitSys)
        edit.commit()
    }

    companion object {
        private const val LANGUAGE : String = "language"
        private const val UNIT_SYSTEM : String = "unitSystem"
    }
}

