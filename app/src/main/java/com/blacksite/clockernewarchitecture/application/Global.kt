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
import com.blacksite.clockernewarchitecture.model.database.Clock
import java.io.*


class Global {
    companion object {
//        var reducedBitmaps = HashMap<Int, Bitmap>()
        var absolutePath: String? = null
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
        fun saveToInternalStorage(bitmapImage: Bitmap, fileName:String = Settings.CLOCK_PNG): String {
            val cw = ContextWrapper(App.appContext)
            // path to /data/data/yourapp/app_data/imageDir
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val mypath = File(directory, fileName)

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
        fun loadImageFromStorage(path: String, fileName: String = Settings.CLOCK_PNG):Bitmap {
            var b:Bitmap? = null
            try {
                val f = File(path, fileName)
                b = BitmapFactory.decodeStream(FileInputStream(f))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                return b!!
            }

        }
        fun addUnique(list:ArrayList<Clock>, clock: Clock){
            var duplicate = false
            for(clockList in list){
                if(clockList.uid == clock.uid){
                    duplicate = true
                    continue
                }
            }
            if(!duplicate){
                list.add(clock)
            }
        }

        fun existInDB(clockList: List<Clock>, uid: Int): Boolean {
            for(clock in clockList){
                if(clock.uid == uid){
                    return true
                }
            }
            return false
        }

//        fun exportDB() {
//            try {
//
//                val sd = Environment.getExternalStorageDirectory()
//                val data = Environment.getDataDirectory()
//
//                //            if (sd.canWrite()) {
//                val currentDBPath = File.separator + "data" /*+File.separator+"user"+File.separator+"0"*/ + File.separator + "com.blacksite.clockernewarchitecture" + File.separator + "databases" + File.separator + Settings.DATABASE_NAME
//                val backupDBPath = File.separator + "DATABASE" + File.separator + "backup.db"
//                val currentDB = File(data, currentDBPath)
//                val backupDB = File(sd, backupDBPath)
//
//
//                if (currentDB.exists()) {
//                    val src = FileInputStream(currentDB).channel
//                    val dst = FileOutputStream(backupDB).channel
//                    dst.transferFrom(src, 0, src.size())
//                    src.close()
//                    dst.close()
//                    //                }
//                }
//            } catch (e: Exception) {
//                Log.e("Exception_Export", e.toString())
//            }
//
//        }

    }
}