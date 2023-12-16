package com.example.simpleto_dolists

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleto_dolists.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), ToDoAdapter.OnItemClickListener {

    private lateinit var binding:ActivityMainBinding

    private lateinit var dbHelper: TaskDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ToDoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = TaskDbHelper(this)

        // Initialize the RecyclerView
        recyclerView = binding.recyclerViewTodoList

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        adapter = ToDoAdapter(this, mutableListOf(), dbHelper, this)

        // Set the adapter to the RecyclerView
        recyclerView.adapter = adapter

        // Call a method to load and display tasks
        loadAndDisplayTasks()

        binding.addButton.setOnClickListener{
            startActivity(Intent(this, NewTaskActivity::class.java))
        }
    }

    private fun loadAndDisplayTasks() {
        // Read tasks from the database
        val tasks = dbHelper.readTasks()

        // Sort the tasks by dueDate and dueTime
        val sortedTasks = tasks.sortedWith(compareBy({ it.dueDate }, { it.dueTime }))

        Log.d("MainActivity", "Number of tasks: ${tasks.size}")
        Log.d("MainActivity", "Number of sorted tasks: ${sortedTasks.size}")

        // Update the adapter with the sorted list
        adapter.updateData(sortedTasks)

        dbHelper.logAllTasks()
    }

    override fun onItemClick() {
        loadAndDisplayTasks()
    }
}