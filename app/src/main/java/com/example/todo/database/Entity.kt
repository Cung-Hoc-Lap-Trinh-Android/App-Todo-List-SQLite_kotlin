package com.example.todo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

//tạo bảng chứa dữ liệu với tên TO_do
@Entity(tableName = "To_Do")
data class Entity(
    @PrimaryKey(autoGenerate = true)
    var taskId: Int ,
    var taskTitle: String ,
    var taskDescrption: String,
    var date: String,
    var isComplete: Int ,
    var lastAlarm: String
)