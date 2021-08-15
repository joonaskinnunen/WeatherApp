package com.joonaskinnunen.weatherapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joonaskinnunen.weatherapp.Forecast

class PageViewModel : ViewModel() {
    private val _forecast = MutableLiveData<Forecast>()

    val forecast: LiveData<Forecast> = Transformations.map(_forecast) {
        it
    }

    fun setForecast(forecast: Forecast) {
        _forecast.value = forecast
    }
}