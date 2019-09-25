package com.github.szymonrudnicki.compassproject.ui.compass

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.szymonrudnicki.compassproject.R
import com.github.szymonrudnicki.compassproject.extensions.observe
import com.github.szymonrudnicki.compassproject.ui.validators.LatitudeValidator
import com.github.szymonrudnicki.compassproject.ui.validators.LongitudeValidator
import kotlinx.android.synthetic.main.fragment_compass.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


private const val ROTATE_ANIMATION_DURATION = 250L
private const val LOCATION_PERMISSION_REQUEST = 567

class CompassFragment : Fragment() {

    private var _compassDegree = 0f
    private var _wasAskedForPermissions = false

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

        set_destination_button.setOnClickListener {
            validateCoordinateInputs()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST)
    private fun validateCoordinateInputs() {
        val latitudeIsValid = latitude_text_view.validateWith(
                LatitudeValidator(context!!.getString(R.string.latitude_error_message)))
        val longitudeIsValid = longitude_text_view.validateWith(
                LongitudeValidator(context!!.getString(R.string.longitude_error_message)))

        if (latitudeIsValid and longitudeIsValid) {
            setDestination(latitude_text_view.text.toDouble(), longitude_text_view.text.toDouble())
        }
    }

    private fun setDestination(latitude: Double, longitude: Double) {
        EasyPermissions.requestPermissions(this, getString(R.string.location_permission_rationale),
                LOCATION_PERMISSION_REQUEST, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST)
    private fun NIEEEEE() {
        if (EasyPermissions.hasPermissions(context!!, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (!_wasAskedForPermissions) {
                checkIfGPSIsTurnedOn()
            }
            _compassViewModel.observeLocation(context!!)
        } else {
            EasyPermissions.requestPermissions(
                    activity!!,
                    getString(R.string.location_permission_rationale),
                    LOCATION_PERMISSION_REQUEST,
                    Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkIfGPSIsTurnedOn() {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(activity)
                    .setMessage(getString(R.string.turn_on_gps_rationale))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                    .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
        }
        _wasAskedForPermissions = true
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
