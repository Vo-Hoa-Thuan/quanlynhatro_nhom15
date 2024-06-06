package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityTaoPhongKhiThemKhuBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class ActivityTaoPhongKhiThemKhu : AppCompatActivity() {
    private lateinit var binding: ActivityTaoPhongKhiThemKhuBinding
    private lateinit var phongApiService: PhongApiService
    private lateinit var maKhuTro: String
    private var soPhongTro: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaoPhongKhiThemKhuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)

        // Nhận mã khu và số lượng phòng từ Intent
        maKhuTro = intent.getStringExtra(MA_KHU_TU_TAO_KHU) ?: ""
        soPhongTro = intent.getIntExtra(SO_LUONG_PHONG_KEY, 0)

        // Log để kiểm tra xem mã khu và số lượng phòng có được nhận đúng không
        Log.d("ActivityTaoPhongKhiThemKhu", "Mã khu: $maKhuTro, Số lượng phòng: $soPhongTro")

        binding.edSoPhongTro.isEnabled = false
        binding.edSoPhongTro.setTextColor(Color.BLACK)
        binding.edSoPhongTro.setText(soPhongTro.toString())

        binding.btnLuuThemPhong.setOnClickListener {
            val tenPhong = binding.edTenPhongTro.text.toString().trim()
            val soNguoiO = binding.edSoNguoiToiDa.text.toString().trim()
            val giaThue = binding.edGiaThue.text.toString().trim()
            val dienTich = binding.edDienTichPhong.text.toString().trim()

            if (soNguoiO.isEmpty() || giaThue.isEmpty() || dienTich.isEmpty()) {
                showErrorMessage("Vui lòng nhập đủ thông tin!!!")
            } else {
                val trangThaiPhong = 0
                repeat(soPhongTro) { index ->
                    val idPhong = UUID.randomUUID().toString()
                    val roomName = if (tenPhong.isNotEmpty()) tenPhong else "Phòng ${index + 1}"
                    val newPhong = Phong(
                        idPhong,
                        roomName,
                        dienTich.toInt(),
                        giaThue.toLong(),
                        soNguoiO.toInt(),
                        trangThaiPhong,
                        maKhuTro
                    )
                    insertNewPhong(newPhong)
                }
            }
        }
    }

    private fun insertNewPhong(phong: Phong) {
        val call = phongApiService.insertPhong(phong)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("ActivityTaoPhongKhiThemKhu", "Gửi yêu cầu thành công")
                    // Chuyển hướng người dùng đến màn hình chính chủ trọ
                    val intent = Intent(this@ActivityTaoPhongKhiThemKhu, ActivityManHinhChinhChuTro::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    showErrorMessage("Lưu thất bại")
                    Log.e("ActivityTaoPhongKhiThemKhu", "Lỗi từ máy chủ: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorMessage("Có lỗi xảy ra khi kết nối đến máy chủ")
                Log.e("ActivityTaoPhongKhiThemKhu", "Exception khi kết nối đến máy chủ: ${t.message}")
            }
        })
    }

    private fun showErrorMessage(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Lỗi")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
