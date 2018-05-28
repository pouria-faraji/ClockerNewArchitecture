package com.blacksite.clockernewarchitecture.application

class Settings {
    companion object {
        val DATABASE_NAME = "ClockerDB"
        val COLLECTION_NAME = "clock"
        val CLOCK_PNG = "clock.png"
        val DEFAULT_HAND_NAME = "hand_1_grey"
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