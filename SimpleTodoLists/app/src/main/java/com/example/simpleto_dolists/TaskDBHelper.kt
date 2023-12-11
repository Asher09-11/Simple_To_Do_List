package com.example.simpleto_dolists

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

data class Task(val id: Long, val title: String, val description: String, val dueDate: Long, val dueTime: Long)

object TaskContract {
    object TaskEntry : BaseColumns {
        const val _ID = "_id"
        const val TABLE_NAME = "tasks"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DUE_DATE = "due_date"
        const val COLUMN_DUE_TIME = "due_time"
    }
}

class TaskDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "tasks.db"
        private val DATABASE_VERSION = 1
    }

    private val SQL_CREATE_ENTRIES = """
    CREATE TABLE ${TaskContract.TaskEntry.TABLE_NAME} (
        ${TaskContract.TaskEntry._ID} INTEGER PRIMARY KEY,
        ${TaskContract.TaskEntry.COLUMN_TITLE} TEXT,
        ${TaskContract.TaskEntry.COLUMN_DESCRIPTION} TEXT,
        ${TaskContract.TaskEntry.COLUMN_DUE_DATE} INTEGER,
        ${TaskContract.TaskEntry.COLUMN_DUE_TIME} INTEGER  -- Add this line for the new column
    )
"""

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades, e.g., alter table or recreate
        // For simplicity, we'll drop the table and recreate it
        db.execSQL("DROP TABLE IF EXISTS ${TaskContract.TaskEntry.TABLE_NAME}")
        onCreate(db)
    }

    fun insertTask(title: String, description: String, dueDate: Long, dueTime: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TaskContract.TaskEntry.COLUMN_TITLE, title)
            put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, description)
            put(TaskContract.TaskEntry.COLUMN_DUE_DATE, dueDate)
            put(TaskContract.TaskEntry.COLUMN_DUE_TIME, dueTime)
        }
        return db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values)
    }

    fun readTasks(): List<Task> {
        val db = readableDatabase
        val projection = arrayOf(
            TaskContract.TaskEntry._ID,
            TaskContract.TaskEntry.COLUMN_TITLE,
            TaskContract.TaskEntry.COLUMN_DESCRIPTION,
            TaskContract.TaskEntry.COLUMN_DUE_DATE,
            TaskContract.TaskEntry.COLUMN_DUE_TIME
        )

        val cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val tasks = mutableListOf<Task>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(TaskContract.TaskEntry._ID))
                val title = getString(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TITLE))
                val description = getString(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DESCRIPTION))
                val dueDate = getLong(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DUE_DATE))
                val dueTime = getLong(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DUE_TIME))

                tasks.add(Task(id, title, description, dueDate, dueTime))
            }
        }
        cursor.close()

        return tasks
    }
}
