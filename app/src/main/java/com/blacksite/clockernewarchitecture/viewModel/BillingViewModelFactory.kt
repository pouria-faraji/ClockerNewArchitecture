package com.blacksite.clockernewarchitecture.viewModel

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.app.Application
import android.arch.lifecycle.ViewModel





class BillingViewModelFactory: ViewModelProvider.NewInstanceFactory{
    private var mActivity: Activity? = null
    private var mApplication: Application? = null
    private var mMainViewModel: MainViewModel? = null

    constructor(mApplication: Application, mActivity: Activity?, mMainViewModel: MainViewModel?) : super() {
        this.mActivity = mActivity
        this.mApplication = mApplication
        this.mMainViewModel = mMainViewModel
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BillingViewModel(mApplication!!, mActivity!!, mMainViewModel!!) as T
    }
}