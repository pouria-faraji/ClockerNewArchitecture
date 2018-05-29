package com.blacksite.clockernewarchitecture.application

import android.content.Context
import android.content.SharedPreferences
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


/**
 * Created by p.faraji on 4/18/2018.
 */
class PrefManager(internal var _context: Context) {
    internal var pref: SharedPreferences
    internal var editor: SharedPreferences.Editor

    // shared pref mode
    internal var PRIVATE_MODE = 0

    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    var facePosition: Int
        get() = pref.getInt(FACE_POSITION, 0)
        set(position){
            editor.putInt(FACE_POSITION, position)
            editor.commit()
        }
    var dialPosition: Int
        get() = pref.getInt(DIAL_POSITION, 0)
        set(position){
            editor.putInt(DIAL_POSITION, position)
            editor.commit()
        }
    var handPosition: Int
        get() = pref.getInt(HAND_POSITION, 0)
        set(position){
            editor.putInt(HAND_POSITION, position)
            editor.commit()
        }
    var faceColor: String
        get() = pref.getString(FACE_COLOR, "#000000")
        set(color){
            editor.putString(FACE_COLOR, color)
            editor.commit()
        }
    var faceColorDialog: String
        get() = pref.getString(FACE_COLOR_DIALOG, "#00fff4")
        set(color){
            editor.putString(FACE_COLOR_DIALOG, color)
            editor.commit()
        }
    var dialColor: String
        get() = pref.getString(DIAL_COLOR, "#ffffff")
        set(color){
            editor.putString(DIAL_COLOR, color)
            editor.commit()
        }
    var whiteBackgroundCheck: Boolean
        get() = pref.getBoolean(WHITE_BACKGROUND_CHECK, true)
        set(check){
            editor.putBoolean(WHITE_BACKGROUND_CHECK, check)
            editor.commit()
        }
    var dialBackgroundCheck: Boolean
        get() = pref.getBoolean(DIAL_BACKGROUND_CHECK, true)
        set(check){
            editor.putBoolean(DIAL_BACKGROUND_CHECK, check)
            editor.commit()
        }
    var faceCheck: Boolean
        get() = pref.getBoolean(FACE_CHECK, true)
        set(check){
            editor.putBoolean(FACE_CHECK, check)
            editor.commit()
        }
    var cachedBitmap: String
        get() = pref.getString(CACHED_BITMAP, "null")
        set(cachedBitmap){
            editor.putString(CACHED_BITMAP, cachedBitmap)
            editor.commit()
        }
    var colorCode: Int
        get() = pref.getInt(COLOR_CODE, 1)//1 -> grey, 2 -> blue, 3 -> red, 4 -> green
        set(colorCode){
            editor.putInt(COLOR_CODE, colorCode)
            editor.commit()
        }
    var handsList: ArrayList<Clock>
        get() {
            var gson = Gson()
            var json = pref.getString(HANDS_LIST, "[]")
            if(json == "[]"){
                var tempList:ArrayList<Clock> = arrayListOf(Clock(1, Settings.DEFAULT_HAND_NAME, Settings.DEFAULT_HAND_NAME, 1, Clock.HAND))
                json = gson.toJson(tempList)
            }
            val type = object : TypeToken<ArrayList<Clock>>() {}.type
            return gson.fromJson(json, type)
        }
    set(handsList){
        var gson = Gson()
        var json = gson.toJson(handsList)
        editor.putString(HANDS_LIST, json)
        editor.commit()
    }
    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    companion object {

        // Shared preferences file name
        private const val PREF_NAME = "clocker_preference"

        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

        private const val FACE_POSITION = "facePosition"
        private const val DIAL_POSITION = "dialPosition"
        private const val HAND_POSITION = "handPosition"
        private const val FACE_COLOR =  "faceColor"
        private const val FACE_COLOR_DIALOG =  "faceColorDialog"
        private const val DIAL_COLOR =  "dialColor"
        private const val WHITE_BACKGROUND_CHECK =  "whiteBackgroundCheck"
        private const val DIAL_BACKGROUND_CHECK =  "dialBackgroundCheck"
        private const val FACE_CHECK =  "faceCheck"
        private const val CACHED_BITMAP =  "cachedBitmap"
        private const val COLOR_CODE =  "colorCode"
        private const val HANDS_LIST =  "handsList"
    }
}