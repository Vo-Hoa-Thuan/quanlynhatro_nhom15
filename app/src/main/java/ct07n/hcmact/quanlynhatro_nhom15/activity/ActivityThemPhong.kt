package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityThemPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
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
            val tenMacDinh = binding.edTenPhongTro.text.toString()
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

            // Gọi API để thêm phòng mới
            addNewRoom(phong)
        }

        binding.btnHuyThemPhong.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }

    override fun onBackPressed() {
        showCancelConfirmationDialog()
    }

    private fun addNewRoom(phong: Phong) {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        val call = phongApiService.insertPhong(phong)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    thongBaoThanhCong("Thêm phòng thành công")
                } else {
                    thongBaoLoi("Thêm phòng thất bại")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                thongBaoLoi("Lỗi khi thêm phòng: ${t.message}")
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