package com.blacksite.clockernewarchitecture.application

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.RelativeLayout

@BindingAdapter("android:layout_width")
fun setImageWidth(imageView: ImageView,width:Float?) {
    imageView.layoutParams.width = width!!.toInt()
//    imageView.layoutParams.width = Global.getAppWidth()/5
}

@BindingAdapter("android:layout_height")
fun setImageHeight(imageView: ImageView,height:Float?) {
    imageView.layoutParams.height = height!!.toInt()
}
@BindingAdapter("app:imagePadding")
fun setImagePadding(imageView: ImageView,padding:Float?) {
    imageView.setPadding(padding!!.toInt(),padding!!.toInt(),padding!!.toInt(),padding!!.toInt())
}

@BindingAdapter("android:layout_height")
fun setRelativeLayoutHeight(layout: RelativeLayout,height:Float?) {
    layout.layoutParams.height = height!!.toInt()
}

@BindingAdapter("app:setBitmap")
fun setImageByBitmap(imageview: ImageView, bitmap: Bitmap?) {
    imageview.setImageBitmap(bitmap)
}