package com.joonaskinnunen.weatherapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joonaskinnunen.weatherapp.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), SearchDialogFragment.AddDialogListener {

    val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

    companion object {
        var forecasts: MutableList<Forecast> = mutableListOf()
    }

    val API_LINK: String = "https://api.openweathermap.org/data/2.5/weather?q="
    val API_ICON: String = "https://openweathermap.org/img/w/"
    val API_KEY: String = "1d25502ae009557485dc730993bfd213"

    // add a few test cities
    var cities: MutableList<String> = mutableListOf("Jyväskylä", "Helsinki", "Oulu", "New York", "Tokyo")
    // city index, used when data will be loaded
    var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadWeatherForecast(cities[index])
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDialogPositiveClick(searchString: String) {
        forecasts.clear()
        sectionsPagerAdapter.notifyDataSetChanged()
        index = 0
        cities.add(0, searchString)
        loadWeatherForecast(cities[index])
    }

    // load forecast
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadWeatherForecast(city:String) {
        progressBar.visibility = View.VISIBLE
        // url for loading
        val url = "$API_LINK$city&APPID=$API_KEY&units=metric&lang=fi"

        // JSON object request with Volley
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, Response.Listener<JSONObject> { response ->
                try {
                    val mainJSONObject = response.getJSONObject("main")
                    val weatherArray = response.getJSONArray("weather")
                    val firstWeatherObject = weatherArray.getJSONObject(0)

                    val city = response.getString("name")
                    Log.d("city: ", city)
                    val condition = firstWeatherObject.getString("main")
                    val temperature = mainJSONObject.getString("temp")+" °C"

                    val weatherTime: String = response.getString("dt")
                    val weatherLong: Long = weatherTime.toLong()
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss")
                    val dt = Instant.ofEpochSecond(weatherLong).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter).toString()

                    val weatherIcon = firstWeatherObject.getString("icon")
                    val url = "$API_ICON$weatherIcon.png"

                    // add forecast object to the list
                    forecasts.add(Forecast(city,condition,temperature,dt,url))
                    // use Logcat window to check that loading really works
                    Log.d("WEATHERCITY", "**** weatherCity = " + forecasts[index].city)
                    // load another city if not loaded yet
                    if ((++index) < cities.size) loadWeatherForecast(cities[index])
                    else {
                        Log.d("WEATHER", "*** ALL LOADED!")
                        setUI()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("WEATHER", "***** error: $e")
                    // hide progress bar
                    progressBar.visibility = View.INVISIBLE
                    // show Toast -> should be done better!!!
                    Toast.makeText(this,"Error loading weather forecast!",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Log.d("PTM", "Error: $error")
            })
        // start loading data with Volley
        val queue = Volley.newRequestQueue(applicationContext)
        queue.add(jsonObjectRequest)
    }

    private fun setUI() {
        // hide progress bar
        progressBar.visibility = View.INVISIBLE
        // add adapter
        val viewPager: ViewPager = findViewById(R.id.viewPager)
        viewPager.adapter = sectionsPagerAdapter
        // add fab
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            // create and show dialog
            val dialog = SearchDialogFragment()
            dialog.show(supportFragmentManager, "SearchDialogFragment")
        }
    }
}