package com.blacksite.clockernewarchitecture.adapter

import android.content.Context
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
//        this.itemsList = mainViewModel.convertToGridItem(itemsList!!)
        this.itemsList = itemsList as MutableList<Clock>?
        hashMapSelected = HashMap()
        for (i in 0 until itemsList!!.size) {
            hashMapSelected!!.put(i, false)
        }
    }
    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout){

        var imageView: ImageView = layout.findViewById(R.id.imgItem)
        var lockLayout: RelativeLayout = layout.findViewById(R.id.lockLayout)
        var lockImageView: ImageView = layout.findViewById(R.id.imgLock)
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
        var lockLayoutSize = ((0.2)*Global.getAppWidth()/Settings.NUMBER_OF_ITEMS_EACH_ROW).toInt()
        var lockImageSize = ((0.1)*Global.getAppWidth()/Settings.NUMBER_OF_ITEMS_EACH_ROW).toInt()
        holder.imageView.layoutParams.width = size
        holder.imageView.layoutParams.height = size
        holder.lockLayout.layoutParams.height = lockLayoutSize
        holder.lockImageView.layoutParams.height = lockImageSize
//        val params1 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Global.getAppWidth()/ Settings.NUMBER_OF_ITEMS_EACH_ROW)
        val params1 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        var margin = Global.dp_to_px(2)
        params1.setMargins(margin,margin,margin,margin)
//        holder.layout.layoutParams = params1
        if(hashMapSelected!![position] == true){
            holder.layout.background = ContextCompat.getDrawable(context!!, R.drawable.item_background_selected)
        }else{
            holder.layout.background = ContextCompat.getDrawable(context!!, R.drawable.item_background)
        }

        if(!item.premium){
            holder.lockLayout.visibility = View.GONE
        }else{
            holder.lockLayout.visibility = View.VISIBLE
        }
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