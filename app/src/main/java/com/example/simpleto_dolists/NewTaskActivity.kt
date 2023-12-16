package com.example.simpleto_dolists

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simpleto_dolists.databinding.NewTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class NewTaskActivity : AppCompatActivity() {
    private lateinit var binding: NewTaskBinding

    private lateinit var dbHelper: TaskDbHelper
    
    private var dateTimestamp: Long? = null
    private var hourTimestamp: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = TaskDbHelper(this)

        // Get the current date
        val currentDate = Calendar.getInstance()

        val calendar1 = binding.calendar1

        // Set the minimum date for the CalendarView
        calendar1.minDate = currentDate.timeInMillis

        calendar1.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // The month parameter is 0-based, so add 1 to get the correct month
            val formattedDate = formatDate(year, month + 1, dayOfMonth)

            // Get timestamp in milliseconds
            dateTimestamp = getTimestamp(year, month, dayOfMonth)
        }

        val spinner = binding.spinnerTime
        val timeList = resources.getStringArray(R.array.time_list)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeList)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter

        // Set a listener to be notified of item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Ignore the first item ("Pilih Jam")
                if (position > 0) {
                    // Calculate the timestamp based on the chosen hour
                    val selectedHour = timeList[position].split(":")[0].toInt()
                    hourTimestamp = getTimestampHour(selectedHour)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }

        binding.addButton.setOnClickListener {
            // Get the values from the EditText fields
            val title = binding.titleEditText.text.toString()
            val description = binding.descEditText.text.toString()
            
            if (binding.titleEditText.text.isNullOrBlank()) {
                Toast.makeText(this@NewTaskActivity,
                    "Task name cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                if(dateTimestamp != null && hourTimestamp != null) {
                    // Insert the task into the database
                    dbHelper.insertTask(title, description, dateTimestamp!!, hourTimestamp!!)
                    finishAffinity()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else{
                    Toast.makeText(this@NewTaskActivity,
                        "Please select the date and time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, dayOfMonth) // Month is 0-based in Calendar
        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
        return dateFormat.format(calendar.time)
    }

    private fun getTimestamp(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, dayOfMonth) // Month is 0-based in Calendar
        return calendar.timeInMillis
    }

    private fun getTimestampHour(selectedHour: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}