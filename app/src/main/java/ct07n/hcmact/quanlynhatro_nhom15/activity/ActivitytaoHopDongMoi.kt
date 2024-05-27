package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.NguoiThueSpinnerAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityActivitytaoHopDongMoiBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogThemKhachThueHopDongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ActivitytaoHopDongMoi : AppCompatActivity() {
    private lateinit var binding: ActivityActivitytaoHopDongMoiBinding
    private lateinit var maKhu: String
    private lateinit var maPhong: String
    private lateinit var tenPhong: String
    private var check: String = ""
    private var maND: String = ""
    private var listND: List<NguoiDung> = listOf()
    private lateinit var srf: SharedPreferences
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val simpleDateFormatNow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivitytaoHopDongMoiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbTaoHopDongMoi)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.black_left)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        srf = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!

        intent.extras?.let { bundle ->
            tenPhong = bundle.getString("tenPhong").toString()
            maPhong = bundle.getString("maPhong").toString()
            loadNguoiDung(maPhong)
            binding.edTenPhongTro.setText(tenPhong)
        }
        val NguoidungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        val i = intent
        val bundle = i.extras
        if (bundle != null) {
            NguoidungApiService.getListNguoiDungByMaPhong(maPhong).enqueue(object : Callback<List<NguoiDung>> {
                override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                    if (response.isSuccessful) {
                        listND = response.body() ?: emptyList()
                        binding.edTenPhongTro.setText(tenPhong)
                        val spinner = NguoiThueSpinnerAdapter(this@ActivitytaoHopDongMoi, listND)
                        binding.spinnerNguoiDung.adapter = spinner
                        binding.spinnerNguoiDung.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                maND = listND[position].ma_nguoi_dung
                                Toast.makeText(this@ActivitytaoHopDongMoi, maND, Toast.LENGTH_SHORT).show()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                Toast.makeText(this@ActivitytaoHopDongMoi, "Sai", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                    Toast.makeText(this@ActivitytaoHopDongMoi, "Sai", Toast.LENGTH_SHORT).show()
                }
            })
        }

        if (listND.isEmpty()) {
            binding.tvThemNguoiThue.isVisible = true
            binding.tvThemNguoiThue.setOnClickListener {
                val dialog = DialogThemKhachThueHopDongBinding.inflate(LayoutInflater.from(this))
                val build = AlertDialog.Builder(this).create()
                dialog.edTenPhong.setText(tenPhong)
                dialog.btnLuuThemNguoiDung.setOnClickListener {
                    val maNguoiDung = UUID.randomUUID().toString()
                    maND = maNguoiDung
                    val nguoiDung = NguoiDung(
                        ma_nguoi_dung = maNguoiDung,
                        ho_ten_nguoi_dung = dialog.edHoTenThemNguoiDung.text.toString(),
                        cccd = dialog.edCCCDThemNguoiDung.text.toString(),
                        nam_sinh = dialog.edNgaySinhThemNguoiDung.text.toString(),
                        que_quan = dialog.edQueQuanThemNguoiDung.text.toString(),
                        sdt_nguoi_dung = dialog.edSDTThemNguoiDung.text.toString(),
                        ma_phong = maPhong,trang_thai_o = 1,
                        trang_thai_chu_hop_dong = 0,
                    )
                    NguoidungApiService.insert(nguoiDung).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                onResume()
                                onPause()
                                build.dismiss()
                                check = "1";
                                Toast.makeText(this@ActivitytaoHopDongMoi, "Thêm người dùng thành công", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@ActivitytaoHopDongMoi, "Thêm người dùng không thành công", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@ActivitytaoHopDongMoi, "Không kết nối được với cơ sở dữ liệu", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                dialog.btnHuyThemNguoiDung.setOnClickListener {
                    build.dismiss()
                    onResume()
                    onPause()
                }
                build.setView(dialog.root)
                build.show()
            }
        }
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        binding.edTenPhongTro.isEnabled = false
        binding.edNgayHetHan.isEnabled = false
        binding.edTenPhongTro.setTextColor(Color.BLACK)
        binding.edNgayHetHan.setTextColor(Color.BLACK)
    }

    private fun setupListeners() {
        binding.imgCalendar.setOnClickListener { showDatePicker() }
        binding.edNgayBatDauO.setOnClickListener { showDatePicker() }

        binding.edThoiHan.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank() && binding.edNgayBatDauO.text.toString().isNotBlank()) {
                val date = simpleDateFormat.parse(binding.edNgayBatDauO.text.toString())
                val calendar = Calendar.getInstance().apply { time = date }
                calendar.add(Calendar.MONTH, text.toString().toInt())
                binding.edNgayHetHan.setText(simpleDateFormat.format(calendar.time))
            }
        }

        binding.chkTrangThai.isChecked = true
        binding.chkTrangThai.isClickable = false

        binding.btnLuuHopDong.setOnClickListener {
            if (validateInputs()) {
                if (check == "") {
                    showErrorDialog("Hãy thêm người dùng vào phòng để hoàn thành hợp đồng!")
                } else {
                    createHopDong()
                }
            } else {
                showErrorDialog("Dữ liệu không được để trống!")
            }
        }

        binding.btnHuyHopDong.setOnClickListener { clearFields() }
    }

    private fun loadNguoiDung(maPhong: String) {
        val service = RetrofitClient.instance.create(NguoidungApiService::class.java)
        val call = service.getNguoiDungByMaPhong(maPhong)
        call.enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    listND = response.body() ?: listOf()
                    setupNguoiThueSpinner()
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                showErrorDialog("Failed to load residents")
            }
        })
    }

    private fun setupNguoiThueSpinner() {
        val spinnerAdapter = NguoiThueSpinnerAdapter(this, listND)
        binding.spinnerNguoiDung.adapter = spinnerAdapter
        binding.spinnerNguoiDung.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                maND = listND[position].ma_nguoi_dung
                Toast.makeText(this@ActivitytaoHopDongMoi, maND, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                binding.edNgayBatDauO.setText(simpleDateFormat.format(selectedDate.time))
                if (binding.edThoiHan.text.toString().isNotBlank()) {
                    selectedDate.add(Calendar.MONTH, binding.edThoiHan.text.toString().toInt())
                    binding.edNgayHetHan.setText(simpleDateFormat.format(selectedDate.time))
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateInputs(): Boolean {
        return true
    //        binding.edThoiHan.text.isNotBlank() &&
//                binding.edNgayBatDauO.text.isNotBlank() &&
//                binding.edTienCoc.text.isNotBlank() &&
//                binding.edNgayHetHan.text.isNotBlank() &&
//                binding.edTenPhongTro.text.isNotBlank()
    }

    private fun createHopDong() {
        val thoiHan = binding.edThoiHan.text.toString().toInt()
        val ngayO = chuyenDinhDangNgay(binding.edNgayBatDauO.text.toString())

        // Create a Calendar instance and set it to the ngay_o date
        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormatNow.parse(ngayO)

        // Add the thoi_han (in months) to the ngay_o date
        calendar.add(Calendar.MONTH, thoiHan)

        // Format the new date back to a string
        val ngayHopDong = simpleDateFormatNow.format(calendar.time)

        val hopDong = HopDong(
            ma_hop_dong = UUID.randomUUID().toString(),
            ma_phong = maPhong,
            ma_nguoi_dung = maND,
            thoi_han = thoiHan,
            ngay_o = ngayO,
            ngay_hop_dong = ngayHopDong,
            tien_coc = binding.edTienCoc.text.toString().toInt(),
            anh_hop_dong = "default_image_link",
            trang_thai_hop_dong = if (binding.chkTrangThai.isChecked) 1 else 0,
            hieu_luc_hop_dong = 1,
            ngay_lap_hop_dong = simpleDateFormatNow.format(Date())
        )

        val service = RetrofitClient.instance.create(HopdongApiService::class.java)
        val call = service.insertHopDong(hopDong)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val phongService = PhongApiService.getInstance()
                    phongService.updateTrangThaiPhongThanhDangO(hopDong.ma_phong).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                showSuccessDialog("Hợp đồng được tạo thành công và trạng thái phòng đã được cập nhật!")
                            } else {
                                showErrorDialog("Cập nhật trạng thái phòng không thành công!")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            showErrorDialog("Lỗi kết nối khi cập nhật trạng thái phòng!")
                        }
                    })
                } else {
                    showErrorDialog("Tạo hợp đồng không thành công!")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorDialog("Lỗi kết nối!")
            }
        })
    }

    private fun chuyenDinhDangNgay(date: String): String {
        return try {
            val parsedDate = simpleDateFormat.parse(date)
            simpleDateFormatNow.format(parsedDate)
        } catch (e: Exception) {
            ""
        }
    }

    private fun clearFields() {
//        binding.edThoiHan.text.clear()
//        binding.edNgayBatDauO.text.clear()
//        binding.edTienCoc.text.clear()
//        binding.edNgayHetHan.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Lỗi")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Thành công")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> finish() }
            .setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNguoiDung(maPhong)
    }
}