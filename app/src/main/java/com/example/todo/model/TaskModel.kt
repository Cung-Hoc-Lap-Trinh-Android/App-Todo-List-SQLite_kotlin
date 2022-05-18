package com.example.todo.model

//tạo đối tượng
data class TaskModel (
    var taskTitle: String,
    var taskDescrption: String,
    var date: String,
    var isComplete: Int,
    var lastAlarm: String,
)