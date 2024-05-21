package com.ssafy.waybackhome.util

import android.view.View

fun interface OnItemOptionsClickListener<T> {
    fun onClick(item : T, anchor : View)
}