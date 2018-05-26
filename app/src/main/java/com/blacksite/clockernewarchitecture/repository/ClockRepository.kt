package com.blacksite.clockernewarchitecture.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.asyncTask.InsertAsyncTask
import com.blacksite.clockernewarchitecture.model.database.AppDatabase
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.model.interfaces.ClockDao

class ClockRepository {
    private var clockDao:ClockDao? = null
//    var clockLiveData: LiveData<List<Clock>> = MutableLiveData()
    constructor(application: Application){
        this.clockDao = AppDatabase.getDatabase(application).clockDao()
    }
    fun getAllClocks():LiveData<List<Clock>>{
//        refreshClockList()
        return clockDao!!.getAll()
    }
    fun getFaces():List<Clock>{
        return clockDao!!.getAllByType(Clock.FACE)
    }
    fun getClocks(type:Int, clockListLiveData: MutableLiveData<List<Clock>>){
        refreshClockList()
        clockListLiveData.value = clockDao!!.getAllByType(type)

//        return this.clockLiveData
    }

    private fun refreshClockList() {
        this.insert(Clock(1, R.drawable.transparent_512, R.drawable.transparent_512, null, Clock.FACE))
        this.insert(Clock(2, R.drawable.lion_512, R.drawable.lion_512_w, null, Clock.FACE))
        this.insert(Clock(3, R.drawable.tiger_512, R.drawable.tiger_512_w, null, Clock.FACE))
        this.insert(Clock(4, R.drawable.no_dial, R.drawable.no_dial, null, Clock.DIAL))
        this.insert(Clock(5, R.drawable.dial_1, R.drawable.dial_1, null, Clock.DIAL))
        this.insert(Clock(6, R.drawable.dial_2, R.drawable.dial_2, null, Clock.DIAL))
        this.insert(Clock(7, R.drawable.dial_3, R.drawable.dial_3, null, Clock.DIAL))
        this.insert(Clock(8, R.drawable.hand_1_grey, R.drawable.hand_1_grey, 1, Clock.HAND))
    }

    fun insert(clock: Clock){
        InsertAsyncTask(this.clockDao!!, clock).execute()
    }
}