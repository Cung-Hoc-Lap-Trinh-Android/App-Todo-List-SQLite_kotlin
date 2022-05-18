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

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var title = itemView.title
//        var priority = itemView.priority
//        var layout = itemView.mylayout

            var day = itemView.day
            var date = itemView.date
            var month = itemView.month
            var title = itemView.title
            var description = itemView.description
            var status = itemView.checkBtn
            var options = itemView.options
            var time = itemView.time
//            ButterKnife.bind(this, view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
//        when (data[position].priority.toLowerCase()) {
//            "high" -> holder.layout.setBackgroundColor(Color.parseColor("#F05454"))
//            "medium" -> holder.layout.setBackgroundColor(Color.parseColor("#EDC988"))
//            else -> holder.layout.setBackgroundColor(Color.parseColor("#00917C"))
//        }
//
//        holder.title.text = data[position].title
//        holder.priority.text = data[position].priority
//        holder.itemView.setOnClickListener{
//            val intent= Intent(holder.itemView.context, UpdateCard::class.java)
//            intent.putExtra("id",position)
//            holder.itemView.context.startActivity(intent)
//        }
        database = Room.databaseBuilder(
            holder.itemView.context, myDatabase::class.java, "To_Do"
        ).build()
        val task = data[position]
        holder.title.text = task.taskTitle
        holder.description.text = task.taskDescrption
        holder.time.text = task.lastAlarm
        holder.status.isChecked = toBoolean(task.isComplete)
        holder.status.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (holder.status.isChecked) {
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
        holder.options.setOnClickListener { view: View? -> showPopUpMenu(holder.itemView.context,view, position) }
        try {
            date = inputDateFormat.parse(task.date)
            outputDateString = dateFormat.format(date)
            val items1 = outputDateString?.split(" ")?.toTypedArray() ?: return
            val day = items1[0]
            val dd = items1[1]
            val month = items1[2]
            holder.day.text = day
            holder.date.text = dd
            holder.month.text = month
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun showPopUpMenu(context: Context, view: View?, position: Int) {
        val task = data[position]
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menuDelete -> {
                    val alertDialogBuilder = AlertDialog.Builder(
                        context, R.style.AppTheme_Dialog
                    )
                    alertDialogBuilder.setTitle(R.string.delete_confirmation)
                        .setMessage(R.string.sureToDelete)
                        .setPositiveButton(R.string.yes) { dialog: DialogInterface?, which: Int ->
                          //  deleteTaskFromId(task.taskId, position)
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
        popupMenu.show()
    }

//    private fun updateStatusID(taskId: Int, i: Int) {
//        database = Room.databaseBuilder(
//            context, myDatabase::class.java, "To_Do"
//        ).build()
//        database.dao().updateStatusRow(taskId, i)
//
//    }
//
//    private fun deleteTaskFromId(taskId: Int, position: Int) {
//        database = Room.databaseBuilder(
//            context, myDatabase::class.java, "To_Do"
//        ).build()
//        database.dao().deleteTaskFromId(taskId)
//    }

    private fun toBoolean(num: Int): Boolean {
        return num != 0
    }

    override fun getItemCount(): Int {
        return data.size
    }
}