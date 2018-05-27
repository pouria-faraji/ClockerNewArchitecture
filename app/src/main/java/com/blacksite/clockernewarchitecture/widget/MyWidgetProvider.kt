package com.blacksite.clockernewarchitecture.widget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.content.Intent
import android.util.Log
import android.app.PendingIntent
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.application.App
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.PrefManager
import com.blacksite.clockernewarchitecture.view.MainActivity
import com.blacksite.clockernewarchitecture.viewModel.MainViewModel


/**
 * Created by p.faraji on 4/18/2018.
 */
class MyWidgetProvider : AppWidgetProvider() {
    var views: RemoteViews? = null
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.e("logger", "widget updated")
    }

    override fun onReceive(context: Context, intent: Intent) {
        var prefManager = PrefManager(context)
        //find out the action
        val action = intent.action
        //is it time to update
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == action) {
            views = RemoteViews(context.packageName,
                    R.layout.widget)

            if(prefManager!!.cachedBitmap != "null") {
                Log.e("logger", prefManager!!.cachedBitmap)
                views!!.setImageViewBitmap(R.id.clock_face_imageview_widget, Global.loadImageFromStorage(prefManager!!.cachedBitmap))
//                views!!.setImageViewResource(R.id.clock_face_imageview_widget, R.drawable.splash_red)
            }

            MainViewModel.makeAllGoneWidget(context, prefManager, views!!)
//            hand.makeAllGoneWidget(context, hand.loadHands()[prefManager!!.handPosition].number!!, prefManager!!.colorCode,views!!)


            val choiceIntent = Intent(context, MainActivity::class.java)
            val clickPendIntent = PendingIntent.getActivity(context, 0, choiceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views!!.setOnClickPendingIntent(R.id.widget_layout, clickPendIntent)
            AppWidgetManager.getInstance(context).updateAppWidget(intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS), views)
        }
    }
}