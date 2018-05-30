package com.blacksite.clockernewarchitecture.customView

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.blacksite.clockernewarchitecture.R
import kotlinx.android.synthetic.main.message_dialog.*

/**
 * Created by p.faraji on 4/26/2018.
 */
class MessageDialog(context: Activity?, message:String) : Dialog(context) {
    var _context = context
    var _message = message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.message_dialog)
        message_dialog_message.text = _message
        message_dialog_ok_button.setOnClickListener {
            dismiss()
        }
    }

}