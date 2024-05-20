package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ct07n.hcmact.quanlynhatro_nhom15.api.AdminApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityDangNhapBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val THONG_TIN_DANG_NHAP = "Thon_tin_dang_nhap"
const val USERNAME_KEY = "username"
const val PASSWORD_KEY = "password"
const val CHECKBOX_KEY = "REMEMBER"

class ActivityDangNhap : AppCompatActivity() {
    private lateinit var binding: ActivityDangNhapBinding
    private lateinit var adminApiService: AdminApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangNhapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adminApiService = RetrofitClient.instance.create(AdminApiService::class.java)

        val pref: SharedPreferences = getSharedPreferences(THONG_TIN_DANG_NHAP, MODE_PRIVATE)
        binding.edTenDangNhap.setText(pref.getString(USERNAME_KEY, ""))
        if (pref.getBoolean(CHECKBOX_KEY, false)) {
            binding.edMatKhau.setText(pref.getString(PASSWORD_KEY, ""))
            binding.edCheckBox.isChecked = pref.getBoolean(CHECKBOX_KEY, false)
        } else {
            binding.edMatKhau.setText("")
            binding.edCheckBox.isChecked = false
        }

        binding.btnLuuDN.setOnClickListener {
            val userName = binding.edTenDangNhap.text.toString()
            val passWord = binding.edMatKhau.text.toString()
            val check = binding.edCheckBox

            if (userName.isNotBlank() && passWord.isNotBlank()) {
                Log.d("ActivityDangNhap", "Sending login request with username: $userName, password: $passWord")
                val loginRequest = AdminApiService.LoginRequest(userName, passWord)
                adminApiService.checkLogin(loginRequest).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            Log.d("ActivityDangNhap", "Response body: $loginResponse")

                            if (loginResponse?.success == true) {
                                rememberUser(userName, passWord, check.isChecked)
                                val intent = Intent(this@ActivityDangNhap, ActivityManHinhChinhChuTro::class.java)
                                startActivity(intent)
                                finish()
                                val loadingDialog = Loading(this@ActivityDangNhap)
                                loadingDialog.show()
                                Log.d("ActivityDangNhap", "Login successful")
                            } else {
                                val message = loginResponse?.message ?: "Không rõ nguyên nhân"
                                thongBaoLoi("Đăng nhập thất bại: $message")
                                Log.d("ActivityDangNhap", "Login failed: $message")
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            thongBaoLoi("Đăng nhập thất bại: $errorBody")
                            Log.d("ActivityDangNhap", "Response failed: ${response.code()}, error: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        thongBaoLoi("Lỗi: ${t.message}")
                        Log.e("ActivityDangNhap", "Error sending login request: ${t.message}", t)
                    }
                })
            } else {
                thongBaoLoi("Nhập đầy đủ thông tin")
            }
        }

        binding.btnHuyDN.setOnClickListener {
            binding.edTenDangNhap.setText("")
            binding.edMatKhau.setText("")
            binding.edCheckBox.isChecked = false
        }

        binding.linerDk.setOnClickListener {
            val intent = Intent(this@ActivityDangNhap, ActivityDangKy::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun thongBaoLoi(loi: String) {
        val bundle = AlertDialog.Builder(this)
        bundle.setTitle("Lỗi đăng nhập")
        bundle.setMessage(loi)
        bundle.setNegativeButton("Hủy", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        bundle.show()
    }

    private fun rememberUser(u: String, p: String, status: Boolean) {
        val pref: SharedPreferences = getSharedPreferences(THONG_TIN_DANG_NHAP, MODE_PRIVATE)
        val edit = pref.edit()
        edit.putString(USERNAME_KEY, u)
        edit.putString(PASSWORD_KEY, p)
        edit.putBoolean(CHECKBOX_KEY, status)
        edit.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Gọi loading.cancel() nếu cần
    }
}
