package com.example.todo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.model.TaskModel

@Database(entities = [Entity::class],version=1)
abstract class myDatabase : RoomDatabase() {
    abstract fun dao(): DAO
}