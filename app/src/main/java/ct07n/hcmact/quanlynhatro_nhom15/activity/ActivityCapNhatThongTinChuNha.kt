package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.api.AdminApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityCapNhatThongTinChuNhaBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Admin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityCapNhatThongTinChuNha : AppCompatActivity() {
    private lateinit var binding: ActivityCapNhatThongTinChuNhaBinding
    private lateinit var adminApiService: AdminApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCapNhatThongTinChuNhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbDangKy)
        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        adminApiService = RetrofitClient.instance.create(AdminApiService::class.java)

        val username: String? = intent.getStringExtra("username")

        if (username != null) {
            adminApiService.getAdmin(username).enqueue(object : Callback<Admin> {
                override fun onResponse(call: Call<Admin>, response: Response<Admin>) {
                    if (response.isSuccessful) {
                        val admin = response.body()
                        if (admin != null) {
                            binding.edHoVaTen.setText(admin.ho_ten)
                            binding.edNgaySinh.setText(admin.ngay_sinh)
                            binding.edMatKhauDangKy.setText(admin.mat_khau)
                            binding.edSoDienThoai.setText(admin.sdt)
                            binding.edSoTaiKhoan.setText(admin.stk)
                            binding.edNganHang.setText(admin.ngan_hang)
                            binding.edTenDangNhapDangKy.setText(admin.ten_dang_nhap)
                            binding.edTenDangNhapDangKy.isEnabled = false
                            binding.edTenDangNhapDangKy.setTextColor(Color.BLACK)
                        }
                    } else {
                        thongBaoLoi("Lỗi tải thông tin chủ nhà: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Admin>, t: Throwable) {
                    thongBaoLoi("Lỗi kết nối tới máy chủ: ${t.message}")
                }
            })

            binding.btnLuuCapNhat.setOnClickListener {
                if (validate() < 1) {
                    thongBaoLoi("Dữ liệu không được để trống!")
                    return@setOnClickListener
                } else {
                    val adminNew = Admin(
                        sdt = binding.edSoDienThoai.text.toString(),
                        ten_dang_nhap = binding.edTenDangNhapDangKy.text.toString(),
                        ho_ten = binding.edHoVaTen.text.toString(),
                        stk = binding.edSoTaiKhoan.text.toString(),
                        ngan_hang = binding.edNganHang.text.toString(),
                        ngay_sinh = binding.edNgaySinh.text.toString(),
                        mat_khau = binding.edMatKhauDangKy.text.toString()
                    )
                    adminApiService.updateAdmin(adminNew).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                thongBaoThanhCong("Bạn đã cập nhật thành công !!!")
                                xoaTrang()
                            } else {
                                thongBaoLoi("Bạn đã cập nhật không thành công tài khoản !!!")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            thongBaoLoi("Lỗi kết nối tới máy chủ: ${t.message}")
                        }
                    })
                }
            }

            binding.btnHuyCapNhat.setOnClickListener {
                xoaTrang()
            }
        } else {
            thongBaoLoi("Dữ liệu admin không hợp lệ!")
        }


        binding.btnHuyCapNhat.setOnClickListener {
            xoaTrang()
        }
    }

    fun xoaTrang() {
        binding.edHoVaTen.setText("")
        binding.edMatKhauDangKy.setText("")
        binding.edSoDienThoai.setText("")
        binding.edTenDangNhapDangKy.setText("")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    fun validate(): Int {
        return if (binding.edHoVaTen.text.toString().isNotBlank() &&
            binding.edSoDienThoai.text.toString().isNotBlank() &&
            binding.edTenDangNhapDangKy.text.toString().isNotBlank() &&
            binding.edMatKhauDangKy.text.toString().isNotBlank() &&
            binding.edSoTaiKhoan.text.toString().isNotBlank() &&
            binding.edNgaySinh.text.toString().isNotBlank()
        ) {
            1
        } else {
            -1
        }
    }

    fun thongBaoLoi(loi: String) {
        val bundle = AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo Lỗi")
        bundle.setMessage(loi)
        bundle.setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }
        bundle.show()
    }

    fun thongBaoThanhCong(loi: String) {
        val bundle = AlertDialog.Builder(this)
        bundle.setTitle("Thông Báo")
        bundle.setMessage(loi)
        bundle.setNegativeButton("OK") { dialog, _ -> finish() }
        bundle.setPositiveButton("Hủy") { dialog, _ -> dialog.cancel() }
        bundle.show()
    }
}
