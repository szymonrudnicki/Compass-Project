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
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.szymonrudnicki.compassproject.R
import com.github.szymonrudnicki.compassproject.extensions.observe
import com.github.szymonrudnicki.compassproject.ui.compass.states.DestinationAzimuthState
import com.github.szymonrudnicki.compassproject.ui.compass.states.InputState
import kotlinx.android.synthetic.main.fragment_compass.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

private const val LOCATION_PERMISSION_REQUEST_CODE = 1337

class CompassFragment : Fragment() {

    private var _wasGPSStatusChecked = false

    private val _compassViewModel by viewModels<CompassViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_compass, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeLocation()
        setupEditTexts()
        with(_compassViewModel) {
            getCompassAzimuth(requireContext())
            observe(northAzimuthLiveData, ::rotateCompass)
            observe(destinationAzimuthLiveData, ::setDestinationAzimuth)
            observe(latitudeStatusLiveData, ::setLatitudeInputState)
            observe(longitudeStatusLiveData, ::setLongitudeInputState)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun setupEditTexts() {
        latitude_edit_text.doOnTextChanged { text, _, _, _ ->
            _compassViewModel.validateLatitude(text.toString())
        }
        longitude_edit_text.doOnTextChanged { text, _, _, _ ->
            _compassViewModel.validateLongitude(text.toString())
        }
    }

    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST_CODE)
    private fun observeLocation() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (!_wasGPSStatusChecked) {
                checkIfGPSIsTurnedOn()
                _wasGPSStatusChecked = true
            }
            _compassViewModel.observeLocation(requireContext())
        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                getString(R.string.location_permission_rationale),
                LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun checkIfGPSIsTurnedOn() {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showTurnOnGPSDialog()
        }
    }

    private fun showTurnOnGPSDialog() {
        AlertDialog.Builder(activity)
            .setMessage(getString(R.string.turn_on_gps_rationale))
            .setCancelable(false)
            .setPositiveButton(android.R.string.yes) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun rotateCompass(azimuth: Double?) {
        compass_view.rotateCompass(azimuth)
    }

    private fun setDestinationAzimuth(azimuthState: DestinationAzimuthState?) {
        compass_view.setDestinationAzimuth(azimuthState)
    }

    private fun setLatitudeInputState(inputState: InputState?) {
        latitude_edit_text.error = when (inputState!!) {
            is InputState.Valid -> null
            is InputState.Invalid -> getString(R.string.latitude_error_message)
        }
    }

    private fun setLongitudeInputState(inputState: InputState?) {
        longitude_edit_text.error = when (inputState!!) {
            is InputState.Valid -> null
            is InputState.Invalid -> getString(R.string.longitude_error_message)
        }
    }
}