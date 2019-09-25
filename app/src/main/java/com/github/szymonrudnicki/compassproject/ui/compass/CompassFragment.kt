package com.github.szymonrudnicki.compassproject.ui.compass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.szymonrudnicki.compassproject.R
import com.github.szymonrudnicki.compassproject.extensions.observe
import kotlinx.android.synthetic.main.fragment_compass.*

private const val ROTATE_ANIMATION_DURATION = 250L

class CompassFragment : Fragment() {

    private var _compassDegree = 0f

    companion object {
        fun newInstance() = CompassFragment()
    }

    private val _compassViewModel: CompassViewModel by lazy {
        ViewModelProviders.of(this).get(CompassViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_compass, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _compassViewModel.getCompassAzimuth(context!!)
        observe(_compassViewModel.azimuthLiveData, ::rotateCompass)
    }

    private fun rotateCompass(azimuth: Float?) {
        // clockwise rotation will basically reduce the degree on the compass image
        // that is why azimuth is not equal to degree and has to be negated
        val newDegree = -azimuth!!
        val rotateAnimation = RotateAnimation(_compassDegree, newDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            fillAfter = true
            duration = ROTATE_ANIMATION_DURATION
        }

        compass_image_view.startAnimation(rotateAnimation)
        _compassDegree = newDegree
    }
}
