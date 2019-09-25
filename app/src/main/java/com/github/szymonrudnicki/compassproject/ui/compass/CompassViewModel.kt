package com.github.szymonrudnicki.compassproject.ui.compass

import android.content.Context
import android.hardware.Sensor
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.github.szymonrudnicki.compassproject.utils.SensorUtils
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.rx.ObservableFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CompassViewModel : ViewModel() {

    private val _compositeDisposable = CompositeDisposable()

    private val _azimuthLiveData = MutableLiveData<Float>()
    val azimuthLiveData : LiveData<Float> = _azimuthLiveData

    fun getCompassAzimuth(context: Context) {
        _compositeDisposable.add(
                ReactiveSensors(context).observeSensor(Sensor.TYPE_ROTATION_VECTOR)
                        .subscribeOn(Schedulers.computation())
                        .filter(ReactiveSensorFilter.filterSensorChanged())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { reactiveSensorEvent ->
                            val azimuth = SensorUtils.calculateAzimuth(reactiveSensorEvent.sensorEvent)
                            _azimuthLiveData.postValue(azimuth)
                        }
        )
    }

    fun observeLocation(context: Context) {
        _compositeDisposable.add(
                ObservableFactory.from(SmartLocation.with(context).location())
                        .subscribe(::calculateBearingToDestination)
        )
    }

    private fun calculateBearingToDestination(location: Location) {
        Log.d("SMARTLOCATION", location.toString()) // todo
    }

    override fun onCleared() {
        super.onCleared()
        _compositeDisposable.dispose()
    }
}
