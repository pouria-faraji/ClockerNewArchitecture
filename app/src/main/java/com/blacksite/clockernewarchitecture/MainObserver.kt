package com.blacksite.clockernewarchitecture

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.util.Log
import com.blacksite.clockernewarchitecture.application.App
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.viewModel.MainViewModel



class MainObserver:LifecycleObserver {
    var mainViewModel:MainViewModel
    constructor(viewModel:MainViewModel){
        this.mainViewModel = viewModel
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        Log.e("logger", "created")
        this.mainViewModel.generated = false
        var bitmap = (ContextCompat.getDrawable(App.appContext!!, R.drawable.splash) as BitmapDrawable).bitmap
        Global.absolutePath = Global.saveToInternalStorage(bitmap, "Initialize")

//        val uri = Uri.parse("android.resource://"+"com.blacksite.clockernewarchitecture"+"/raw/"+Settings.DEFAULT_HAND_NAME)
//        var bitmap = BitmapFactory.decodeStream(FileInputStream(File(uri.path)))
//        Global.saveToInternalStorage(bitmap, Settings.DEFAULT_HAND_NAME)
//        this.mainViewModel.generateReducedBitmaps()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        Log.e("logger", "started")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        Log.e("logger", "resumed")
    }
}