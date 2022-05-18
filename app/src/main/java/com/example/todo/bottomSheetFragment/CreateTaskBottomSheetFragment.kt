package com.example.todolist.bottomSheetFragment

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.*
import androidx.room.Room
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.database.DataObject
import com.example.todo.database.Entity
import com.example.todo.model.TaskModel
import com.example.todo.database.myDatabase
import kotlinx.android.synthetic.main.fragment_create_task.*
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

//???
class CreateTaskBottomSheetFragment : BottomSheetDialogFragment() {
    var status = 0
    var pos = 0
    var isEdit = false
    var task: TaskModel? = null
    var mYear = 0
    var mMonth = 0
    var mDay = 0
    var mHour = 0
    var mMinute = 0
    var setRefreshListener: SetRefreshListener? = null
//    var alarmManager: AlarmManager? = null
    var timePickerDialog: TimePickerDialog? = null
    var datePickerDialog: DatePickerDialog? = null
    lateinit var activity: MainActivity
    private lateinit var database: myDatabase

    interface SetRefreshListener {
        fun refresh()
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    fun setTaskItem(position: Int, isEdit: Boolean, setRefreshListener: SetRefreshListener?, activity: MainActivity) {
        this.pos = position
        this.isEdit = isEdit
        this.activity = activity
        this.setRefreshListener = setRefreshListener
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_create_task, null)

        activity = getActivity() as MainActivity
        database = Room.databaseBuilder(
            activity, myDatabase::class.java, "To_Do"
        ).build()
//        GlobalScope.launch {
//            DataObject.listdata = database.dao().getTasks() as MutableList<TaskModel>
//        }

        dialog.setContentView(contentView)
        contentView.addTask.setOnClickListener { view: View? -> if (validateFields(contentView)) createTask(contentView) }
        if (isEdit) {
            showTaskFromId(contentView,pos)
        }
        contentView.taskDate.setOnTouchListener { view: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val c = Calendar.getInstance()
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_MONTH]
                datePickerDialog = DatePickerDialog(
                    activity,
                    { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        contentView.taskDate.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                        datePickerDialog?.dismiss()
                    }, mYear, mMonth, mDay
                )
                datePickerDialog?.datePicker?.minDate = System.currentTimeMillis() - 1000
                datePickerDialog?.show()
            }
            true
        }
        contentView.taskTime.setOnTouchListener { view: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // Get Current Time
                val c = Calendar.getInstance()
                mHour = c[Calendar.HOUR_OF_DAY]
                mMinute = c[Calendar.MINUTE]

                // Launch Time Picker Dialog
                timePickerDialog = TimePickerDialog(getActivity(),
                    { view12: TimePicker?, hourOfDay: Int, minute: Int ->
                        contentView.taskTime?.setText("$hourOfDay:$minute")
                        timePickerDialog?.dismiss()
                    }, mHour, mMinute, false
                )
                timePickerDialog?.show()
            }
            true
        }
    }

    fun validateFields(view: View): Boolean {
        return if (view.addTaskTitle.text.toString().equals("", ignoreCase = true)) {
            Toast.makeText(activity, "Please enter a valid title", Toast.LENGTH_SHORT).show()
            false
        } else if (view.addTaskDescription.text.toString().equals("", ignoreCase = true)) {
            Toast.makeText(activity, "Please enter a valid description", Toast.LENGTH_SHORT).show()
            false
        } else if (view.taskDate.text.toString().equals("", ignoreCase = true)) {
            Toast.makeText(activity, "Please enter date", Toast.LENGTH_SHORT).show()
            false
        } else if (view.taskTime.text.toString().equals("", ignoreCase = true)) {
            Toast.makeText(activity, "Please enter time", Toast.LENGTH_SHORT).show()
            false
            //        } else if (taskEvent.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(activity, "Please enter an event", Toast.LENGTH_SHORT).show();
//            return false;
        } else {
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun createTask(view: View) {
                var taskTitle = view.addTaskTitle.text.toString()
                var taskDescrption = view.addTaskDescription.text.toString()
                var date = view.taskDate.text.toString()
                var lastAlarm = view.taskTime.text.toString()

                if (!isEdit){
                    DataObject.setData(taskTitle, taskDescrption, date, status, lastAlarm)
                    GlobalScope.launch {
                        database.dao().insertTask(
                            Entity(0, taskTitle, taskDescrption, date, status, lastAlarm
                            )
                        )
                    }
                    Toast.makeText(getActivity(), "Your event is been added", Toast.LENGTH_SHORT).show()
                }
                else{
//                    val pos = activity.intent.getIntExtra("id", -1)
//                    showTaskFromId(pos)
                    DataObject.updateData(pos, taskTitle, taskDescrption, date, status, lastAlarm)
                    GlobalScope.launch {
                        database.dao().updateTask(
                            Entity(pos + 1, taskTitle, taskDescrption, date, status, lastAlarm
                            )
                        )
                    }
                    Toast.makeText(getActivity(), "Your event is been update", Toast.LENGTH_SHORT).show()
                }
                setRefreshListener?.refresh()
                dismiss()
    }

    private fun showTaskFromId(view: View, pos : Int) {
        view.addTaskTitle.setText(DataObject.getData(pos).taskTitle)
        Toast.makeText(getActivity(), DataObject.getData(pos).taskTitle + " helllo", Toast.LENGTH_SHORT).show()
        view.addTaskDescription.setText(DataObject.getData(pos).taskDescrption)
        view.taskDate.setText(DataObject.getData(pos).date)
        view.taskTime.setText(DataObject.getData(pos).lastAlarm)
        status = DataObject.getData(pos).isComplete
    }

    private fun setDataInUI(view: View) {
        view.addTaskTitle.setText(task?.taskTitle)
        view.addTaskDescription?.setText(task?.taskDescrption)
        view.taskDate.setText(task?.date)
        view.taskTime.setText(task?.lastAlarm)

//        taskEvent.setText(task.getEvent());
    }

    companion object {
        var count = 0
    }

}
