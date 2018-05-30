package com.blacksite.clockernewarchitecture.repository

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.graphics.BitmapFactory
import com.blacksite.clockernewarchitecture.asyncTask.InsertAsyncTask
import com.blacksite.clockernewarchitecture.model.database.AppDatabase
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.model.interfaces.ClockDao
import com.google.firebase.firestore.FirebaseFirestore

import android.util.Log
import com.blacksite.clockernewarchitecture.application.Settings
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import com.blacksite.clockernewarchitecture.application.Global
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FileDownloadTask
import com.google.android.gms.tasks.OnSuccessListener
import java.io.FileInputStream


class ClockRepository {
    var db = FirebaseFirestore.getInstance()
    // Create a storage reference from our app
    var storage = FirebaseStorage.getInstance()
    // Create a storage reference from our app
    var storageRef = storage.reference

    private var clockDao:ClockDao? = null
//    var clockLiveData: LiveData<List<Clock>> = MutableLiveData()
    constructor(application: Application){
        this.clockDao = AppDatabase.getDatabase(application).clockDao()
    }
    fun getAllClocks(allClocksLiveData: MutableLiveData<List<Clock>>, fetchedNetwork: MutableLiveData<Boolean>, message: MutableLiveData<String>){
        allClocksLiveData.value = clockDao!!.getAll()
        db.collection(Settings.COLLECTION_NAME)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if(!Global.existInDB(allClocksLiveData.value!!, (document.data[Settings.DB_COLUMN_UID]as Long).toInt())) {
                                fetchedNetwork.value = false
                                var imageRef = storageRef.child(document.data[Settings.DB_COLUMN_IMAGE].toString())
                                var localFile = File.createTempFile("images", "jpg")
                                imageRef.getFile(localFile).addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot> {
                                    // Local temp file has been created
                                    var b = BitmapFactory.decodeStream(FileInputStream(localFile))
                                    Global.absolutePath = Global.saveToInternalStorage(b, document.data[Settings.DB_COLUMN_IMAGE].toString())

                                    imageRef = storageRef.child(document.data[Settings.DB_COLUMN_IMAGEWHITE].toString())
                                    localFile = File.createTempFile("images", "jpg")
                                    imageRef.getFile(localFile).addOnSuccessListener {
                                        b = BitmapFactory.decodeStream(FileInputStream(localFile))
                                        Global.absolutePath = Global.saveToInternalStorage(b, document.data[Settings.DB_COLUMN_IMAGEWHITE].toString())

                                        this.insert(Clock((document.data[Settings.DB_COLUMN_UID] as Long).toInt(),
                                                document.data[Settings.DB_COLUMN_IMAGE].toString(),
                                                document.data[Settings.DB_COLUMN_IMAGEWHITE].toString(),
                                                if (document.data[Settings.DB_COLUMN_NUMBER] != null) (document.data[Settings.DB_COLUMN_NUMBER] as Long?)!!.toInt() else (0),
                                                (document.data[Settings.DB_COLUMN_TYPE] as Long).toInt()), allClocksLiveData)

                                        fetchedNetwork.value = true
                                        Log.e("logger", "log")
                                    }
                                }).addOnFailureListener(OnFailureListener {
                                    // Handle any errors
                                    fetchedNetwork.value = true
                                    message.value = "This item is not available in your country."
                                    Log.d("logger", it.message)
                                    Log.d("logger", "Error-> " + document.data.toString())
                                })
                            }

                            Log.d("logger", document.id + " => " + document.data)
                        }
                    } else {
                        fetchedNetwork.value = true
                        message.value = "Error getting documents."
                        Log.w("logger", "Error getting documents.", task.exception)
                    }
                }
                .addOnFailureListener {
                    fetchedNetwork.value = true
                    message.value = "Error getting collection."
                    Log.w("logger", "Error getting collection.")
                }

//        return clockDao!!.getAll()
    }
    fun getClocks(type:Int, clockListLiveData: MutableLiveData<List<Clock>>){
//        refreshClockList()
        clockListLiveData.value = clockDao!!.getAllByType(type)

//        var handler = Handler()
//        handler.postDelayed({
//        }, 2000)
//        var handler2 = Handler()
//        handler2.postDelayed({
//        }, 5000)


//        refreshClockList()
//        clockListLiveData.value = clockDao!!.getAllByType(type)

//        return this.clockLiveData
    }

    private fun refreshClockList() {
//        this.insert(Clock(1, R.drawable.transparent_512, R.drawable.transparent_512, null, Clock.FACE))
//        this.insert(Clock(2, R.drawable.lion_512, R.drawable.lion_512_w, null, Clock.FACE))
//        this.insert(Clock(3, R.drawable.tiger_512, R.drawable.tiger_512_w, null, Clock.FACE))
//        this.insert(Clock(4, R.drawable.no_dial, R.drawable.no_dial, null, Clock.DIAL))
//        this.insert(Clock(5, R.drawable.dial_1, R.drawable.dial_1, null, Clock.DIAL))
//        this.insert(Clock(6, R.drawable.dial_2, R.drawable.dial_2, null, Clock.DIAL))
//        this.insert(Clock(7, R.drawable.dial_3, R.drawable.dial_3, null, Clock.DIAL))
//        this.insert(Clock(8, R.drawable.hand_1_grey, R.drawable.hand_1_grey, 1, Clock.HAND))
    }

    fun insert(clock: Clock, allClocksLiveData: MutableLiveData<List<Clock>>){
        InsertAsyncTask(this.clockDao!!, clock, allClocksLiveData).execute()
    }
}