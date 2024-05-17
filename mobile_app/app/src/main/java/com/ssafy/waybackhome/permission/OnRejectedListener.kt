package com.ssafy.waybackhome.permission

fun interface OnRejectedListener {
    fun onRejected(result : Map<String, Boolean>)
}