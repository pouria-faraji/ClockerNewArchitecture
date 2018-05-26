package com.blacksite.clockernewarchitecture.view

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.blacksite.clocker.view.HandColorDialog
import com.blacksite.clockernewarchitecture.MainObserver
import com.blacksite.clockernewarchitecture.adapter.ItemAdapter
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.Settings
import com.blacksite.clockernewarchitecture.databinding.ActivityMainBinding
import com.blacksite.clockernewarchitecture.databinding.AppBarMainBinding
import com.blacksite.clockernewarchitecture.databinding.ContentMainBinding
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.viewModel.ContentMainViewModel
import com.blacksite.clockernewarchitecture.viewModel.MainViewModel
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.hand_color_dialog.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LifecycleOwner {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    var recyclerAdapter: ItemAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        prepareDrawer()
        prepareRecylcer()
        prepareObservers()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
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
            if(!viewModel.uiUpdated) {
                viewModel.generateReducedBitmaps()
                viewModel.filterComponents()
                clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())
                clock_dial_imageview.setImageBitmap(viewModel.getSelectedDialImage())
                clock_face_imageview.colorFilter = viewModel.getSelectedFaceColor()
                clock_dial_imageview.setColorFilter(viewModel.getSelectedDialColor(),PorterDuff.Mode.MULTIPLY)
                clock_dial_imageview.visibility = viewModel.getDialBackgroundVisibility()
                createHand(viewModel.currentHandPosition.value, viewModel.prefManager.colorCode)

                viewModel.uiUpdated = true
            }
        })
        viewModel.loadClocksLiveData().observe(this, Observer { list ->
            viewModel.updateUI()
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
        viewModel.reducedBitmaps.observe(this, Observer {
            recyclerAdapter!!.notifyDataSetChanged()
        })
        viewModel.currentFacePosition.observe(this, Observer {
            clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())
        })
        viewModel.currentDialPosition.observe(this, Observer {
            clock_dial_imageview.setImageBitmap(viewModel.getSelectedDialImage())
        })
        viewModel.whiteBackgroundCheck.observe(this, Observer {
            clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())
            clock_face_imageview.colorFilter = viewModel.getSelectedFaceColor()
        })
        viewModel.dialBackgroundCheck.observe(this, Observer {
            if(it!!){
                clock_dial_imageview.visibility = View.VISIBLE
            }else{
                clock_dial_imageview.visibility = View.GONE
            }
        })
        viewModel.colorPanelClicked.observe(this, Observer {
            if(it!!){
                main_grid_recycler.visibility = View.GONE
                color_btn_layout.visibility = View.VISIBLE
            }else{
                main_grid_recycler.visibility = View.VISIBLE
                color_btn_layout.visibility = View.GONE
            }

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
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.setLifecycleOwner(this)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        lifecycle.addObserver(MainObserver(viewModel))
        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.app_name)


        var contentMainViewModel = ContentMainViewModel(application)
        activityMainBinding.appBarMainInclude.contentMainInclude.contentMainVM = contentMainViewModel

        face_color_btn.setOnClickListener(showFaceColorClickListener)
        dial_color_btn.setOnClickListener(showDialColorClickListener)
        hand_color_btn.setOnClickListener(showHandColorClickListener)
        white_background_switch.setOnCheckedChangeListener(whiteCheckChangeListener)
        dial_background_switch.setOnCheckedChangeListener(dialCheckChangeListener)
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
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
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

    private fun showHandColorDialog(){
        var currentHandPosition = viewModel.currentHandPosition.value
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
}
