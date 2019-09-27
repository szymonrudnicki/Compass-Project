package com.github.szymonrudnicki.compassproject.utils

fun Float.formatDistance() = when {
    this < 1000 -> "%.2f".format(this) + "m"
    else -> "%.2f".format(this / 1000) + "km"
}