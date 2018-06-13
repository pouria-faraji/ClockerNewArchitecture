package com.blacksite.clockernewarchitecture.view

import android.appwidget.AppWidgetManager
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.blacksite.clockernewarchitecture.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.AnalogClock
import android.widget.CompoundButton
import android.widget.RemoteViews
import com.blacksite.clockernewarchitecture.MainObserver
import com.blacksite.clockernewarchitecture.adapter.ItemAdapter
import com.blacksite.clockernewarchitecture.application.App
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.Settings
import com.blacksite.clockernewarchitecture.customView.HandColorDialog
import com.blacksite.clockernewarchitecture.customView.MessageDialog
import com.blacksite.clockernewarchitecture.databinding.ActivityMainBinding
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.viewModel.ContentMainViewModel
import com.blacksite.clockernewarchitecture.viewModel.MainViewModel
import com.blacksite.clockernewarchitecture.widget.MyWidgetProvider
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.hand_color_dialog.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LifecycleOwner {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    var recyclerAdapter: ItemAdapter? = null
    var remoteViews: RemoteViews? = null
    var thisWidget: ComponentName? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    lateinit var snackbar:Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics instance.
        setup()
        prepareDrawer()
        prepareRecylcer()
        prepareObservers()
    }

    private fun prepareRecylcer(){
        main_grid_recycler.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this, Settings.NUMBER_OF_ITEMS_EACH_ROW)
        main_grid_recycler.layoutManager = layoutManager
    }
    private fun prepareObservers(){
        viewModel.mode.observe(this, Observer {
            viewModel.refreshClocks()
        })
        viewModel.allClocksLiveData.observe(this, Observer {
//            if(!viewModel.uiUpdated) {
            if(it!!.isNotEmpty()){
                loading_layout.visibility = View.GONE
                main_grid_recycler.visibility = View.VISIBLE
            }
            viewModel.generateReducedBitmaps()
            viewModel.filterComponents()
            viewModel.refreshClocks()
            clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())
            clock_dial_imageview.setImageBitmap(viewModel.getSelectedDialImage())
            clock_face_imageview.colorFilter = viewModel.getSelectedFaceColor()
            clock_dial_imageview.setColorFilter(viewModel.getSelectedDialColor(),PorterDuff.Mode.MULTIPLY)
            clock_dial_imageview.visibility = viewModel.getDialBackgroundVisibility()
            createHand(viewModel.currentHandPosition.value, viewModel.prefManager.colorCode)

//                if(it!!.isNotEmpty()) {
//                    viewModel.uiUpdated = true
//                }
//            }
        })
        viewModel.loadClocksLiveData().observe(this, Observer { list ->
            recyclerAdapter = ItemAdapter(this, list!!, viewModel)
            main_grid_recycler.adapter = recyclerAdapter
            viewModel.makeAllUnselect(recyclerAdapter)
            recyclerAdapter!!.onItemClick = {position ->
//                recyclerAdapter!!.makeAllUnselect(position)
                recyclerAdapter!!.notifyDataSetChanged()
                viewModel.saveClickPosition(position)
                viewModel.makeAllUnselect(recyclerAdapter)
            }
//            Toast.makeText(this, list!!.size.toString(), Toast.LENGTH_LONG).show()
        })
        viewModel.bitmapsGenerated.observe(this, Observer {
            if(recyclerAdapter!= null) {
//                recyclerAdapter!!.notifyDataSetChanged()
            }
        })
        viewModel.currentFacePosition.observe(this, Observer {
            clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())
//            if(viewModel.faces.size != 0) {
//                Toast.makeText(this, viewModel.faces!![viewModel.currentFacePosition.value!!].uid.toString(), Toast.LENGTH_LONG).show()
//            }

        })
        viewModel.currentDialPosition.observe(this, Observer {
            clock_dial_imageview.setImageBitmap(viewModel.getSelectedDialImage())
        })
        viewModel.whiteBackgroundCheck.observe(this, Observer {
            clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())
            clock_face_imageview.colorFilter = viewModel.getSelectedFaceColor()
        })
        viewModel.faceCheck.observe(this, Observer {
            if(it!!){
                clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())
                clock_face_imageview.colorFilter = viewModel.getSelectedFaceColor()
            }else{
                clock_face_imageview.setImageBitmap(Global.toBitmap(R.drawable.transparent_512))
            }
        })
        viewModel.dialBackgroundCheck.observe(this, Observer {
            if(it!!){
                clock_dial_imageview.visibility = View.VISIBLE
            }else{
                clock_dial_imageview.visibility = View.GONE
            }
        })
        viewModel.mainPanelClicked.observe(this, Observer {
            if(it!!){
                main_grid_recycler.visibility = View.VISIBLE
                color_btn_layout.visibility = View.GONE
                premium_layout.visibility = View.GONE
            }
//            else{
//                main_grid_recycler.visibility = View.GONE
//            }
        })
        viewModel.colorPanelClicked.observe(this, Observer {
            if(it!!){
                color_btn_layout.visibility = View.VISIBLE
                main_grid_recycler.visibility = View.GONE
                premium_layout.visibility = View.GONE
            }
//            else{
//                main_grid_recycler.visibility = View.VISIBLE
//                color_btn_layout.visibility = View.GONE
//            }

        })
        viewModel.premiumPanelClicked.observe(this, Observer {
            if(it!!){
                premium_layout.visibility = View.VISIBLE
                main_grid_recycler.visibility = View.GONE
                color_btn_layout.visibility = View.GONE
            }
//            else{
//                premium_layout.visibility = View.GONE
//            }
        })
        viewModel.fetchedNetwork.observe(this, Observer {
            if(it!!){
                if(snackbar.isShown){
                    snackbar.dismiss()
                    loading_gif.visibility = View.GONE
                    error_image.visibility = View.VISIBLE
                }
            }else{
                snackbar.show()
            }
        })
        viewModel.message.observe(this, Observer {
            if(!it.equals(Settings.NO_ERROR)){
                MessageDialog(this, it!!).show()
            }
        })
        viewModel.faceLock.observe(this, Observer {
            unlock_face_btn.isClickable = it!!
        })
    }

    private fun createHand(value: Int?, colorCode: Int) {
        if(viewModel.hands.size != 0) {
            var resourceName = "hand_" + viewModel.hands!![value!!].number + "_" + Global.getColorNameByCode(colorCode)
            var resourceID = this.resources.getIdentifier(resourceName, "id", this.packageName)
            var analogClock = this.findViewById<AnalogClock>(resourceID)
            for (i in viewModel.hands.indices) {
                for (j in 1..4) {
                    var resourceNameTemp = "hand_" + viewModel.hands[i].number + "_" + Global.getColorNameByCode(j)
                    var resourceIDTemp = this.resources.getIdentifier(resourceNameTemp, "id", this.packageName)
                    var temp = this.findViewById<AnalogClock>(resourceIDTemp)
                    temp.visibility = View.GONE
                }
            }
            analogClock.visibility = View.VISIBLE
        }
    }

    private fun prepareDrawer(){
        val toggle = ActionBarDrawerToggle(
                this, activityMainBinding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        activityMainBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        activityMainBinding.navView.setNavigationItemSelectedListener(this)

    }
    private fun setup(){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
//        mAuth = FirebaseAuth.getInstance()
//        mUser = mAuth!!.currentUser
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.setLifecycleOwner(this)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        lifecycle.addObserver(MainObserver(viewModel))
        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.app_name)

        snackbar = Snackbar.make(fab, "Loading...", Snackbar.LENGTH_INDEFINITE)


        var contentMainViewModel = ContentMainViewModel(application)
        activityMainBinding.appBarMainInclude.contentMainInclude.contentMainVM = contentMainViewModel

        face_color_btn.setOnClickListener(showFaceColorClickListener)
        dial_color_btn.setOnClickListener(showDialColorClickListener)
        hand_color_btn.setOnClickListener(showHandColorClickListener)
        white_background_switch.setOnCheckedChangeListener(whiteCheckChangeListener)
        dial_background_switch.setOnCheckedChangeListener(dialCheckChangeListener)
        face_switch.setOnCheckedChangeListener(faceCheckChangeListener)
        fab.setOnClickListener(fabClickListener)
        unlock_face_btn.setOnClickListener(unlockFaceClickListener)
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_face -> {
                viewModel.setMode(Clock.FACE)
            }
            R.id.nav_dial -> {
                viewModel.setMode(Clock.DIAL)
            }
            R.id.nav_hand -> {
                viewModel.setMode(Clock.HAND)
            }
            R.id.nav_color -> {
                viewModel.colorPanelClicked.value = true
            }
            R.id.nav_pro -> {
                viewModel.premiumPanelClicked.value = true
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    var unlockFaceClickListener = View.OnClickListener {
        viewModel.prefManager.faceLock = false
        viewModel.faceLock.value = false
        MessageDialog(this, "All faces have been unlocked").show()
    }
    var showHandColorClickListener = View.OnClickListener { view ->
        showHandColorDialog()
    }
    val showDialColorClickListener = View.OnClickListener { view ->
        showDialColorDialog()
    }
    val showFaceColorClickListener = View.OnClickListener { view ->
        showFaceColorDialog()
    }
    val whiteCheckChangeListener = CompoundButton.OnCheckedChangeListener{
        buttonView, isChecked ->
        if(isChecked){
            viewModel.prefManager.whiteBackgroundCheck = true
            viewModel.whiteBackgroundCheck.value = true
        }
        else{
            viewModel.prefManager.whiteBackgroundCheck = false
            viewModel.whiteBackgroundCheck.value = false
        }
    }
    val dialCheckChangeListener = CompoundButton.OnCheckedChangeListener{
        buttonView, isChecked ->
        if(isChecked){
            viewModel.prefManager!!.dialBackgroundCheck = true
            viewModel.dialBackgroundCheck.value = true
        }else{
            viewModel.prefManager!!.dialBackgroundCheck = false
            viewModel.dialBackgroundCheck.value = false
        }
    }
    val faceCheckChangeListener = CompoundButton.OnCheckedChangeListener{
        buttonView, isChecked ->
        if(isChecked){
            viewModel.prefManager!!.faceCheck = true
            viewModel.faceCheck.value = true
        }else{
            viewModel.prefManager!!.faceCheck = false
            viewModel.faceCheck.value = false
        }
    }
    val fabClickListener = View.OnClickListener{
        updateWidget()
    }

    private fun showHandColorDialog(){
        var dialog = HandColorDialog(this)
        dialog.show()
        dialog.hand_color_dialog_ok_button.setOnClickListener {
            viewModel.prefManager!!.colorCode = dialog.selectedColorCode
            createHand(viewModel.currentHandPosition.value, viewModel.prefManager!!.colorCode)
//            hand.makeAllGone(this, viewModel.hands[currentHandPosition!!].number!!, prefManager!!.colorCode)
            dialog.dismiss()
        }
    }

    fun showDialColorDialog(){
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose Color")
                .initialColor(Color.parseColor(viewModel.prefManager!!.dialColor))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorChangedListener { selectedColor ->

                }
                .setOnColorSelectedListener { selectedColor ->

                }
                .setPositiveButton("Ok", ColorPickerClickListener(){
                    dialog, selectedColor, allColors ->
                    viewModel.prefManager!!.dialColor = "#" + Integer.toHexString(selectedColor)
//                    prefManager!!.dialColorDialog = "#" + Integer.toHexString(selectedColor)
//                    clock_face_imageview.colorFilter = LightingColorFilter(Color.WHITE, Color.parseColor("#" + Integer.toHexString(selectedColor)))
                    clock_dial_imageview.setColorFilter(viewModel.getSelectedDialColor(), PorterDuff.Mode.MULTIPLY)

                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener(){
                    dialog, which ->

                })
                .showAlphaSlider(false)
                .showColorEdit(true)
                .setColorEditTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright))
                .build()
                .show()
    }
    fun showFaceColorDialog(){
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose Color")
                .initialColor(Color.parseColor(viewModel.prefManager!!.faceColorDialog))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorChangedListener { selectedColor ->

                }
                .setOnColorSelectedListener { selectedColor ->

                }
                .setPositiveButton("Ok", ColorPickerClickListener(){
                    dialog, selectedColor, allColors ->
                    viewModel.prefManager!!.faceColor = "#" + Integer.toHexString(selectedColor)
                    viewModel.prefManager!!.faceColorDialog = "#" + Integer.toHexString(selectedColor)
                    clock_face_imageview.colorFilter = viewModel.getSelectedFaceColor()
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener(){
                    dialog, which ->

                })
                .showAlphaSlider(false)
                .showColorEdit(true)
                .setColorEditTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright))
                .build()
                .show()
    }
    fun updateWidget(){
        remoteViews = RemoteViews(this.packageName, R.layout.widget)
        thisWidget = ComponentName(this, MyWidgetProvider::class.java)
        if(!viewModel.premiumItem || !viewModel.prefManager.faceLock) {
            viewModel.logToFireBase(mFirebaseAnalytics!!)
            clock_canvas.destroyDrawingCache()
            clock_canvas.buildDrawingCache()
            if (clock_canvas.drawingCache != null) {
                viewModel.prefManager!!.cachedBitmap = Global.saveToInternalStorage(clock_canvas.drawingCache)
            }
            remoteViews!!.setImageViewBitmap(R.id.clock_face_imageview_widget, clock_canvas.drawingCache)
            MainViewModel.makeAllGoneWidget(this, viewModel.prefManager, remoteViews!!)

            val appWidgetManager = AppWidgetManager.getInstance(this)
            appWidgetManager.updateAppWidget(thisWidget, remoteViews)
            Snackbar.make(fab, "Your widget has been created.", Snackbar.LENGTH_LONG)
                    .setAction("Widget", null).show()
        }else{
            MessageDialog(this, "You have selected a premium item. You can have it all.").show()
            viewModel.premiumPanelClicked.value = true
        }
    }
}