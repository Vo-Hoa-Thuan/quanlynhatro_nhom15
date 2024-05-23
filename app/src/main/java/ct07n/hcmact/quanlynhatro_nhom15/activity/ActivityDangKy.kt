package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityDangNhap
import ct07n.hcmact.quanlynhatro_nhom15.api.AdminApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityDangKyBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Admin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ActivityDangKy : AppCompatActivity() {
    private lateinit var binding: ActivityDangKyBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangKyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var adminApiService = RetrofitClient.instance.create(AdminApiService::class.java)

        setSupportActionBar(binding.tbDangKy)
        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(ct07n.hcmact.quanlynhatro_nhom15.R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        binding.btnLuuDK.setOnClickListener {
            if (validate() < 1) {
                Snackbar.make(it, "Dữ liệu không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val admin = Admin(
                    sdt = binding.edSoDienThoai.text.toString(),
                    ten_dang_nhap = binding.edTenDangNhapDangKy.text.toString(),
                    ho_ten = binding.edHoVaTen.text.toString(),
                    stk = "",
                    ngan_hang = "",
                    ngay_sinh = binding.edNgaySinh.text.toString(),
                    mat_khau = binding.edMatKhauDangKy.text.toString()
                )
                // Logging thông tin admin trước khi gửi lên server
                Log.d("Admin Info", "Admin: $admin")

                adminApiService.insertAdmin(admin).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            thongBaoThanhCong("Bạn đã tạo thành công tài khoản mới !!!")
                            xoaTrang()
                        } else {
                            thongBaoLoi("Đăng ký không thành công: ${response.message()}")
                            Log.e("API_RESPONSE", "Response code: ${response.code()}, message: ${response.message()}")
                            Log.e("API_RESPONSE", "Error body: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        thongBaoLoi("Lỗi: ${t.message}")
                        Log.e("API_CALL", "Error: ${t.message}", t)
                    }
                })
            }
        }

        binding.btnHuyDK.setOnClickListener {
            xoaTrang()
        }
    }

    private fun chuyenActivity() {
        val intent = Intent(this@ActivityDangKy, ActivityDangNhap::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            chuyenActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validate(): Int {
        var check = -1
        if (binding.edHoVaTen.text.toString().isNotBlank() &&
            binding.edSoDienThoai.text.toString().isNotBlank() &&
            binding.edTenDangNhapDangKy.text.toString().isNotBlank()
            && binding.edMatKhauDangKy.text.toString().isNotBlank()
            && binding.edNgaySinh.text.toString().isNotBlank()
        ) {
            check = 1
        }
        return check
    }

    private fun xoaTrang() {
        binding.edHoVaTen.setText("")
        binding.edMatKhauDangKy.setText("")
        binding.edSoDienThoai.setText("")
        binding.edTenDangNhapDangKy.setText("")
    }

    private fun thongBaoLoi(loi: String) {
        val bundle = AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo Lỗi")
        bundle.setMessage(loi)
        bundle.setNegativeButton("Hủy", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        bundle.show()
    }

    private fun thongBaoThanhCong(loi: String) {
        val bundle = AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo")
        bundle.setMessage(loi)
        bundle.setNegativeButton("OK", DialogInterface.OnClickListener { dialog, which ->
            chuyenActivity()
        })
        bundle.setPositiveButton("Hủy", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        bundle.show()
    }



}
