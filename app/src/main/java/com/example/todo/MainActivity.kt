package com.example.todo


import android.app.SearchManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.todo.adapter.Adapter
import com.example.todo.database.DataObject
import com.example.todo.database.myDatabase
import com.example.todo.model.TaskModel
import com.example.todolist.bottomSheetFragment.CreateTaskBottomSheetFragment
import com.example.todolist.bottomSheetFragment.ShowCalendarViewBottomSheet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.ArrayList


// entity - table
// dao - queries

class MainActivity : AppCompatActivity(), CreateTaskBottomSheetFragment.SetRefreshListener{
    private lateinit var database: myDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.toolbar)
        Glide.with(applicationContext).load(R.drawable.new_todo).into(noDataImage!!)

        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "To_Do"
        ).build()
        GlobalScope.launch {
            DataObject.listdata = database.dao().getTasks() as MutableList<TaskModel>
            setRecycler()
        }

        btnAddTask.setOnClickListener {view: View? ->
//            val intent = Intent(this, CreateCard::class.java)
//            startActivity(intent)
            val createTaskBottomSheetFragment = CreateTaskBottomSheetFragment()
            createTaskBottomSheetFragment.setTaskItem(0, false, this, this@MainActivity)
            createTaskBottomSheetFragment.show(
                supportFragmentManager,
                createTaskBottomSheetFragment.tag
            )
        }
//        deleteAll.setOnClickListener {
//            DataObject.deleteAll()
//            GlobalScope.launch {
//                database.dao().deleteAll()
//            }
//            setRecycler()
//        }

    }

    fun setRecycler() {
        noDataImage!!.visibility = if (DataObject.getAllData().isEmpty()) View.VISIBLE else View.GONE
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        taskRecycler.adapter = Adapter(DataObject.getAllData(), this, this@MainActivity)
        taskRecycler.layoutManager = linearLayoutManager
    }

    override fun refresh() {
        setRecycler()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchManager = this.getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.componentName))
        val theTextArea = searchView.findViewById<View>(R.id.search_src_text) as SearchView.SearchAutoComplete
        theTextArea.setTextColor(resources.getColor(R.color.colorAccent)) //or any color that you want
        theTextArea.setHintTextColor(resources.getColor(R.color.colorAccent))
        //change icon color
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        searchIcon.setColorFilter(resources.getColor(R.color.colorAccent))
        val searchIconClose =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchIconClose.setColorFilter(resources.getColor(R.color.colorAccent))

        //Event
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                startSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        //Clear text when click to Clear button on Search View
        val closeButton = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener { v: View? ->
            val ed = searchView.findViewById<View>(R.id.search_src_text) as EditText
            //Clear Text
            ed.setText("")
            //Clear Query
            searchView.setQuery("", false)
            //Collapse the action view
            searchView.onActionViewCollapsed()
            //Collapse the search widget
            menuItem.collapseActionView()
            //Restore result to original
            setRecycler()
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_calendar) {
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
            val showCalendarViewBottomSheet = ShowCalendarViewBottomSheet()
            showCalendarViewBottomSheet.show(
                supportFragmentManager,
                showCalendarViewBottomSheet.tag
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startSearch(query: String) {
        val searchList: MutableList<TaskModel> = ArrayList()
        for (i in DataObject.getAllData().indices) {
            val task = DataObject.getAllData()[i]
            if (task.taskTitle.toLowerCase().contains(query)) {
                searchList.add(task)
            }
            var adapter = Adapter(searchList,this, this@MainActivity)
            taskRecycler!!.layoutManager = LinearLayoutManager(applicationContext)
            if (adapter == null) {
                Toast.makeText(this, "rỗng", Toast.LENGTH_SHORT).show()
            } else {
                taskRecycler!!.adapter = adapter
            }
        }
    }
}