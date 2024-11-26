package com.anbinh.submitform
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.studentform.R
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var selectedDOB: TextView
    private lateinit var spinnerCity: Spinner
    private lateinit var spinnerDistrict: Spinner
    private lateinit var spinnerWard: Spinner

    private var dobSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        val etStudentId = findViewById<EditText>(R.id.etStudentId)
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhoneNumber = findViewById<EditText>(R.id.etPhoneNumber)
        selectedDOB = findViewById(R.id.tvSelectedDOB)
        spinnerCity = findViewById(R.id.spinnerCity)
        spinnerDistrict = findViewById(R.id.spinnerDistrict)
        spinnerWard = findViewById(R.id.spinnerWard)

        val btnSelectDOB = findViewById<Button>(R.id.btnSelectDOB)
        btnSelectDOB.setOnClickListener { showDatePickerDialog() }

        // Set up spinners and submit button
        setupCitySpinner()
        setupSubmitButton(etStudentId, etName, etEmail, etPhoneNumber)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            selectedDOB.text = formattedDate
            dobSelected = true
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun setupCitySpinner() {
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, LocationData.cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity.adapter = cityAdapter

        // Initially hide the district and ward spinners
        spinnerDistrict.visibility = View.GONE
        spinnerWard.visibility = View.GONE

        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCity = LocationData.cities[position]

                if (selectedCity != "Chọn thành phố") {
                    setupDistrictSpinner(selectedCity)
                    spinnerDistrict.visibility = View.VISIBLE
                } else {
                    spinnerDistrict.visibility = View.GONE
                    spinnerWard.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupDistrictSpinner(city: String) {
        val districts = LocationData.districtsByCity[city] ?: emptyList()
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Chọn quận/huyện") + districts)
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDistrict.adapter = districtAdapter

        spinnerWard.visibility = View.GONE

        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDistrict = if (position > 0) districts[position - 1] else "Chọn quận/huyện"

                if (selectedDistrict != "Chọn quận/huyện") {
                    setupWardSpinner(selectedDistrict)
                    spinnerWard.visibility = View.VISIBLE
                } else {
                    spinnerWard.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupWardSpinner(district: String) {
        val wards = LocationData.wardsByDistrict[district] ?: emptyList()
        val wardAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Chọn phường/xã") + wards)
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWard.adapter = wardAdapter
    }

    private fun setupSubmitButton(
        etStudentId: EditText,
        etName: EditText,
        etEmail: EditText,
        etPhoneNumber: EditText
    ) {
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            val studentId = etStudentId.text.toString().trim()
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val selectedCity = spinnerCity.selectedItem.toString()
            val selectedDistrict = if (spinnerDistrict.visibility == View.VISIBLE) spinnerDistrict.selectedItem.toString() else ""
            val selectedWard = if (spinnerWard.visibility == View.VISIBLE) spinnerWard.selectedItem.toString() else ""

            // Validate required fields
            when {
                studentId.isEmpty() -> etStudentId.error = "MSSV không được để trống"
                name.isEmpty() -> etName.error = "Họ và tên không được để trống"
                email.isEmpty() -> etEmail.error = "Email không được để trống"
                phoneNumber.isEmpty() -> etPhoneNumber.error = "Số điện thoại không được để trống"
                !dobSelected -> selectedDOB.error = "Vui lòng chọn ngày sinh"
                selectedCity == "Chọn thành phố" -> Toast.makeText(this, "Vui lòng chọn thành phố", Toast.LENGTH_SHORT).show()
                selectedDistrict == "Chọn quận/huyện" -> Toast.makeText(this, "Vui lòng chọn quận/huyện", Toast.LENGTH_SHORT).show()
                selectedWard == "Chọn phường/xã" -> Toast.makeText(this, "Vui lòng chọn phường/xã", Toast.LENGTH_SHORT).show()
                else -> {
                    // If all fields are valid
                    Toast.makeText(this, "Form submitted successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

