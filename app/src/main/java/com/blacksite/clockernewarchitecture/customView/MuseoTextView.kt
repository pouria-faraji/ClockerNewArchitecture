package com.blacksite.clocker.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by p.faraji on 4/16/2018.
 */
class MuseoTextView : TextView {

    constructor(context: Context) : super(context) {
        this.typeface = Typeface.createFromAsset(context.assets, "fonts/Museo Sans W01 Rounded 300.ttf")
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = Typeface.createFromAsset(context.assets, "fonts/Museo Sans W01 Rounded 300.ttf")
    }
}