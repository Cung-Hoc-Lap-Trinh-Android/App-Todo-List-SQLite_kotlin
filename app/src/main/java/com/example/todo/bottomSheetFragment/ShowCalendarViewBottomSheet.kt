package com.example.todolist.bottomSheetFragment

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import androidx.room.Room
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.database.DataObject
import com.example.todo.model.TaskModel
import com.example.todo.database.myDatabase
import kotlinx.android.synthetic.main.fragment_calendar_view.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ShowCalendarViewBottomSheet : BottomSheetDialogFragment() {
    lateinit var activity: MainActivity
    private lateinit var database: myDatabase

    var tasks: List<TaskModel> = ArrayList()
    private val bottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_calendar_view, null)

        activity = getActivity() as MainActivity
        database = Room.databaseBuilder(
            activity, myDatabase::class.java, "To_Do"
        ).build()
        GlobalScope.launch {
            DataObject.listdata = database.dao().getTasks() as MutableList<TaskModel>
            tasks = DataObject.listdata
            contentView.calendarView!!.setEvents(highlitedDays())
        }

        dialog.setContentView(contentView)
        contentView.calendarView!!.setHeaderColor(R.color.colorAccent)
//        savedTasks
        contentView.back!!.setOnClickListener { view: View? -> dialog.dismiss() }
    }

    override fun onDestroyView() {     //kết thúc tiến trình - nhận kết quả từ doInBackground
        super.onDestroyView()
    }

    //Lấy ngày
    private fun highlitedDays(): List<EventDay>{
            val events: MutableList<EventDay> = ArrayList()
            for (i in tasks.indices) {
                val calendar = Calendar.getInstance()
                val items1 = tasks[i].date.split("-").toTypedArray() //Lấy ngày
                val dd = items1[0]
                val month = items1[1]
                val year = items1[2]
                calendar[Calendar.DAY_OF_MONTH] = dd.toInt()
                calendar[Calendar.MONTH] = month.toInt() - 1
                calendar[Calendar.YEAR] = year.toInt()

                events.add(EventDay(calendar, R.drawable.dot))
            }
            return events
        }
}

