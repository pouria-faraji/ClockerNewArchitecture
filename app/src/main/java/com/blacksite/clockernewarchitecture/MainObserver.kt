package com.blacksite.clockernewarchitecture

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.util.Log
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