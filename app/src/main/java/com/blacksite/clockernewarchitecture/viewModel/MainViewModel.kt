package com.blacksite.clockernewarchitecture.viewModel

import android.app.Application
import android.arch.lifecycle.*
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import com.blacksite.clocker.application.PrefManager
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.adapter.ItemAdapter
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.model.GridItem
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.repository.ClockRepository


class MainViewModel(application: Application) : AndroidViewModel(application) {
    //This is a Test commit
    var clockRepository:ClockRepository = ClockRepository(application)
    var clockLiveData:MutableLiveData<List<Clock>> = MutableLiveData<List<Clock>>()
    var allClocksLiveData:LiveData<List<Clock>>
    var mode:MutableLiveData<Int> = MutableLiveData() // 1 -> Face, 2 -> Dial, 3 -> Hand
    var prefManager:PrefManager = PrefManager(application)
    var reducedBitmaps = MutableLiveData<HashMap<Int, Bitmap>>()
    var currentFacePosition = MutableLiveData<Int>()
    var currentDialPosition = MutableLiveData<Int>()
    var currentHandPosition = MutableLiveData<Int>()
    var whiteBackgroundCheck = MutableLiveData<Boolean>()
    var faces = ArrayList<Clock>()
    var generated = false
    init {
        currentFacePosition.value = prefManager!!.facePosition
        currentDialPosition.value = prefManager!!.dialPosition
        currentHandPosition.value = prefManager!!.handPosition
        mode.value = Clock.FACE
        clockRepository.getClocks(mode.value!!, clockLiveData)
        allClocksLiveData = clockRepository.getAllClocks()
//        clockRepository.getAllClocks(allClocksLiveData)
        whiteBackgroundCheck.value = prefManager!!.whiteBackgroundCheck
    }
    fun loadClocksLiveData():MutableLiveData<List<Clock>>{
        return clockLiveData
    }

    fun convertToGridItem(list:List<Clock>):MutableList<GridItem>{
        var result = ArrayList<GridItem>()
        for(item in list){
            result.add(item.toGridItem())
        }
        return result.toMutableList()
    }
    fun setMode(type:Int){
        this.mode.value = type
    }
    fun refreshClocks() {
        clockRepository.getClocks(mode.value!!, clockLiveData)
    }

    fun saveClickPosition(position:Int){
        when(this.mode.value){
            Clock.FACE -> prefManager!!.facePosition = position
            Clock.DIAL -> prefManager!!.dialPosition = position
            Clock.HAND -> prefManager!!.handPosition = position
        }
    }

    fun makeAllUnselect(recyclerAdapter: ItemAdapter?) {
        currentFacePosition.value = prefManager!!.facePosition
        currentDialPosition.value = prefManager!!.dialPosition
        currentHandPosition.value = prefManager!!.handPosition
        when(this.mode.value){
            Clock.FACE -> recyclerAdapter!!.makeAllUnselect(currentFacePosition.value!!)
            Clock.DIAL -> recyclerAdapter!!.makeAllUnselect(currentDialPosition.value!!)
            Clock.HAND -> recyclerAdapter!!.makeAllUnselect(currentHandPosition.value!!)
        }
    }

    fun getSelectedFaceImage():Bitmap{
        return if(faces.size != 0) {
            (ContextCompat.getDrawable(getApplication(), if (whiteBackgroundCheck.value!!) (faces!![currentFacePosition.value!!].imageWhite!!) else (faces!![currentFacePosition.value!!].image)) as BitmapDrawable).bitmap
        }else{
            Global.toBitmap(R.drawable.transparent_512)
        }
    }
    fun generateReducedBitmaps(){
        if(!generated) {
            reducedBitmaps.value = HashMap<Int, Bitmap>()
            reducedBitmaps.value!!.clear()
            var tempList = allClocksLiveData.value
            for (clock in tempList!!) {
                reducedBitmaps.value!![clock.image!!] = reduceImageAsBitmap(clock.image!!)
            }
            generated = true
        }
    }
    fun reduceImageAsBitmap(resource:Int):Bitmap{
        var bitmap = (ContextCompat.getDrawable(getApplication(), resource) as BitmapDrawable).bitmap
        return Bitmap.createScaledBitmap(bitmap, bitmap.width/5, bitmap.height/5, true)
    }

    fun filterFaces() {
        for(clock in this.allClocksLiveData.value!!){
            if(clock.type == Clock.FACE){
                faces.add(clock)
            }
        }
    }

    fun updateUI() {

//        (ContextCompat.getDrawable(getApplication(), if (whiteBackgroundCheck.value!!) (faces!![currentFacePosition.value!!].imageWhite!!) else (faces!![currentFacePosition.value!!].image)) as BitmapDrawable).bitmap
    }
}