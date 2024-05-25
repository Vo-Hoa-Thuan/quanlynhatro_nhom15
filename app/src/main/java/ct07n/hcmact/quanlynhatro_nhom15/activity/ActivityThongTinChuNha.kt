package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.isVisible
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.api.AdminApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityThongTinChuNhaBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Admin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityThongTinChuNha : AppCompatActivity() {
    private lateinit var binding: ActivityThongTinChuNhaBinding
    private lateinit var adminApiService: AdminApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThongTinChuNhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbThongTinChuNha)
        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        adminApiService = RetrofitClient.instance.create(AdminApiService::class.java)

        // Nhận thông tin username từ Intent
        val adminUsername: String? = intent.getStringExtra("username")

        // Kiểm tra nếu username không null hoặc trống thì gọi hàm fetchAdminDetails
        if (!adminUsername.isNullOrEmpty()) {
            fetchAdminDetails(adminUsername)
        } else {
            // Nếu không có username từ Intent, xử lý tương ứng ở đây (ví dụ: hiển thị thông báo lỗi)
            Log.e("ActivityThongTinChuNha", "Không tìm thấy thông tin người dùng.")
        }
    }


    private fun fetchAdminDetails(FILE_NAME: String) {
        adminApiService.getAdmin(FILE_NAME).enqueue(object : Callback<Admin> {
            override fun onResponse(call: Call<Admin>, response: Response<Admin>) {
                if (response.isSuccessful) {
                    val admin = response.body()
                    if (admin != null) {
                        binding.tvTenChuNha.text = admin.ho_ten
                        binding.tvSoDienThoai.text = admin.sdt
                        if (admin.ngan_hang.isEmpty() && admin.stk.isEmpty()) {
                            binding.linearSoTaiKhoan.isVisible = false
                            binding.linearNganHang.isVisible = false
                        } else {
                            binding.linearSoTaiKhoan.isVisible = true
                            binding.linearNganHang.isVisible = true
                            binding.tvSoTaiKhoan.text = admin.stk
                            binding.tvNganHang.text = admin.ngan_hang
                        }
                        binding.tvNgaySinh.text = admin.ngay_sinh
                        if (binding.tvSoTaiKhoan.text.isEmpty()) {
                            binding.linearSoTaiKhoan.setBackgroundColor(Color.parseColor("#EBEBEB"))
                            binding.tvSTK.text = ""
                        }
                        if (binding.tvNgaySinh.text.isEmpty()) {
                            binding.linearNgaySinh.setBackgroundColor(Color.parseColor("#EBEBEB"))
                            binding.tvNS.text = ""
                        }
                    }
                } else {
                    // Handle response errors
                    thongBaoLoi("Lỗi tải thông tin chủ nhà: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Admin>, t: Throwable) {
                // Handle failure
                thongBaoLoi("Lỗi kết nối tới máy chủ: ${t.message}")
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun thongBaoLoi(loi: String) {
        val bundle = androidx.appcompat.app.AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo Lỗi")
        bundle.setMessage(loi)
        bundle.setNegativeButton("Hủy") { dialog, _ ->
            dialog.cancel()
        }
        bundle.show()
    }
}
