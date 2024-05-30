package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.api.*
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityKetThucHopDongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ActivityKetThucHopDong : AppCompatActivity() {
    private lateinit var binding: ActivityKetThucHopDongBinding
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val simpleDateFormatNow = SimpleDateFormat("yyyy-MM-dd")
    private var mYearNow = 0
    private var mDayNow = 0
    private var mMonthNow = 0
    private var tienDenBuHopDong = 100
    private var tienCocDenBu = 0
    private var tongTien = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKetThucHopDongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbKetThucHopDong)
        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        val c = Calendar.getInstance() as GregorianCalendar?
        mYearNow = (c as Calendar).get(Calendar.YEAR)
        mMonthNow = c.get(Calendar.MONTH)
        mDayNow = c.get(Calendar.DAY_OF_MONTH)
        val cNow = GregorianCalendar(mYearNow, mMonthNow, mDayNow)
        val intent = intent
        val hopDong: HopDong = intent.getSerializableExtra("hopDong") as HopDong

        loadTenPhong(hopDong.ma_phong)
//        loadTenKhuTro()
        binding.tvSoTienCocXuLyPhong.text = "${hopDong.tien_coc}"
        binding.chkThanhToanXuLy.isEnabled = false
        binding.chkKiemTraXuLyPhong.isEnabled = false

        if (hopDong.trang_thai_hop_dong == 0) {
            binding.tvThoiHanXuLyHopDong.text = "Hết hạn"
            binding.tvThoiHanXuLyHopDong.setTextColor(Color.RED)
            binding.tvNgayKetThucHopDong.text = chuyenDinhDangNgay(hopDong.ngay_hop_dong)
            binding.tvSoTienDenBuHopDong.text = "$tienCocDenBu"
        } else if (hopDong.trang_thai_hop_dong == 1) {
            binding.tvThoiHanXuLyHopDong.text = "Còn hạn"
            binding.tvThoiHanXuLyHopDong.setTextColor(Color.BLACK)
            binding.tvNgayKetThucHopDong.text = chuyenDinhDangNgay(simpleDateFormatNow.format(cNow.time))
            tienCocDenBu = hopDong.tien_coc
            binding.tvSoTienDenBuHopDong.text = "$tienCocDenBu"
        } else {
            binding.tvThoiHanXuLyHopDong.text = "Sắp hết hạn"
            binding.tvThoiHanXuLyHopDong.setTextColor(Color.BLUE)
            binding.tvNgayKetThucHopDong.text = chuyenDinhDangNgay(simpleDateFormatNow.format(cNow.time))
            binding.tvSoTienDenBuHopDong.text = "$tienCocDenBu"
        }

        binding.chkThietHai.setOnCheckedChangeListener { _, _ -> binding.layoutTienDenBu.isVisible = true }

        binding.btnKiemTraTaiSan.setOnClickListener {
            if (binding.chkThietHai.isChecked) {
                if (binding.edTienThietHai.text.toString().isNotBlank()) {
                    binding.tvTienThietHaiXuLyPhong.text = binding.edTienThietHai.text.toString()
                } else {
                    thongBaoLoi("Bạn cần điền số tiền thiệt hại trước khi kiểm tra!")
                    return@setOnClickListener
                }
            } else {
                binding.tvTienThietHaiXuLyPhong.text = "0"
            }
            binding.chkKiemTraXuLyPhong.isChecked = true
            binding.tvCongViecXuLyPhong.text = "1/2"
        }

        binding.btnDaThucHienXuLyPhong.setOnClickListener {
            binding.chkThanhToanXuLy.isChecked = true
            tongTien = if (hopDong.trang_thai_hop_dong == 0) {
                if (binding.chkThietHai.isChecked) {
                    hopDong.tien_coc - binding.tvTienThietHaiXuLyPhong.text.toString().toInt()
                } else {
                    hopDong.tien_coc - 0
                }
            } else if (hopDong.trang_thai_hop_dong == 1) {
                if (binding.chkThietHai.isChecked) {
                    tienCocDenBu + binding.tvTienThietHaiXuLyPhong.text.toString().toInt()
                } else {
                    tienCocDenBu + 0
                }
            } else {
                if (binding.chkThietHai.isChecked) {
                    hopDong.tien_coc - binding.tvTienThietHaiXuLyPhong.text.toString().toInt()
                } else {
                    hopDong.tien_coc - 0
                }
            }
            binding.tvTongTien.text = if (hopDong.trang_thai_hop_dong == 1) "Tiền cần thu của khách: " else "Tiền thanh toán cho khách: "
            binding.tvTienConLaiXuLyPhong.text = "$tongTien"
            binding.tvCongViecXuLyPhong.text = "2/2"
        }

        binding.btnXoaThongTinPhong.setOnClickListener {
            if (binding.chkKiemTraXuLyPhong.isChecked && binding.chkThanhToanXuLy.isChecked) {
                updateHD(hopDong)
            } else {
                thongBaoLoi("Hãy thực hiện đủ các thao tác để hoàn thành việc kết thúc hợp đồng!")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun loadTenPhong(maPhong: String) {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.getTenPhongById(maPhong).enqueue(object : Callback<PhongApiService.TenPhongResponse> {
            override fun onResponse(call: Call<PhongApiService.TenPhongResponse>, response: Response<PhongApiService.TenPhongResponse>) {
                if (response.isSuccessful) {
                    binding.tvTenPhongXuLyPhong.text = response.body().toString()
                } else {
                    thongBaoLoi("Không thể tải tên phòng")
                }
            }

            override fun onFailure(call: Call<PhongApiService.TenPhongResponse>, t: Throwable) {
                thongBaoLoi("Lỗi kết nối: ${t.message}")
            }
        })
    }

//    private fun loadTenKhuTro() {
//        val khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)
//        khuTroApiService.getTenKhuTro().enqueue(object : Callback<String> {
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                if (response.isSuccessful) {
//                    binding.tvTenKhuXuLyPhong.text = "Khu: ${response.body()}"
//                } else {
//                    thongBaoLoi("Không thể tải tên khu trọ")
//                }
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                thongBaoLoi("Lỗi kết nối: ${t.message}")
//            }
//
//        })
//    }

    private fun updateHD(hopDong: HopDong) {
        val hopDongNew = hopDong.copy(
            ngay_hop_dong = chuyenDinhDangNgayChuan(binding.tvNgayKetThucHopDong.text.toString()),
            trang_thai_hop_dong = 0,
            hieu_luc_hop_dong = 0
        )
        val hopDongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)
        hopDongApiService.updateHopDong(hopDong, hopDongNew).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    updatePhongAndNguoiDung(hopDong.ma_phong)
                } else {
                    thongBaoLoi("Không thể cập nhật hợp đồng")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                thongBaoLoi("Lỗi kết nối: ${t.message}")
            }
        })
    }

    private fun updatePhongAndNguoiDung(maPhong: String) {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.updateTrangThaiPhongThanhDaO(maPhong).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadNguoiDungTrongPhong(maPhong)
                } else {
                    thongBaoLoi("Không thể cập nhật trạng thái phòng")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                thongBaoLoi("Lỗi kết nối: ${t.message}")
            }
        })
    }

    private fun loadNguoiDungTrongPhong(maPhong: String) {
        val NguoidungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        NguoidungApiService.getNguoiDungByMaPhong(maPhong).enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    val listNDTrongPhong = response.body()!!
                    var count = 0
                    for (nguoiDung in listNDTrongPhong) {
                        updateNDTrongPhong(nguoiDung) {
                            count++
                            if (count == listNDTrongPhong.size) {
                                thongBaoThanhCong("Kết thúc hợp đồng thành công!")
                            }
                        }
                    }
                } else {
                    thongBaoLoi("Không thể tải người dùng trong phòng")
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                thongBaoLoi("Lỗi kết nối: ${t.message}")
            }
        })
    }

    private fun updateNDTrongPhong(nguoiDung: NguoiDung, onSuccess: () -> Unit) {
        val nguoiDungNew = nguoiDung.copy(trang_thai_chu_hop_dong = 0, trang_thai_o = 0)
        val NguoidungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        NguoidungApiService.update(nguoiDung.ma_nguoi_dung,nguoiDungNew).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    thongBaoLoi("Không thể cập nhật người dùng")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                thongBaoLoi("Lỗi kết nối: ${t.message}")
            }
        })
    }

    private fun chuyenDinhDangNgay(ngay: String): String {
        val sdfNgay = SimpleDateFormat("yyyy-MM-dd")
        val objDateNgayO = sdfNgay.parse(ngay)
        return DateFormat.format("dd/MM/yyyy", objDateNgayO).toString()
    }

    private fun chuyenDinhDangNgayChuan(text: String): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val objDate = sdf.parse(text.trim())
            DateFormat.format("yyyy-MM-dd", objDate).toString()
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    private fun thongBaoLoi(loi: String) {
        val bundle = androidx.appcompat.app.AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo Lỗi")
        bundle.setMessage(loi)
        bundle.setNegativeButton("OK") { dialog, _ -> dialog.cancel() }
        bundle.show()
    }

    private fun thongBaoThanhCong(loi: String) {
        val bundle = androidx.appcompat.app.AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo")
        bundle.setMessage(loi)
        bundle.setNegativeButton("OK") { dialog, _ -> finish() }
        bundle.show()
    }
}