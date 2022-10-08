package net.softglobe.raniumandroidtask

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.launch
import net.softglobe.raniumandroidtask.data.NeoFeed
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lateinit var startDate: String
        lateinit var endDate: String

        //Start Date Click Event
        val startDatePickerView = findViewById<TextView>(R.id.textViewStartDate)
        startDatePickerView.setOnClickListener {
            val c = Calendar.getInstance();
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val startDatePickerDialog = DatePickerDialog(
                this,
                { view, years, monthOfYear, dayOfMonth ->
                    startDatePickerView.text =
                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + years
                    startDate = years.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                },
                year, month, day
            )
            startDatePickerDialog.show()
        }

        //End Date Click Event
        val endDatePickerView = findViewById<TextView>(R.id.textViewEndDate)
        endDatePickerView.setOnClickListener {
            val c = Calendar.getInstance();
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val endDatePickerDialog = DatePickerDialog(
                this,
                { view, years, monthOfYear, dayOfMonth ->
                    endDatePickerView.text =
                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + years
                    endDate = years.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                },
                year, month, day
            )
            endDatePickerDialog.show()
        }

        //Submit Button Click
        val submitBtn = findViewById<Button>(R.id.submitBtn)
        submitBtn.setOnClickListener {
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            lifecycleScope.launch {
                try {
                    progressBar.visibility = View.VISIBLE
                    val response = RetrofitInstance.api.getUserData(startDate, endDate, "DEMO_KEY")
                    if (response.isSuccessful && response.body() != null) {
                        calculateData(response.body()!!)
                        setUpBarChart(response.body()!!)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception: $e")
                } finally {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    //Method to set up bar chart with data form response
    private fun setUpBarChart(response: NeoFeed) {
        val speed = response.near_earth_objects
        val chart: BarChart = findViewById(R.id.idBarChart)
        val asteroidsByDate = ArrayList<Int>()
        var noOfAsteroids = 0
        for ((k, v) in speed) {
            for (i in v.indices) {
                noOfAsteroids++
            }
            asteroidsByDate.add(noOfAsteroids)
            noOfAsteroids = 0
        }
        val valueSet = ArrayList<BarEntry>()
        for (i in 0 until asteroidsByDate.size) {
            valueSet.add(BarEntry(i.toFloat(), asteroidsByDate[i].toFloat()))
        }
        val barDataSet = BarDataSet(valueSet, "Asteroids")
        val barData = BarData(barDataSet)
        chart.data = barData
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    //Method to calculate the data from response
    private fun calculateData(response: NeoFeed) {
        val speed = response.near_earth_objects
        var fastestAsteroidVelocity = 0.0
        var fastestAsteroidId = -1
        var closestAsteroidDistance = Double.MAX_VALUE
        var closestAsteroidId = -1
        val diametersList = ArrayList<Double>()
        var averageSizeInKm = 0.0

        for ((k, v) in speed) {
            for (i in 0 until v.size) {
                //Velocity
                val currentVelocity =
                    v[i].close_approach_data[0].relative_velocity.kilometers_per_hour.toDouble()
                if (currentVelocity > fastestAsteroidVelocity) {
                    fastestAsteroidVelocity = currentVelocity
                    fastestAsteroidId = v[i].id.toInt()
                }

                //Close distance
                val currentDistance =
                    v[i].close_approach_data[0].miss_distance.kilometers.toDouble()
                if (currentDistance < closestAsteroidDistance) {
                    closestAsteroidDistance = currentDistance
                    closestAsteroidId = v[i].id.toInt()
                }

                //Average distance
                diametersList.add(
                    (v[i].estimated_diameter.kilometers.estimated_diameter_min +
                            v[i].estimated_diameter.kilometers.estimated_diameter_max) / 2
                )
            }
        }

        //Calculating average
        var sum = 0.0;
        for (i in 0 until diametersList.size) {
            sum += diametersList[i]
        }
        averageSizeInKm = sum / diametersList.size

        //Setting up respective values to TextViews
        findViewById<TextView>(R.id.fastestAsteroidData).text =
            "Id: $fastestAsteroidId\nSpeed: $fastestAsteroidVelocity km/hr"
        findViewById<TextView>(R.id.closestAsteroidData).text =
            "Id: $closestAsteroidId\nDistance: $closestAsteroidDistance km"
        findViewById<TextView>(R.id.averageSizedAsteroidData).text = "Diameter: $averageSizeInKm km"
    }
}