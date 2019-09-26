package com.github.szymonrudnicki.compassproject.ui.compass.states

sealed class InputState {
    object Valid : InputState()
    object Invalid : InputState()
}