package com.blacksite.clockernewarchitecture.asyncTask

import android.os.AsyncTask
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.model.interfaces.ClockDao

class InsertAsyncTask internal constructor(val clockDao: ClockDao, val clock: Clock) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void?): Void? {
        clockDao.insert(clock) // This line throws the exception
        return null
    }
}