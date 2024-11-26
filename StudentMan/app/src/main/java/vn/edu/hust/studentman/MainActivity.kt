package vn.edu.hust.studentman

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
  private val students = mutableListOf(
    StudentModel("Kha Minh Bao", "20210098"),
    StudentModel("Alice Johnson", "20211000"),
    StudentModel("Bob Smith", "20211001"),
    StudentModel("Charlie Davis", "20211002"),
    StudentModel("Diana Evans", "20211003"),
    StudentModel("Ethan Williams", "20211004"),
    StudentModel("Fiona Brown", "20211005"),
    StudentModel("George Harris", "20211006"),
    StudentModel("Hannah Clark", "20211007"),
    StudentModel("Isaac Martin", "20211008"),
  )


  private val studentAdapter = StudentAdapter(students, this::editStudent, this::deleteStudent)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<RecyclerView>(R.id.recycler_view_students).run {
      adapter = studentAdapter
      layoutManager = LinearLayoutManager(this@MainActivity)
    }

    findViewById<Button>(R.id.btn_add_new).setOnClickListener {
      openStudentInputDialog()
    }
  }

  private fun openStudentInputDialog() {
    val dialogView = LayoutInflater.from(this@MainActivity)
      .inflate(R.layout.layout_alert_dialog, null)

    val editHoten = dialogView.findViewById<EditText>(R.id.edit_hoten)
    val editMssv = dialogView.findViewById<EditText>(R.id.edit_mssv)

    AlertDialog.Builder(this)
      .setTitle("Add Student")
      .setView(dialogView)
      .setPositiveButton("OK") { _, _ ->
        val hoten = editHoten.text.toString()
        val mssv = editMssv.text.toString()
        if (hoten.isNotEmpty() && mssv.isNotEmpty()) {
          addStudent(StudentModel(hoten, mssv))
        }
      }
      .setNegativeButton("Cancel", null)
      .create().show()
  }

  private fun addStudent(student: StudentModel) {
    students.add(student)
    studentAdapter.notifyItemInserted(students.size - 1)
  }

  private fun editStudent(position: Int) {
    val student = students[position]

    val dialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_alert_dialog, null)

    val editHoten = dialogView.findViewById<EditText>(R.id.edit_hoten)
    val editMssv = dialogView.findViewById<EditText>(R.id.edit_mssv)

    editHoten.setText(student.studentName)
    editMssv.setText(student.studentId)

    AlertDialog.Builder(this)
      .setTitle("Nhap thong tin sinh vien")
      .setView(dialogView)
      .setPositiveButton("OK") { _, _ ->
        val newHoten = editHoten.text.toString()
        val newMssv = editMssv.text.toString()
        if (newHoten.isNotEmpty() && newMssv.isNotEmpty()) {
          students[position] = StudentModel(newHoten, newMssv)
          studentAdapter.notifyItemChanged(position)
        }
      }
      .setNegativeButton("Cancel", null)
      .create()
      .show()
  }

  private fun deleteStudent(position: Int) {
    val deletedStudent = students[position]
    AlertDialog.Builder(this)
      .setTitle("Delete Student")
      .setMessage("Are you sure you want to delete this student?")
      .setPositiveButton("Delete") { _, _ ->
        students.removeAt(position)
        studentAdapter.notifyItemRemoved(position)

        Snackbar.make(findViewById(R.id.recycler_view_students),
          "Đã xóa sinh viên: ${deletedStudent.studentName}",
          Snackbar.LENGTH_LONG)
          .setAction("Undo") {
            students.add(position, deletedStudent)
            studentAdapter.notifyItemInserted(position)
          }
          .show()
      }
      .setNegativeButton("Cancel", null)
      .create()
      .show()
  }
}