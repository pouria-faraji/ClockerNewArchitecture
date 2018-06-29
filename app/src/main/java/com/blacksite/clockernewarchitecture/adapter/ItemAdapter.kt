package com.blacksite.clockernewarchitecture.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RotateDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.Settings
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.viewModel.MainViewModel

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    var context: Context? = null
    var itemsList:MutableList<Clock>? = null
    var hashMapSelected: HashMap<Int, Boolean>? = null
    var mainViewModel:MainViewModel

    constructor(context: Context?, itemsList: List<Clock>?, viewModel: MainViewModel) : super() {
        this.context = context
        this.mainViewModel = viewModel
        this.itemsList = itemsList as MutableList<Clock>?
        hashMapSelected = HashMap()
        for (i in 0 until itemsList!!.size) {
            hashMapSelected!![i] = false
        }
    }
    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout){

        var imageView: ImageView = layout.findViewById(R.id.imgItem)
        var lockLayout: RelativeLayout = layout.findViewById(R.id.lockLayout)
        var lockImageView: ImageView = layout.findViewById(R.id.imgLock)
        var premiumLockImageView: ImageView = layout.findViewById(R.id.premiumImgLock)
        var premiumL: RelativeLayout = layout.findViewById(R.id.premiumL)
    }
    var onItemClick: (Int) -> Unit = {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item, parent, false)
        var viewHolder = ViewHolder(v)
        v.setOnClickListener {
            onItemClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = this.itemsList!![position]
//        holder.imageView.setImageResource(item.image!!)
        holder.imageView.setImageBitmap(if(mainViewModel.reducedBitmaps.value != null) (mainViewModel.reducedBitmaps.value!![item.image!!])else(Global.toBitmap(R.drawable.transparent_512)))
        var size = ((0.7) * Global.getAppWidth()/Settings.NUMBER_OF_ITEMS_EACH_ROW).toInt()
        var lockLayoutSize = ((0.25)*Global.getAppWidth()/Settings.NUMBER_OF_ITEMS_EACH_ROW).toInt()
        var lockImageSize = ((0.15)*Global.getAppWidth()/Settings.NUMBER_OF_ITEMS_EACH_ROW).toInt()
        holder.imageView.layoutParams.width = size
        holder.imageView.layoutParams.height = size
        holder.lockLayout.layoutParams.height = lockLayoutSize
        holder.lockImageView.layoutParams.height = lockImageSize
        holder.premiumLockImageView.layoutParams.height = lockImageSize
        holder.premiumLockImageView.layoutParams.width = lockImageSize
        var premiumItemBackground: LayerDrawable = ContextCompat.getDrawable(context!!, R.drawable.premium_item_background)!!.mutate() as LayerDrawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            premiumItemBackground.setLayerHeight(0, (holder.imageView.layoutParams.width*(0.5)).toInt())
            premiumItemBackground.setLayerInsetTop(0, (holder.imageView.layoutParams.width*(1.3)).toInt())
            premiumItemBackground.setLayerInsetRight(0, holder.imageView.layoutParams.width * (-2))
        }

        holder.premiumL.layoutParams.width = (holder.imageView.layoutParams.width/1.0).toInt()
        holder.premiumL.layoutParams.height = (holder.imageView.layoutParams.height/1.0).toInt()
        holder.premiumL.background = premiumItemBackground

        holder.premiumLockImageView.translationX = (holder.premiumL.layoutParams.width * 0.61).toFloat()
        holder.premiumLockImageView.translationY = (holder.premiumL.layoutParams.height * 0.61).toFloat()



//        val params1 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Global.getAppWidth()/ Settings.NUMBER_OF_ITEMS_EACH_ROW)
        val params1 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        var margin = Global.dp_to_px(2)
        params1.setMargins(margin,margin,margin,margin)
//        holder.layout.layoutParams = params1
        if(hashMapSelected!![position] == true){
            holder.layout.background = ContextCompat.getDrawable(context!!, R.drawable.item_background_selected)
            when(item.type){
                Clock.FACE -> mainViewModel.premiumFace = item.premium
                Clock.DIAL -> mainViewModel.premiumDial = item.premium
            }
        }else{
            holder.layout.background = ContextCompat.getDrawable(context!!, R.drawable.item_background)
        }

        if(!item.premium || (!mainViewModel.prefManager.faceLock && item.type == Clock.FACE) || (!mainViewModel.prefManager.dialLock && item.type == Clock.DIAL)){
            holder.premiumL.visibility = View.GONE
            holder.lockLayout.visibility = View.GONE
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.premiumL.visibility = View.VISIBLE
                holder.lockLayout.visibility = View.GONE
            }else{
                holder.lockLayout.visibility = View.VISIBLE
                holder.premiumL.visibility = View.GONE
            }
        }
//        if(mainViewModel.prefManager.faceLock && item.type == Clock.FACE) {
//            if (!item.premium) {
//                holder.lockLayout.visibility = View.GONE
//            } else {
//                holder.lockLayout.visibility = View.VISIBLE
//            }
//        }else if(mainViewModel.prefManager.dialLock && item.type == Clock.DIAL){
//            if (!item.premium) {
//                holder.lockLayout.visibility = View.GONE
//            } else {
//                holder.lockLayout.visibility = View.VISIBLE
//            }
//        }else{
//            holder.lockLayout.visibility = View.GONE
//        }
    }
    override fun getItemCount(): Int {
        return itemsList!!.size
    }
    fun makeAllUnselect(position: Int) {
        hashMapSelected!!.put(position, true)
        for (i in 0 until hashMapSelected!!.size) {
            if (i != position)
                hashMapSelected!![i] = false
        }
    }
}