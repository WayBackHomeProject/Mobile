package com.ssafy.waybackhome.util

fun Double.formatMeter() : String {
    return if(this > 1_000) "%.1f km".format(this/1_000) else "%.1f m".format(this)
}