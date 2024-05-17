package com.ssafy.waybackhome.permission

fun interface OnGrantedListener {
    fun onGranted(result : Map<String, Boolean>)
}