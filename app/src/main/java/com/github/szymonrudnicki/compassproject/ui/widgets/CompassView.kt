package com.github.szymonrudnicki.compassproject.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.szymonrudnicki.compassproject.R
import com.github.szymonrudnicki.compassproject.extensions.gone
import com.github.szymonrudnicki.compassproject.extensions.visible
import com.github.szymonrudnicki.compassproject.ui.compass.states.DestinationState
import kotlinx.android.synthetic.main.layout_compass.view.*

private const val ROTATE_ANIMATION_DURATION = 250L

class CompassView(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    private var _compassDegree = 0.0

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_compass, this, true)
    }

    fun rotateCompass(azimuth: Double?) {
        // clockwise rotation will basically reduce the degree on the compass image
        // that is why azimuth is not equal to animation degree and has to be negated
        val newDegree = -azimuth!!
        val rotateAnimation = RotateAnimation(
            _compassDegree.toFloat(), newDegree.toFloat(),
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            fillAfter = true
            duration = ROTATE_ANIMATION_DURATION
        }
        rotating_container.startAnimation(rotateAnimation)
        _compassDegree = newDegree

        degrees_text_view.text = "${"%.2f".format(azimuth)}°"
    }

    fun setDestinationAzimuth(state: DestinationState?) {
        when (state) {
            is DestinationState.Available -> {
                with(destination_azimuth_image_view) {
                    rotation = state.azimuth
                    layoutParams = (layoutParams as LayoutParams).apply {
                        circleAngle = state.azimuth
                    }
                    visible()
                }
            }
            is DestinationState.Unavailable -> destination_azimuth_image_view.gone()
        }
    }
}