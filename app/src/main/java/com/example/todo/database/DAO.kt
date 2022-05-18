package com.example.todo.database

import androidx.room.*
import com.example.todo.model.TaskModel

//sửa giữ liệu bên database
@Dao
interface DAO {
    //thêm 1 Task mới vào database
    @Insert
    suspend fun insertTask(entity: Entity)

    //sửa 1 Task trong database dựa trên bảng dữ liệu Entity
    @Update
    suspend fun updateTask(entity: Entity)

    //xóa 1 Task trong database dựa trên bảng dữ liệu Entity
    @Delete
    suspend fun deleteTask(entity: Entity)

    //lấy ra tất cả dữ liệu trên bảng to_do - bảng dữ liệu Entity
    @Query("Select * from to_do")
    suspend fun getTasks():List<TaskModel>

}