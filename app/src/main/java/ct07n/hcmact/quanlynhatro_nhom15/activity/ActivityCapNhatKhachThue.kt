package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.MaPhongSpinnerAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityCapNhatKhachThueBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityCapNhatKhachThue : AppCompatActivity() {
    private var maKhu = ""
    private var maPhong = ""
    private var maPhongCu = ""
    private lateinit var binding: ActivityCapNhatKhachThueBinding

    private val nguoidungApiService: NguoidungApiService by lazy {
        NguoidungApiService.getInstance()
    }

    private val phongApiService: PhongApiService by lazy {
        PhongApiService.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCapNhatKhachThueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbCapNhatKhachThue)
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.black_left)
            setDisplayHomeAsUpEnabled(true)
        }

        val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = sharedPreferences.getString(MA_KHU_KEY, "")!!


        val intent = intent
        val nguoiDung: NguoiDung = intent.getSerializableExtra("khachThue") as NguoiDung
        maPhongCu = nguoiDung.ma_phong

        binding.edHoTenSuaNguoiDung.setText(nguoiDung.ho_ten_nguoi_dung)
        binding.edCCCDSuaNguoiDung.setText(nguoiDung.cccd)
        binding.edNgaySinhSuaNguoiDung.setText(nguoiDung.nam_sinh)
        binding.edQueQuanSuaNguoiDung.setText(nguoiDung.que_quan)
        binding.edSDTSuaNguoiDung.setText(nguoiDung.sdt_nguoi_dung)

        Log.d("ActivityCapNhatKhachThue", "Fetching rooms for maKhu: $maKhu")

        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    val listPhong = response.body() ?: emptyList()
                    if (listPhong.isNotEmpty()) {
                        val spinnerAdapter = MaPhongSpinnerAdapter(this@ActivityCapNhatKhachThue, listPhong)
                        binding.spinnerSuaNguoiDung.adapter = spinnerAdapter
                        binding.spinnerSuaNguoiDung.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                maPhong = listPhong[position].ma_phong
                                Toast.makeText(this@ActivityCapNhatKhachThue, "Tên phòng: " + listPhong[position].ten_phong, Toast.LENGTH_SHORT).show()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    } else {
                        Toast.makeText(this@ActivityCapNhatKhachThue, "Không có phòng nào khả dụng", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch rooms. Response code: ${response.code()}. Error body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ActivityCapNhatKhachThue, "Không thể tải danh sách phòng", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch rooms due to: ${t.message}", t)
            }
        })

        nguoidungApiService.getMaNguoiDangOByMaPhong(nguoiDung.ma_phong).enqueue(object : Callback<NguoidungApiService.MaNguoiDangOResponse> {
            override fun onResponse(call: Call<NguoidungApiService.MaNguoiDangOResponse>, response: Response<NguoidungApiService.MaNguoiDangOResponse>) {
                if (response.isSuccessful) {
                    val maNguoiDangO = response.body()?.ma_nguoi_dang_o
                    Log.d("ActivityCapNhatKhachThue", "Current resident ID: $maNguoiDangO")
                    if (nguoiDung.ma_nguoi_dung != maNguoiDangO) {
                        binding.spinnerSuaNguoiDung.visibility = View.VISIBLE
                    } else {
                        binding.spinnerSuaNguoiDung.visibility = View.VISIBLE
                        binding.spinnerSuaNguoiDung.isEnabled = false
                        binding.chkTrangThaiKhachThue.isEnabled = true
                    }
                } else {
                    Log.d("ActivityCapNhatKhachThue", "Failed to get current resident ID: ${response.message()}")
                    thongBaoLoi("Không thể xác định mã người đang ở")
                }
            }

            override fun onFailure(call: Call<NguoidungApiService.MaNguoiDangOResponse>, t: Throwable) {
                Log.d("ActivityCapNhatKhachThue", "Failed to get current resident ID due to: ${t.message}")
            }
        })

        binding.btnLuuSuaNguoiDung.setOnClickListener {
            if (binding.chkTrangThaiKhachThue.isChecked) {
                nguoidungApiService.updateTrangThaiNguoiDungThanhDaO(nguoiDung.ma_phong).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("ActivityCapNhatKhachThue", "Updated tenant status successfully")
                                nguoidungApiService.getListNguoiDungByMaPhong(maPhongCu).enqueue(object : Callback<List<NguoiDung>> {
                                    override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                                        if (response.isSuccessful) {
                                            val listNguoiDung = response.body() ?: emptyList()
                                            Log.d("ActivityCapNhatKhachThue", "Fetched tenant list: $listNguoiDung")
                                            if (listNguoiDung.isEmpty()) {

                                                phongApiService.updateTrangThaiPhongThanhDaO(maPhongCu).enqueue(object : Callback<Void> {
                                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                                        if (response.isSuccessful) {
                                                            Log.d("ActivityCapNhatKhachThue", "Updated room status successfully")
                                                        } else {
                                                            Log.d("ActivityCapNhatKhachThue", "Failed to update room status: ${response.message()}")
                                                            thongBaoLoi("Cập nhật trạng thái phòng không thành công")
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                                        Log.d("ActivityCapNhatKhachThue", "Failed to update room status due to: ${t.message}")
                                                        thongBaoLoi("Lỗi kết nối: ${t.message}")
                                                    }
                                                })

                                                phongApiService.deleteHopDongIfPhongMaZero(maPhongCu).enqueue(object : Callback<Void> {
                                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                                        if (response.isSuccessful) {
                                                            Log.d("ActivityCapNhatKhachThue", "Updated hop dong status successfully")
                                                        } else {
                                                            thongBaoLoi("Lỗi")
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                                        Log.d("ActivityCapNhatKhachThue", "Failed to update room status due to: ${t.message}")
                                                        thongBaoLoi("Lỗi kết nối: ${t.message}")
                                                    }
                                                })
                                            }
                                        } else {
                                            Log.d("ActivityCapNhatKhachThue", "Failed to fetch tenant list: ${response.message()}")
                                            thongBaoLoi("Không thể lấy danh sách người dùng")
                                        }
                                    }

                                    override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                                        Log.d("ActivityCapNhatKhachThue", "Failed to fetch tenant list due to: ${t.message}")
                                        thongBaoLoi("Lỗi kết nối: ${t.message}")
                                    }
                                })
                            } else {
                                Log.d("ActivityCapNhatKhachThue", "Failed to update tenant status: ${response.message()}")
                                thongBaoLoi("Cập nhật không thành công")
                            }
                        thongBaoXoa("Đã xóa thành công")
                        finish()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("ActivityCapNhatKhachThue", "Failed to update tenant status due to: ${t.message}")
                        thongBaoLoi("Lỗi kết nối: ${t.message}")
                    }
                })
            } else {
                if (binding.edHoTenSuaNguoiDung.text.toString().isNotBlank() && binding.edNgaySinhSuaNguoiDung.text.toString().isNotBlank() &&
                    binding.edQueQuanSuaNguoiDung.text.toString().isNotBlank() && binding.edSDTSuaNguoiDung.text.toString().isNotBlank() &&
                    binding.edCCCDSuaNguoiDung.text.toString().isNotBlank()) {

                    val nguoiDungUpdate = NguoiDung(
                        nguoiDung.ma_nguoi_dung,
                        binding.edHoTenSuaNguoiDung.text.toString(),
                        binding.edNgaySinhSuaNguoiDung.text.toString(),
                        binding.edSDTSuaNguoiDung.text.toString(),
                        binding.edCCCDSuaNguoiDung.text.toString(),
                        binding.edQueQuanSuaNguoiDung.text.toString(),
                        maPhong,
                        nguoiDung.trang_thai_chu_hop_dong,
                        nguoiDung.trang_thai_o
                    )

                    nguoidungApiService.update(nguoiDung.ma_nguoi_dung, nguoiDungUpdate).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("ActivityCapNhatKhachThue", "Updated user info successfully")
                                thongBaoLoi("Cập nhật thành công")
                                val intent = Intent(this@ActivityCapNhatKhachThue, ActivityDanhSachNguoiThue::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.d("ActivityCapNhatKhachThue", "Failed to update user info: ${response.message()}")
                                thongBaoLoi("Cập nhật không thành công")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d("ActivityCapNhatKhachThue", "Failed to update user info due to: ${t.message}")
                            thongBaoLoi("Lỗi kết nối: ${t.message}")
                        }
                    })
                } else {
                    thongBaoLoi("Vui lòng điền đầy đủ thông tin")
                }
            }
        }

        binding.btnHuySuaNguoiDung.setOnClickListener {
            finish()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun thongBaoLoi(message: String) {
        Log.d("ActivityCapNhatKhachThue", "Error: $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun thongBaoXoa(message: String) {
        Log.d("ActivityCapNhatKhachThue", "Delete Confirmation: $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
