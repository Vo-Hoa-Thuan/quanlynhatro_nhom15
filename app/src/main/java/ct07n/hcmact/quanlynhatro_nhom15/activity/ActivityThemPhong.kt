package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityThemPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class ActivityThemPhong : AppCompatActivity() {
    private lateinit var binding: ActivityThemPhongBinding
    private var maKhu = ""
    private var maPhong = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityThemPhongBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.tbThemPhong)
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.black_left)
            setDisplayHomeAsUpEnabled(true)
        }

        maPhong = UUID.randomUUID().toString()
        val srf = getSharedPreferences(FILE_NAME, MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!

        binding.btnLuuThemPhong.setOnClickListener {
            fetchRoomCountAndAddRoom()
        }

        binding.btnHuyThemPhong.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }

    override fun onBackPressed() {
        showCancelConfirmationDialog()
    }

    private fun fetchRoomCountAndAddRoom() {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.demSoPhong(maKhu).enqueue(object : Callback<PhongApiService.SoPhongResponse> {
            override fun onResponse(call: Call<PhongApiService.SoPhongResponse>, response: Response<PhongApiService.SoPhongResponse>) {
                if (response.isSuccessful) {
                    val roomCount = response.body()?.soPhong ?: 0
                    addNewRoom(roomCount + 1)
                } else {
                    thongBaoLoi("Không thể lấy tổng số phòng.")
                }
            }

            override fun onFailure(call: Call<PhongApiService.SoPhongResponse>, t: Throwable) {
                thongBaoLoi("Lỗi khi lấy tổng số phòng: ${t.message}")
            }
        })
    }

    private fun addNewRoom(nextRoomNumber: Int) {
        val tenMacDinh = "Phòng $nextRoomNumber"
        val dienTich = binding.edDienTichPhong.text.toString().toIntOrNull() ?: 0
        val giaThue = binding.edGiaThue.text.toString().toLongOrNull() ?: 0
        val soNguoiToiDa = binding.edSoNguoiOToiDa.text.toString().toIntOrNull() ?: 0

        val phong = Phong(
            ma_phong = maPhong,
            ten_phong = tenMacDinh,
            dien_tich = dienTich,
            gia_thue = giaThue,
            so_nguoi_o = soNguoiToiDa,
            trang_thai_phong = 0,
            ma_khu_tro = maKhu
        )

        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        val call = phongApiService.insertPhong(phong)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    updateRoomCount()
                } else {
                    thongBaoLoi("Thêm phòng thất bại")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                thongBaoLoi("Lỗi khi thêm phòng: ${t.message}")
            }
        })
    }

    private fun updateRoomCount() {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        val call = phongApiService.updateSoLuongPhongByMaKhu(maKhu)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    thongBaoThanhCong("Thêm phòng thành công")
                } else {
                    thongBaoLoi("Cập nhật số lượng phòng thất bại")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                thongBaoLoi("Lỗi khi cập nhật số lượng phòng: ${t.message}")
            }
        })
    }

    private fun showCancelConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận")
        builder.setMessage("Bạn có muốn hủy bỏ thêm phòng không? Dữ liệu bạn nhập sẽ không được lưu lại.")
        builder.setPositiveButton("Đồng ý") { _, _ ->
            finish()
        }
        builder.setNegativeButton("Không", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun thongBaoLoi(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Lỗi")
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun thongBaoThanhCong(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thành công")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ ->
            finish()
        }
        val dialog = builder.create()
        dialog.show()
    }
}
