package com.github.szymonrudnicki.compassproject.ui.compass.states

sealed class DestinationAzimuthState {
    data class Available(val value: Float) : DestinationAzimuthState()
    object Unavailable : DestinationAzimuthState()
}