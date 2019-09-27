package com.github.szymonrudnicki.compassproject.ui.compass

import android.content.Context
import android.hardware.Sensor
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.github.szymonrudnicki.compassproject.ui.compass.states.DestinationState
import com.github.szymonrudnicki.compassproject.ui.compass.states.InputState
import com.github.szymonrudnicki.compassproject.ui.validators.LatitudeValidator
import com.github.szymonrudnicki.compassproject.ui.validators.LongitudeValidator
import com.github.szymonrudnicki.compassproject.utils.SensorUtils
import com.github.szymonrudnicki.compassproject.utils.formatDistance
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.rx.ObservableFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

class CompassViewModel : ViewModel() {

    private val _compositeDisposable = CompositeDisposable()

    val northAzimuthLiveData = MutableLiveData<Double>()
    val destinationLiveData = MutableLiveData<DestinationState>()

    val latitudeStatusLiveData = MutableLiveData<InputState>()
    val longitudeStatusLiveData = MutableLiveData<InputState>()

    private var _latitude by Delegates.observable<Double?>(null) {
            _, _, _ -> calculateDestinationAzimuth()
    }
    private var _longitude by Delegates.observable<Double?>(null) {
            _, _, _ -> calculateDestinationAzimuth()
    }
    private var _deviceLocation by Delegates.observable<Location?>(null) {
        _, _, _ -> calculateDestinationAzimuth()
    }

    fun getCompassAzimuth(context: Context) {
        _compositeDisposable.add(
            ReactiveSensors(context).observeSensor(Sensor.TYPE_ROTATION_VECTOR)
                .subscribeOn(Schedulers.computation())
                .filter(ReactiveSensorFilter.filterSensorChanged())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { reactiveSensorEvent ->
                    val azimuth = SensorUtils.calculateAzimuth(reactiveSensorEvent.sensorEvent)
                    northAzimuthLiveData.postValue(azimuth)
                }
        )
    }

    fun observeLocation(context: Context) {
        _compositeDisposable.add(
            ObservableFactory.from(SmartLocation.with(context).location())
                .subscribe(::updateLocation)
        )
    }

    fun validateLatitude(latitudeCandidate: String) {
        if(LatitudeValidator.isValid(latitudeCandidate)) {
            _latitude = latitudeCandidate.toDouble()
            latitudeStatusLiveData.postValue(InputState.Valid)
        } else {
            _latitude = null
            latitudeStatusLiveData.postValue(InputState.Invalid)
        }
    }

    fun validateLongitude(longitudeCandidate: String) {
        if (LongitudeValidator.isValid(longitudeCandidate)) {
            _longitude = longitudeCandidate.toDouble()
            longitudeStatusLiveData.postValue(InputState.Valid)
        } else {
            _longitude = null
            longitudeStatusLiveData.postValue(InputState.Invalid)
        }
    }

    private fun updateLocation(location: Location) {
        _deviceLocation = location
    }

    private fun calculateDestinationAzimuth() {
        if (_latitude != null && _longitude != null && _deviceLocation != null) {
            val destinationLocation = Location(LocationManager.GPS_PROVIDER).apply {
                latitude = _latitude!!
                longitude = _longitude!!
            }
            val bearingToDestination = _deviceLocation!!.bearingTo(destinationLocation)
            val destinationAzimuth = (bearingToDestination + 360) % 360
            val distanceToDestination = _deviceLocation!!.distanceTo(destinationLocation)
            destinationLiveData.postValue(
                DestinationState.Available(destinationAzimuth, distanceToDestination.formatDistance())
            )
        } else {
            destinationLiveData.postValue(DestinationState.Unavailable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _compositeDisposable.dispose()
    }
}
