package com.blacksite.clockernewarchitecture.viewModel

import android.app.Application
import android.arch.lifecycle.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.view.View
import android.widget.RemoteViews
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.adapter.ItemAdapter
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.PrefManager
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.repository.ClockRepository
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle
import com.blacksite.clockernewarchitecture.application.Settings


class MainViewModel(application: Application) : AndroidViewModel(application) {
    var clockRepository:ClockRepository = ClockRepository(application)
    var clockLiveData:MutableLiveData<List<Clock>> = MutableLiveData<List<Clock>>()
    var allClocksLiveData:MutableLiveData<List<Clock>> = MutableLiveData<List<Clock>>()
    var mode:MutableLiveData<Int> = MutableLiveData() // 1 -> Face, 2 -> Dial, 3 -> Hand
    var prefManager: PrefManager = PrefManager(application)
    var reducedBitmaps = MutableLiveData<HashMap<String, Bitmap>>()
    var bitmapsGenerated = MutableLiveData<Boolean>()
    var fetchedNetwork = MutableLiveData<Boolean>()
    var currentFacePosition = MutableLiveData<Int>()
    var currentDialPosition = MutableLiveData<Int>()
    var currentHandPosition = MutableLiveData<Int>()
    var whiteBackgroundCheck = MutableLiveData<Boolean>()
    var dialBackgroundCheck = MutableLiveData<Boolean>()
    var faceCheck = MutableLiveData<Boolean>()
    var faceFilterCheck = MutableLiveData<Boolean>()
    var faces = ArrayList<Clock>()
    var dials = ArrayList<Clock>()
    var hands = ArrayList<Clock>()
    var generated = false
    var colorPanelClicked = MutableLiveData<Boolean>()
    var mainPanelClicked = MutableLiveData<Boolean>()
    var premiumPanelClicked = MutableLiveData<Boolean>()
    var message = MutableLiveData<String>()
    var faceLock = MutableLiveData<Boolean>()
    var dialLock = MutableLiveData<Boolean>()
    var colorLock = MutableLiveData<Boolean>()
    var featureLock = MutableLiveData<Boolean>()
    var premiumFace = false
    var premiumDial = false
    init {
        message.value = Settings.NO_ERROR
        currentFacePosition.value = prefManager!!.facePosition
        currentDialPosition.value = prefManager!!.dialPosition
        currentHandPosition.value = prefManager!!.handPosition
        mode.value = Clock.FACE
        clockRepository.getClocks(mode.value!!, clockLiveData)
        clockRepository.getAllClocks(allClocksLiveData, fetchedNetwork, message)
        whiteBackgroundCheck.value = prefManager!!.whiteBackgroundCheck
        dialBackgroundCheck.value = prefManager!!.dialBackgroundCheck
        faceCheck.value = prefManager!!.faceCheck
        faceFilterCheck.value = prefManager!!.faceFilterCheck
        bitmapsGenerated.value = false
        faceLock.value = prefManager.faceLock
        dialLock.value = prefManager.dialLock
        colorLock.value = prefManager.colorLock
        featureLock.value = prefManager.featureLock
    }
    fun loadClocksLiveData():MutableLiveData<List<Clock>>{
        return clockLiveData
    }

    fun setMode(type:Int){
        this.mainPanelClicked.value = true
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
        reducedBitmaps.value = HashMap<String, Bitmap>()
        reducedBitmaps.value!!.clear()
        var tempList = allClocksLiveData.value
        for (clock in tempList!!) {
            reducedBitmaps.value!![clock.image!!] = reduceImageAsBitmap(clock.image!!)
        }
        bitmapsGenerated.value = (bitmapsGenerated.value!!).xor(true)


    }
    fun reduceImageAsBitmap(fileName:String):Bitmap{
        var bitmap = Global.loadImageFromStorage(Global.absolutePath!!, fileName)
        return Bitmap.createScaledBitmap(bitmap, bitmap.width/3, bitmap.height/3, true)
    }

    fun filterComponents() {
        faces.clear()
        dials.clear()
        hands.clear()
        for(clock in this.allClocksLiveData.value!!){
            if(clock.type == Clock.FACE){
                Global.addUnique(faces, clock)
            }
            if(clock.type == Clock.DIAL){
                Global.addUnique(dials, clock)
            }
            if(clock.type == Clock.HAND){
                Global.addUnique(hands, clock)
            }
        }
        prefManager.handsList = hands
    }

    fun getDialBackgroundVisibility(): Int {
        return if(prefManager!!.dialBackgroundCheck){
            View.VISIBLE
        }else{
            View.GONE
        }
    }

    fun logToFireBase(mFirebaseAnalytics: FirebaseAnalytics) {
        if(!faces.isEmpty() && !dials.isEmpty() && !hands.isEmpty()) {
            val bundle = Bundle()
            bundle.putString("Face", faces[currentFacePosition.value!!].uid.toString())
            bundle.putString("Dial", dials[currentDialPosition.value!!].uid.toString())
            bundle.putString("Hand", hands[currentHandPosition.value!!].uid.toString())
            mFirebaseAnalytics.logEvent("WIDGET_CREATED", bundle)
        }
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
            }
            views.setViewVisibility(resourceID, View.VISIBLE)
        }
    }

}