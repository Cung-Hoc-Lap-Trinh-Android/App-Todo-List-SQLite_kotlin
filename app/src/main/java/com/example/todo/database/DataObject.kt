package com.example.todo.database

import com.example.todo.model.TaskModel


object DataObject {
    var listdata = mutableListOf<TaskModel>()

    fun setData(taskTitle: String,
                taskDescrption: String,
                date: String,
                isComplete: Int,
                lastAlarm: String) {
        listdata.add(TaskModel(taskTitle,taskDescrption,date,isComplete,lastAlarm))
    }

    fun getAllData(): List<TaskModel> {
        return listdata
    }

    fun deleteAll(){
        listdata.clear()
    }

    fun getData(pos:Int): TaskModel {
        return listdata[pos]
    }

    fun deleteData(pos:Int){
        listdata.removeAt(pos)
    }

    fun updateData(pos:Int,taskTitle: String,
                   taskDescrption: String,
                   date: String,
                   isComplete: Int,
                   lastAlarm: String)
    {
        listdata[pos].taskTitle=taskTitle
        listdata[pos].taskDescrption=taskDescrption
        listdata[pos].date=date
        listdata[pos].isComplete=isComplete
        listdata[pos].lastAlarm=lastAlarm
    }

}