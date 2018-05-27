package com.blacksite.clockernewarchitecture.customView

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.PrefManager
import com.blacksite.clockernewarchitecture.application.Settings
import kotlinx.android.synthetic.main.hand_color_dialog.*

/**
 * Created by p.faraji on 4/26/2018.
 */
class HandColorDialog(context: Activity?) : Dialog(context) {
    var prefManager = PrefManager(context!!)
    var selectedColorCode:Int = prefManager!!.colorCode
    var _context = context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.hand_color_dialog)
        makeAllUnselect(selectedColorCode)
        hand_color_dialog_cancel_button.setOnClickListener {
            dismiss()
        }
        hand_color_grey.setOnClickListener{
            selectedColorCode = Settings.COLOR_CODE_GREY
            makeAllUnselect(selectedColorCode)
        }
        hand_color_blue.setOnClickListener{
            selectedColorCode = Settings.COLOR_CODE_BLUE
            makeAllUnselect(selectedColorCode)
        }
        hand_color_red.setOnClickListener{
            selectedColorCode = Settings.COLOR_CODE_RED
            makeAllUnselect(selectedColorCode)
        }
        hand_color_green.setOnClickListener{
            selectedColorCode = Settings.COLOR_CODE_GREEN
            makeAllUnselect(selectedColorCode)
        }

        hand_color_dialog_ok_button.setOnClickListener {

        }
    }
    fun makeAllUnselect(colorCode:Int){
        var resourceName = "hand_color_" + Global.getColorNameByCode(colorCode)
        var resourceID = _context!!.resources.getIdentifier(resourceName, "id", _context!!.packageName)
        var view = this.findViewById<View>(resourceID)
        for(i in 1..4){
            var resourceNameTemp = "hand_color_" + Global.getColorNameByCode(i)
            var resourceIDTemp = _context!!.resources.getIdentifier(resourceNameTemp, "id", _context!!.packageName)
            var viewTemp = this.findViewById<View>(resourceIDTemp)
            viewTemp.setBackgroundResource(Global.getCircleResourceByColorCode(i))
        }
        view.setBackgroundResource(Global.getSelectedCircleResourceByColorCode(colorCode))
    }
}