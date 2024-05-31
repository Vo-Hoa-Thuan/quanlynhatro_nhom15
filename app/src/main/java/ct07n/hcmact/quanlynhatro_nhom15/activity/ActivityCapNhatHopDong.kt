package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.NguoiThueSpinnerAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityCapNhatHopDongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ActivityCapNhatHopDong : AppCompatActivity() {
    private lateinit var binding: ActivityCapNhatHopDongBinding
    var thoiHan = 0
    var mYear = 0
    var mMonth = 0
    var mDay = 0
    var mYearNow = 0
    var mMonthNow = 0
    var mDayNow = 0
    var mYear2 = 0
    var mMonth2 = 0
    var mDay2 = 0
    var mDateNgayO: Any? = null
    var listND = listOf<NguoiDung>()
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val simpleDateFormatNow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var maND = ""
    private var maKhu = ""
    private lateinit var hopDong: HopDong

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCapNhatHopDongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tbTaoHopDongMoi
        setSupportActionBar(binding.tbTaoHopDongMoi)
        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        //=================================================================
        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!
        hopDong = intent.getSerializableExtra("hopDong") as HopDong

        binding.edNgayBatDauO.setText(chuyenDinhDangNgay(hopDong.ngay_o))
        binding.edThoiHan.setText(hopDong.thoi_han.toString())
        binding.edNgayHetHan.setText(chuyenDinhDangNgay(hopDong.ngay_hop_dong))
        binding.edTienCoc.setText(hopDong.tien_coc.toString())
        binding.chkTrangThai.isChecked = hopDong.trang_thai_hop_dong == 1 || hopDong.trang_thai_hop_dong == 2
        binding.chkTrangThai.isClickable = false

        setupSpinner()
        setupListeners()
    }

    private fun setupSpinner() {
        val spinner = NguoiThueSpinnerAdapter(this, listND)
        binding.spinnerNguoiDung.adapter = spinner
        binding.spinnerNguoiDung.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                maND = listND[position].ma_nguoi_dung
                Toast.makeText(this@ActivityCapNhatHopDong, maND, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        var posND = 0
        for (i in listND.indices) {
            if (hopDong.ma_nguoi_dung == listND[i].ma_nguoi_dung) {
                posND = i
            }
        }
        binding.spinnerNguoiDung.setSelection(posND)
    }

    private fun setupListeners() {
        binding.edTenPhongTro.isEnabled = false
        binding.edNgayHetHan.isEnabled = false
        binding.edTenPhongTro.setTextColor(Color.BLACK)
        binding.edNgayHetHan.setTextColor(Color.BLACK)

        binding.imgCalendar.setOnClickListener { showDatePicker() }
        binding.edNgayBatDauO.setOnClickListener { showDatePicker() }
        binding.edThoiHan.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank() && binding.edNgayBatDauO.text.toString().isNotBlank()) {
                updateNgayHetHan()
            }
        }

        binding.btnLuuHopDong.setOnClickListener { saveHopDong() }
        binding.btnHuyHopDong.setOnClickListener { clearFields() }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance() as GregorianCalendar?
        mYear = c!!.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        val d = DatePickerDialog(
            this,
            0,
            mDateNgayO as DatePickerDialog.OnDateSetListener?,
            mYear,
            mMonth,
            mDay
        )
        d.show()
    }

    private fun updateNgayHetHan() {
        val stringOldDate = binding.edNgayBatDauO.text.toString()
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val newDate = dateFormat.parse(stringOldDate)
        val calendar = Calendar.getInstance()
        if (newDate != null) {
            calendar.time = newDate
        }
        val month = calendar.get(Calendar.MONTH) + binding.edThoiHan.text.toString().toInt()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val c1 = GregorianCalendar(year, month, day)
        binding.chkTrangThai.isChecked = true
        binding.edNgayHetHan.setText(simpleDateFormat.format(c1.time))
    }

    private fun saveHopDong() {
        if (validate() < 1) {
            showError("Dữ liệu không được để trống!")
            return
        }

        val thoiHan = binding.edThoiHan.text.toString().toInt()
        val ngayO = chuyenDinhDangNgay(binding.edNgayBatDauO.text.toString())

        // Create a Calendar instance and set it to the ngay_o date
        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormatNow.parse(ngayO)

        // Add the thoi_han (in months) to the ngay_o date
        calendar.add(Calendar.MONTH, thoiHan)

        // Format the new date back to a string
        val ngayHopDong = simpleDateFormatNow.format(calendar.time)
        val hopDongNew = HopDong(
            ma_hop_dong = hopDong.ma_hop_dong,
            ma_phong = hopDong.ma_phong,
            ma_nguoi_dung = hopDong.ma_nguoi_dung,
            thoi_han = thoiHan,
            ngay_o = ngayO,
            ngay_hop_dong = ngayHopDong,
            tien_coc = hopDong.tien_coc,
            anh_hop_dong = "default_image_link",
            trang_thai_hop_dong = if (binding.chkTrangThai.isChecked) 1 else 0,
            hieu_luc_hop_dong = 1,
            ngay_lap_hop_dong = simpleDateFormatNow.format(Date())
        )
        val hopDongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)
        hopDongApiService.updateHopDong(hopDong, hopDongNew)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        showSuccess("Bạn đã cập nhật thành công!")
                    } else {
                        showError("Bạn đã cập nhật không thành công!")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showError("Lỗi kết nối gia han: ${t.message}")
                }
            })
    }

//    private fun loadNguoiDung(maPhong: String) {
//        val NguoidungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
//        NguoidungApiService.getNguoiDungByMaPhong(maPhong).enqueue(object : Callback<List<NguoiDung>> {
//            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
//                if (response.isSuccessful) {
//                    listND = response.body() ?: listOf()
//                    setupSpinner()
//                } else {
//                    showError("Không thể tải người dùng")
//                }
//            }
//
//            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
//                showError("Lỗi kết nối nguoi dung: ${t.message}")
//            }
//        })
//    }

    private fun loadTenPhong(maPhong: String) {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.getTenPhongById(maPhong).enqueue(object : Callback<PhongApiService.TenPhongResponse> {
            override fun onResponse(call: Call<PhongApiService.TenPhongResponse>, response: Response<PhongApiService.TenPhongResponse>) {
                if (response.isSuccessful) {
//                    binding.edTenPhongTro.text = response.body()
                } else {
                    showError("Không thể tải tên phòng")
                }
            }

            override fun onFailure(call: Call<PhongApiService.TenPhongResponse>, t: Throwable) {
                showError("Lỗi kết nối ten phong: ${t.message}")
            }
        })
    }

    private fun chuyenDinhDangNgay(ngay: String): String {
        val sdfNgay = SimpleDateFormat("yyyy-MM-dd")
        val objDateNgayO = sdfNgay.parse(ngay)
        return android.text.format.DateFormat.format("dd/MM/yyyy", objDateNgayO) as String
    }

    private fun chuyenDinhDangNgayChuan(text: String): String {
        var ngayChuanDinhDang = ""
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val objDate = sdf.parse(text.trim())
            ngayChuanDinhDang = android.text.format.DateFormat.format("yyyy-MM-dd", objDate) as String
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ngayChuanDinhDang
    }

    private fun validate(): Int {
        var check = -1
        if (binding.edThoiHan.text.toString().isNotBlank() &&
            binding.edNgayBatDauO.text.toString().isNotBlank() &&
            binding.edTienCoc.text.toString().isNotBlank() &&
            binding.edNgayHetHan.text.toString().isNotBlank() &&
            binding.edTenPhongTro.text.toString().isNotBlank()
        ) {
            check = 1
        }
        return check
    }

    private fun clearFields() {
        binding.edThoiHan.setText("")
        binding.edNgayBatDauO.setText("")
        binding.edTienCoc.setText("")
        binding.edNgayHetHan.setText("")
    }

    private fun showError(message: String) {
        val bundle = androidx.appcompat.app.AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo Lỗi")
        bundle.setMessage(message)
        bundle.setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }
        bundle.show()
    }

    private fun showSuccess(message: String) {
        val bundle = androidx.appcompat.app.AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo")
        bundle.setMessage(message)
        bundle.setNegativeButton("OK") { _, _ -> finish() }
        bundle.setPositiveButton("Hủy") { dialog, _ -> dialog.cancel() }
        bundle.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}