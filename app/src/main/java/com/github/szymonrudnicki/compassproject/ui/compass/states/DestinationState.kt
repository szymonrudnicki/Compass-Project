package com.github.szymonrudnicki.compassproject.ui.compass.states

sealed class DestinationState {
    data class Available(val azimuth: Float, val formattedDistance: String) : DestinationState()
    object Unavailable : DestinationState()
}