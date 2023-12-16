package com.example.simpleto_dolists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToDoAdapter (
    private val context: Context,
    private val taskList: MutableList<Task>,
    private val dbHelper: TaskDbHelper,
    private val itemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<ToDoAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.card_to_do, parent, false)
        return MyViewHolder(view, dbHelper)
    }

    interface OnItemClickListener {
        fun onItemClick()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = taskList[position]
        holder.bindBookingData(task, itemClickListener)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    class MyViewHolder(itemView: View, private val dbHelper: TaskDbHelper) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.text_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_description)
        private val dateTextView: TextView = itemView.findViewById(R.id.text_date)
        private val timeTextView: TextView = itemView.findViewById(R.id.text_time)
        private val closeButton: ImageButton = itemView.findViewById(R.id.close_button)

        fun bindBookingData(task: Task, listener: OnItemClickListener) {
            val taskId = task.id

            titleTextView.text = task.title
            descriptionTextView.text = task.description
            dateTextView.text = convertTimestampToDate(task.dueDate)
            timeTextView.text = convertTimestampToTime(task.dueTime)

            closeButton.setOnClickListener{
                deleteTaskFromDatabase(taskId)
                listener.onItemClick()
            }
        }

        private fun deleteTaskFromDatabase(taskId: Long) {
            dbHelper.deleteTask(taskId)
        }

        private fun convertTimestampToDate(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }

        private fun convertTimestampToTime(timestamp: Long): String {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(timestamp)
            return timeFormat.format(date)
        }
    }

    fun updateData(newList: List<Task>) {
        taskList.clear()
        taskList.addAll(newList)
        notifyDataSetChanged()
    }

}