package com.blacksite.clockernewarchitecture.asyncTask

import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.model.interfaces.ClockDao

class InsertAsyncTask internal constructor(val clockDao: ClockDao, val clock: Clock, allClocksLiveData: MutableLiveData<List<Clock>>) : AsyncTask<Void, Void, Void>() {

    var _allClocksLiveData:MutableLiveData<List<Clock>> = allClocksLiveData
    override fun doInBackground(vararg params: Void?): Void? {
        clockDao.insert(clock) // This line throws the exception
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        _allClocksLiveData.value = clockDao!!.getAll()

    }
}