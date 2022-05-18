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

//ánh xạ ID - Thay thế  findViewById<>()
class MainActivity : AppCompatActivity(), CreateTaskBottomSheetFragment.SetRefreshListener{
    private lateinit var database: myDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)    //ánh xạ đến giao diện nào (layout)

//      cài đặt giao diện cho  ActionBar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.toolbar)

        //Load hình ảnh - import thêm thư viện trong Gradle
        Glide.with(applicationContext).load(R.drawable.new_todo).into(noDataImage!!)

        //khởi tạo và đỗ dữ liệu
        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "To_Do"
        ).build()
        GlobalScope.launch {
            DataObject.listdata = database.dao().getTasks() as MutableList<TaskModel>
            setRecycler()
        }

        //Lắng nghe sự kiện cho button
        btnAddTask.setOnClickListener {view: View? ->
            //khởi tạo và truyền dữ liệu cho dialog
            val createTaskBottomSheetFragment = CreateTaskBottomSheetFragment()
            createTaskBottomSheetFragment.setTaskItem(0, false, this, this@MainActivity)
            //hiển thị dialog
            createTaskBottomSheetFragment.show(
                supportFragmentManager,
                createTaskBottomSheetFragment.tag
            )
        }

    }

    //quản lý giao diện hiển thị(dọc/ngang), mặc định là dọc và đỗ dữ liệu cho danh sách(Recycler)
    fun setRecycler() {
        noDataImage!!.visibility = if (DataObject.getAllData().isEmpty()) View.VISIBLE else View.GONE
        val linearLayoutManager = LinearLayoutManager(applicationContext) // 1 cột
        // add vào đầu danh sách
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        taskRecycler.adapter = Adapter(DataObject.getAllData(), this, this@MainActivity)
        taskRecycler.layoutManager = linearLayoutManager
    }

    //reset lại dữ liệu/giao diện
    override fun refresh() {
        setRecycler()
    }

    //menu trên - Search
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        val menuItem = menu.findItem(R.id.action_search)

        //khởi tạo đối tượng kiểu SearchManager
        val searchManager = this.getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.componentName))

        //Cấu hình
        val theTextArea = searchView.findViewById<View>(R.id.search_src_text) as SearchView.SearchAutoComplete
        theTextArea.setTextColor(resources.getColor(R.color.colorAccent)) //or any color that you want
        theTextArea.setHintTextColor(resources.getColor(R.color.colorAccent))
        //định dạng màu
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        searchIcon.setColorFilter(resources.getColor(R.color.colorAccent))
        val searchIconClose =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchIconClose.setColorFilter(resources.getColor(R.color.colorAccent))

        //lắng nghe sự kiện
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                startSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        //Xóa văn bản khi nhấp vào nút Xóa(X) trên thanh tìm kiếm
        val closeButton = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener { v: View? ->
            val ed = searchView.findViewById<View>(R.id.search_src_text) as EditText
            //Xóa văn bản
            ed.setText("")
            //Xóa truy vấn
            searchView.setQuery("", false)
            //thu gọn lại chế độ xem
            searchView.onActionViewCollapsed()
            //Thu gọn tiện ích tìm kiếm
            menuItem.collapseActionView()
            //Khôi phục kết quả về bản gốc
            setRecycler()
        }
        return super.onCreateOptionsMenu(menu)
    }

    //hiển thị dialog Lịch
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_calendar) {
            //Khởi tạo dialog
            val showCalendarViewBottomSheet = ShowCalendarViewBottomSheet()
            //hiển thị dialog
            showCalendarViewBottomSheet.show(
                supportFragmentManager,
                showCalendarViewBottomSheet.tag
            )
        }
        return super.onOptionsItemSelected(item)
    }

    //Tìm kiếm dữ liệu trong danh sách sử dụng vòng lặp for-each
    private fun startSearch(query: String) {
        //tạo danh sách mới chứa dữ liệu cần tìm
        val searchList: MutableList<TaskModel> = ArrayList()
        for (i in DataObject.getAllData().indices) {
            val task = DataObject.getAllData()[i]
            if (task.taskTitle.toLowerCase().contains(query)) {
                //thêm dữ liệu tìm được vào danh sách
                searchList.add(task)
            }
            //hiển thị lên giao diện (recyclerview)
            var adapter = Adapter(searchList,this, this@MainActivity)
            taskRecycler!!.layoutManager = LinearLayoutManager(applicationContext)
            if (adapter == null) {
                Toast.makeText(this, "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show()
            } else {
                taskRecycler!!.adapter = adapter
            }
        }
    }
}