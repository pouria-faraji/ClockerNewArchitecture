package com.blacksite.clockernewarchitecture.viewModel

import android.app.Application
import android.arch.lifecycle.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.RemoteViews
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.adapter.ItemAdapter
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.PrefManager
import com.blacksite.clockernewarchitecture.model.GridItem
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.repository.ClockRepository
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle




class MainViewModel(application: Application) : AndroidViewModel(application) {
    var clockRepository:ClockRepository = ClockRepository(application)
    var clockLiveData:MutableLiveData<List<Clock>> = MutableLiveData<List<Clock>>()
    var allClocksLiveData:MutableLiveData<List<Clock>> = MutableLiveData<List<Clock>>()
    var mode:MutableLiveData<Int> = MutableLiveData() // 1 -> Face, 2 -> Dial, 3 -> Hand
    var prefManager: PrefManager = PrefManager(application)
    var reducedBitmaps = MutableLiveData<HashMap<String, Bitmap>>()
    var currentFacePosition = MutableLiveData<Int>()
    var currentDialPosition = MutableLiveData<Int>()
    var currentHandPosition = MutableLiveData<Int>()
    var whiteBackgroundCheck = MutableLiveData<Boolean>()
    var dialBackgroundCheck = MutableLiveData<Boolean>()
    var faces = ArrayList<Clock>()
    var dials = ArrayList<Clock>()
    var hands = ArrayList<Clock>()
    var generated = false
    var uiUpdated = false
    var colorPanelClicked = MutableLiveData<Boolean>()
    init {
        currentFacePosition.value = prefManager!!.facePosition
        currentDialPosition.value = prefManager!!.dialPosition
        currentHandPosition.value = prefManager!!.handPosition
        mode.value = Clock.FACE
        colorPanelClicked.value = false
        clockRepository.getClocks(mode.value!!, clockLiveData)
        clockRepository.getAllClocks(allClocksLiveData)
//        allClocksLiveData = clockRepository.getAllClocks()
//        clockRepository.getAllClocks(allClocksLiveData)
        whiteBackgroundCheck.value = prefManager!!.whiteBackgroundCheck
        dialBackgroundCheck.value = prefManager!!.dialBackgroundCheck
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
        this.colorPanelClicked.value = false
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
//            (ContextCompat.getDrawable(getApplication(), if (whiteBackgroundCheck.value!!) (faces!![currentFacePosition.value!!].imageWhite!!) else (faces!![currentFacePosition.value!!].image)) as BitmapDrawable).bitmap
            if(whiteBackgroundCheck.value!!){
                Global.loadImageFromStorage(Global.absolutePath!!, faces!![currentFacePosition.value!!].imageWhite!!)
            }else{
                Global.loadImageFromStorage(Global.absolutePath!!, faces!![currentFacePosition.value!!].image!!)
            }
        }else{
            Global.toBitmap(R.drawable.transparent_512)
        }
    }
    fun getSelectedDialImage():Bitmap{
        return if(dials.size != 0) {
//            (ContextCompat.getDrawable(getApplication(), dials!![currentDialPosition.value!!].image!!) as BitmapDrawable).bitmap
            Global.loadImageFromStorage(Global.absolutePath!!, dials!![currentDialPosition.value!!].image!!)
        }else{
            Global.toBitmap(R.drawable.transparent_512)
        }
    }
    fun getSelectedFaceColor():LightingColorFilter{
        return LightingColorFilter(Color.WHITE, Color.parseColor(prefManager!!.faceColor))
    }
    fun getSelectedDialColor():Int{
        return Color.parseColor(prefManager!!.dialColor)
    }
    fun generateReducedBitmaps(){
        if(!generated) {
            reducedBitmaps.value = HashMap<String, Bitmap>()
            reducedBitmaps.value!!.clear()
            var tempList = allClocksLiveData.value
            for (clock in tempList!!) {
                reducedBitmaps.value!![clock.image!!] = reduceImageAsBitmap(clock.image!!)
            }
            generated = true
        }
    }
    fun reduceImageAsBitmap(fileName:String):Bitmap{
        var bitmap = Global.loadImageFromStorage(Global.absolutePath!!, fileName)
//        var bitmap = (ContextCompat.getDrawable(getApplication(), resource) as BitmapDrawable).bitmap
        return Bitmap.createScaledBitmap(bitmap, bitmap.width/5, bitmap.height/5, true)
    }

    fun filterComponents() {
        for(clock in this.allClocksLiveData.value!!){
            if(clock.type == Clock.FACE){
                faces.add(clock)
            }
            if(clock.type == Clock.DIAL){
                dials.add(clock)
            }
            if(clock.type == Clock.HAND){
                hands.add(clock)
            }
        }
        prefManager.handsList = hands
    }

    fun updateUI() {

//        (ContextCompat.getDrawable(getApplication(), if (whiteBackgroundCheck.value!!) (faces!![currentFacePosition.value!!].imageWhite!!) else (faces!![currentFacePosition.value!!].image)) as BitmapDrawable).bitmap
    }

    fun getDialBackgroundVisibility(): Int {
        return if(prefManager!!.dialBackgroundCheck){
            View.VISIBLE
        }else{
            View.GONE
        }
    }

    fun logToFireBase(mFirebaseAnalytics: FirebaseAnalytics) {
        val bundle = Bundle()
        bundle.putString("Face", faces[currentFacePosition.value!!].uid.toString())
        bundle.putString("Dial", dials[currentDialPosition.value!!].uid.toString())
        bundle.putString("Hand", hands[currentHandPosition.value!!].uid.toString())
        mFirebaseAnalytics.logEvent("WIDGET_CREATED", bundle)
    }

    companion object {
        fun makeAllGoneWidget(context: Context,prefManager: PrefManager, views: RemoteViews) {
            var resourceName = "hand_" + prefManager.handsList[prefManager.handPosition].number + "_widget_" + Global.getColorNameByCode(prefManager.colorCode)
            var resourceID = context.resources.getIdentifier(resourceName, "id", context.packageName)
            for(hand in prefManager.handsList){
                for(i in 1..4){
                    var resourceNameTemp = "hand_" + hand.number + "_widget_" + Global.getColorNameByCode(i)
                    var resourceIDTemp = context.resources.getIdentifier(resourceNameTemp, "id", context.packageName)
                    views.setViewVisibility(resourceIDTemp, View.GONE)
                }
//            remoteViews.setViewVisibility(hand.analogClockWidget!!, View.GONE)
            }
            views.setViewVisibility(resourceID, View.VISIBLE)
        }
    }

}