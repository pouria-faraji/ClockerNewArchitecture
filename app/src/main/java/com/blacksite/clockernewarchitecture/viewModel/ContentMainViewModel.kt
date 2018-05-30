package com.blacksite.clockernewarchitecture.viewModel

import android.app.Application
import android.app.WallpaperManager
import android.arch.lifecycle.AndroidViewModel
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.PrefManager
import com.blacksite.clockernewarchitecture.application.Settings

class ContentMainViewModel(application: Application) : AndroidViewModel(application) {
    var clockFaceImageviewWidth:Float
    var clockFaceImageviewHeight:Float
    var clockFaceImageviewPadding:Float
    var clockDialImageviewWidth:Float
    var clockDialImageviewHeight:Float
    var clockMainLayoutHeight:Float
    var clockWallpaperHeight:Float
    var whiteBackgroundCheck:Boolean
    var dialBackgroundCheck:Boolean
    var faceCheck:Boolean
    var prefManager = PrefManager(application)
    init {
        this.clockFaceImageviewWidth = Settings.CLOCK_FACE_IMAGEVIEW_WIDTH.toFloat()
        this.clockFaceImageviewHeight = Settings.CLOCK_FACE_IMAGEVIEW_HEIGHT.toFloat()
        this.clockFaceImageviewPadding = Settings.CLOCK_FACE_IMAGEVIEW_PADDING.toFloat()
        this.clockDialImageviewWidth = Settings.CLOCK_DIAL_IMAGEVIEW_WIDTH.toFloat()
        this.clockDialImageviewHeight = Settings.CLOCK_DIAL_IMAGEVIEW_HEIGHT.toFloat()
        this.clockMainLayoutHeight = Settings.CLOCK_MAIN_LAYOUT_HEIGHT.toFloat()
        this.clockWallpaperHeight = Settings.CLOCK_WALLPAPER_HEIGHT.toFloat()
        this.whiteBackgroundCheck = prefManager.whiteBackgroundCheck
        this.dialBackgroundCheck = prefManager.dialBackgroundCheck
        this.faceCheck = prefManager.faceCheck
    }
    fun getWallpaperAsBitmap():Bitmap{
        var wallpaperManager: WallpaperManager = WallpaperManager.getInstance(getApplication())
        var wallpaperDrawable = wallpaperManager.drawable

        val mbitmap = (wallpaperDrawable as BitmapDrawable).bitmap
        val imageRounded = Bitmap.createBitmap(mbitmap.width, mbitmap.height, mbitmap.config)
//        val imageRounded = Bitmap.createBitmap(200, 200, mbitmap.config)
        val canvas = Canvas(imageRounded)
        val mpaint = Paint()
        mpaint.isAntiAlias = true
        mpaint.shader = BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
//        canvas.drawRoundRect(RectF(0f, 0f, mbitmap.width.toFloat(), mbitmap.height.toFloat()), 70f, 70F, mpaint)// Round Image Corner 100 100 100 100
        canvas.drawRoundRect(RectF(0f, 0f, (Global.getAppWidth()-(2*Global.dp_to_px(8))).toFloat(), clockWallpaperHeight), Global.dp_to_px(8).toFloat(), Global.dp_to_px(8).toFloat()
                , mpaint)// Round Image Corner 100 100 100 100

        return imageRounded
    }
}