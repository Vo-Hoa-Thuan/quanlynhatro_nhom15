package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityThemKhuTroBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val SO_LUONG_PHONG_KEY = "So_luong_phong"
const val MA_KHU_TU_TAO_KHU = "ma_khu_khi_tao_khu"

class ActivityThemKhuTro : AppCompatActivity() {
    private lateinit var binding: ActivityThemKhuTroBinding
    private lateinit var khuTroApiService: KhuTroApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemKhuTroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)

        val adminUsername = getAdminUsername()

        binding.btnTiepTuc.setOnClickListener {
            if (!validateInput()) {
                showErrorMessage("Vui lòng nhập đủ thông tin!!!")
            } else {
                val tenKhuTro = binding.edTenKhuTro.text.toString()
                val soLuongPhong = binding.edSoPhong.text.toString().toInt()
                val diaChi = binding.edDiaChi.text.toString()

                val newKhuTro = KhuTro(tenKhuTro, diaChi, soLuongPhong, adminUsername) // Thêm adminUsername vào đối tượng KhuTro

                createNewKhuTro(newKhuTro)
            }
        }
    }

    private fun getAdminUsername(): String {
        val sharedPreferences: SharedPreferences = getSharedPreferences(THONG_TIN_DANG_NHAP, MODE_PRIVATE)
        return sharedPreferences.getString(USERNAME_KEY, "") ?: ""
    }

    private fun validateInput(): Boolean {
        val tenKhuTro = binding.edTenKhuTro.text.toString()
        val soLuongPhong = binding.edSoPhong.text.toString()
        val diaChi = binding.edDiaChi.text.toString()

        return tenKhuTro.isNotBlank() && soLuongPhong.isNotBlank() && diaChi.isNotBlank()
    }

    private fun createNewKhuTro(khuTro: KhuTro) {
        val call = khuTroApiService.insertKhuTro(khuTro)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Giả sử bạn lưu trữ tạm thời mã khu trọ trong SharedPreferences
                    val sharedPreferences = getSharedPreferences("temp_prefs", MODE_PRIVATE)
                    sharedPreferences.edit().putString("latest_ma_khu_tro", khuTro.maKhuTro).apply()

                    val intent = Intent(this@ActivityThemKhuTro, ActivityTaoPhongKhiThemKhu::class.java)
                    intent.putExtra(MA_KHU_TU_TAO_KHU, khuTro.maKhuTro)
                    intent.putExtra(SO_LUONG_PHONG_KEY, khuTro.so_luong_phong)
                    startActivity(intent)
                    finish()
                } else {
                    showErrorMessage("Lưu thất bại")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorMessage("Có lỗi xảy ra khi kết nối đến máy chủ")
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
