package com.blacksite.clockernewarchitecture.application

class Settings {
    companion object {
        const val DATABASE_NAME = "ClockerDB"
        const val COLLECTION_NAME = "clock"
        const val CLOCK_PNG = "clock.png"
        const val DEFAULT_HAND_NAME = "hand_1_grey"
        const val NO_ERROR = "no_error"
        const val DB_COLUMN_UID = "uid"
        const val DB_COLUMN_IMAGE = "image"
        const val DB_COLUMN_IMAGEWHITE = "imageWhite"
        const val DB_COLUMN_TYPE = "type"
        const val DB_COLUMN_NUMBER = "number"
        const val DATABASE_VERSION = 1
        val CLOCK_FACE_IMAGEVIEW_WIDTH = Global.getAppWidth()/2
        val CLOCK_FACE_IMAGEVIEW_HEIGHT = Global.getAppWidth()/2
        val CLOCK_FACE_IMAGEVIEW_PADDING = CLOCK_FACE_IMAGEVIEW_WIDTH/5
        val CLOCK_DIAL_IMAGEVIEW_WIDTH = Global.getAppWidth()/2
        val CLOCK_DIAL_IMAGEVIEW_HEIGHT = Global.getAppWidth()/2
        val CLOCK_MAIN_LAYOUT_HEIGHT = Global.getAppWidth()/2
        val CLOCK_WALLPAPER_HEIGHT = Global.getAppWidth()/2
        const val NUMBER_OF_ITEMS_EACH_ROW = 3
        const val COLOR_CODE_GREY = 1
        const val COLOR_CODE_BLUE = 2
        const val COLOR_CODE_RED = 3
        const val COLOR_CODE_GREEN = 4
    }
}