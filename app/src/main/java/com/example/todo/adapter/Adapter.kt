package com.example.todo.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todo.*
import com.example.todo.database.DataObject
import com.example.todo.database.Entity
import com.example.todo.database.myDatabase
import com.example.todo.model.TaskModel
import com.example.todolist.bottomSheetFragment.CreateTaskBottomSheetFragment
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class Adapter(var data: List<TaskModel>, var setRefreshListener: CreateTaskBottomSheetFragment.SetRefreshListener,
              private val mainActivity: MainActivity)
    : RecyclerView.Adapter<Adapter.viewHolder>() {
    var dateFormat = SimpleDateFormat("EE dd MMM yyyy", Locale.US)
    var inputDateFormat = SimpleDateFormat("dd-M-yyyy", Locale.US)
    var date: Date? = null
    var outputDateString: String? = null
    private lateinit var database: myDatabase

    //ánh xạ ID các phần tử trên giao diện item
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var day = itemView.day
            var date = itemView.date
            var month = itemView.month
            var title = itemView.title
            var description = itemView.description
            var status = itemView.checkBtn
            var options = itemView.options
            var time = itemView.time

    }

//    liên kết giao diện item trong recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return viewHolder(itemView)
    }

    // đỗ dữ liệu lên các thành phần
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        database = Room.databaseBuilder(
            holder.itemView.context, myDatabase::class.java, "To_Do"
        ).build()
        val task = data[position]
        holder.title.text = task.taskTitle
        holder.description.text = task.taskDescrption
        holder.time.text = task.lastAlarm
        holder.status.isChecked = toBoolean(task.isComplete)

        //lắng nghe sự kiện thay đổi của Checkbox - tích vào -> status.isChecked = true -> cho giá trị trả về bằng 1 => Nhiệm vụ Task đã hoàn thành
        //không tích  -> cho giá trị trả về bằng 0 => Nhiệm vụ Task đang và sẽ thực hiện
        holder.status.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (holder.status.isChecked) {
                //cập nhật lại dữ liệu - trường hợp tích
                DataObject.updateData(position, task.taskTitle, task.taskDescrption, task.date,
                    1, task.lastAlarm)
                GlobalScope.launch {
                    database.dao().updateTask(
                        Entity(position+1, task.taskTitle, task.taskDescrption, task.date,
                            1, task.lastAlarm
                        )
                    )
                }
            } else {
                //cập nhật lại dữ liệu - trường hợp bỏ tích
                DataObject.updateData(position, task.taskTitle, task.taskDescrption, task.date,
                    0, task.lastAlarm)
                GlobalScope.launch {
                    database.dao().updateTask(
                        Entity(position+1, task.taskTitle, task.taskDescrption, task.date,
                            0, task.lastAlarm
                        )
                    )
                }
            }
        }
        //lắng nghe sự kiện và hiển thị menu ngữ cảnh - PopupMenu
        holder.options.setOnClickListener { view: View? -> showPopUpMenu(holder.itemView.context,view, position) }
        try {
            //định dạng/fomat lại ngày tháng và thực hiện cắt chuỗi
            date = inputDateFormat.parse(task.date)
            outputDateString = dateFormat.format(date)
            val items1 = outputDateString?.split(" ")?.toTypedArray() ?: return
            val day = items1[0]
            val dd = items1[1]
            val month = items1[2]
            //hiển thị lên giao hiện
            holder.day.text = day
            holder.date.text = dd
            holder.month.text = month
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

//hiển thị menu ngữ cảnh - xóa/sửa
    fun showPopUpMenu(context: Context, view: View?, position: Int) {
        val task = data[position]
        val popupMenu = PopupMenu(context, view)
        //ánh xạ layout: menu
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {  //lăng nghe Item Id
                R.id.menuDelete -> {
                    val alertDialogBuilder = AlertDialog.Builder(
                        context, R.style.AppTheme_Dialog
                    )
                    //định dạng hiển  thị cho dialog
                    alertDialogBuilder.setTitle(R.string.delete_confirmation)     //tiêu đề
                        .setMessage(R.string.sureToDelete)   //nội dung
                        .setPositiveButton(R.string.yes) { dialog: DialogInterface?, which: Int ->
                            //thực hiện chức năng xóa
                            DataObject.deleteData(position)
                            GlobalScope.launch {
                                database.dao().deleteTask(
                                    Entity(position+1, task.taskTitle, task.taskDescrption, task.date,
                                        task.isComplete, task.lastAlarm
                                    )
                                )
                            }
                            setRefreshListener.refresh()
                        }
                        .setNegativeButton(R.string.no) { dialog: DialogInterface, which: Int -> dialog.cancel() }
                        .show()
                }
                R.id.menuUpdate -> {
                    //khởi tạo và hiển thị dialog Update
                    val createTaskBottomSheetFragment = CreateTaskBottomSheetFragment()
                    createTaskBottomSheetFragment.setTaskItem(position, true, mainActivity, mainActivity)
                    createTaskBottomSheetFragment.show(
                        mainActivity.supportFragmentManager,
                        createTaskBottomSheetFragment.tag
                    )
                }
            }
            false
        }
        //hiển thị menu ngũ cảnh
        popupMenu.show()
    }

    //trả về giá trị khác ko - giá trị của task.status
    private fun toBoolean(num: Int): Boolean {
        return num != 0
    }

    //trả về kích thước của danh sách
    override fun getItemCount(): Int {
        return data.size
    }
}