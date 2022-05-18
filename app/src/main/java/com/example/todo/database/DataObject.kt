package com.example.todo.database

import com.example.todo.model.TaskModel

//sửa dữ liệu trên giao diện
object DataObject {
    //khởi tạo danh sách đối tượng (task: Nhiệm vụ)
    var listdata = mutableListOf<TaskModel>()

    //thêm Task vào danh sách
    fun setData(taskTitle: String,
                taskDescrption: String,
                date: String,
                isComplete: Int,
                lastAlarm: String) {
        listdata.add(TaskModel(taskTitle,taskDescrption,date,isComplete,lastAlarm))
    }

    //lấy ra tất cả dữ liệu có trong danh sách
    fun getAllData(): List<TaskModel> {
        return listdata
    }

    //lấy ra 1 phần tử Task trong danh sách
    fun getData(pos:Int): TaskModel {
        return listdata[pos]
    }

    //xóa 1 phần tử Task trong danh sách
    fun deleteData(pos:Int){
        listdata.removeAt(pos)
    }

    //sửa 1 phần tử Task trong danh sách
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