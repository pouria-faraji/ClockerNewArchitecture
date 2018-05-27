package com.blacksite.clockernewarchitecture.application

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.blacksite.clockernewarchitecture.R
import java.io.*

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
        fun getColorNameByCode(code:Int):String{
            when(code){
                1 -> return "grey"
                2 -> return "blue"
                3 -> return "red"
                4 -> return "green"
                else -> return "grey"
            }
        }
        fun getCircleResourceByColorCode(code:Int):Int{
            when(code){
                1 -> return R.drawable.grey_circle
                2 -> return R.drawable.blue_circle
                3 -> return R.drawable.red_circle
                4 -> return R.drawable.green_circle
                else -> return R.drawable.grey_circle
            }
        }
        fun getSelectedCircleResourceByColorCode(code:Int):Int{
            when(code){
                1 -> return R.drawable.grey_circle_selected
                2 -> return R.drawable.blue_circle_selected
                3 -> return R.drawable.red_circle_selected
                4 -> return R.drawable.green_circle_selected
                else -> return R.drawable.grey_circle_selected
            }
        }
        fun saveToInternalStorage(bitmapImage: Bitmap): String {
            val cw = ContextWrapper(App.appContext)
            // path to /data/data/yourapp/app_data/imageDir
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val mypath = File(directory, "clock.png")

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(mypath)
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return directory.absolutePath
        }
        fun loadImageFromStorage(path: String):Bitmap {
            var b:Bitmap? = null
            try {
                val f = File(path, "clock.png")
                b = BitmapFactory.decodeStream(FileInputStream(f))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                return b!!
            }

        }

    }
}