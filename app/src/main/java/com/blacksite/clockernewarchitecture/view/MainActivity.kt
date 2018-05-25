package com.blacksite.clockernewarchitecture.view

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.blacksite.clockernewarchitecture.MainObserver
import com.blacksite.clockernewarchitecture.adapter.ItemAdapter
import com.blacksite.clockernewarchitecture.application.Settings
import com.blacksite.clockernewarchitecture.databinding.ActivityMainBinding
import com.blacksite.clockernewarchitecture.databinding.AppBarMainBinding
import com.blacksite.clockernewarchitecture.databinding.ContentMainBinding
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.viewModel.ContentMainViewModel
import com.blacksite.clockernewarchitecture.viewModel.MainViewModel
import kotlinx.android.synthetic.main.content_main.*


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
            viewModel.generateReducedBitmaps()
            viewModel.filterFaces()
            clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())

//            Toast.makeText(this, "allClockLivesData: " + it!!.size.toString(), Toast.LENGTH_SHORT).show()

        })
        viewModel.loadClocksLiveData().observe(this, Observer { list ->
//            Toast.makeText(this, "clockLiveData: " + list!!.size.toString(), Toast.LENGTH_SHORT).show()
            viewModel.updateUI()
//            viewModel.generateReducedBitmaps()
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
        viewModel.whiteBackgroundCheck.observe(this, Observer {
            clock_face_imageview.setImageBitmap(viewModel.getSelectedFaceImage())
        })
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
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
