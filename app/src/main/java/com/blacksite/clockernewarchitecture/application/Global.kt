package com.blacksite.clockernewarchitecture.application

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager

class Global {
    companion object {
//        var reducedBitmaps = HashMap<Int, Bitmap>()
        fun getStatusBarHeight(): Int {
            var result = 0
            val resourceId = App.appContext!!.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = App.appContext!!.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }
        fun getAppHeight(): Int {
            val metrics = DisplayMetrics()
            (App.appContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(metrics)
            //        App.getAppContext().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            val statusBarHeight = getStatusBarHeight()

            return metrics.heightPixels - statusBarHeight
        }
        fun getAppWidth(): Int {
            val metrics = DisplayMetrics()
            (App.appContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }
        fun dp_to_px(dp: Int): Int {
            val r = App.appContext!!.resources
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics)
            return px.toInt()
        }
        fun px_to_dp(px: Int): Int {
            val r = App.appContext!!.resources
            val dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px.toFloat(), r.displayMetrics)
            return dp.toInt()
        }
        fun toBitmap(resource:Int):Bitmap{
            return (ContextCompat.getDrawable(App.appContext!!, resource) as BitmapDrawable).bitmap
        }

    }
}