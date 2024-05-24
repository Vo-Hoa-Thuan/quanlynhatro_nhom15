package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ct07n.hcmact.quanlynhatro_nhom15.api.AdminApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityDangNhapBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val THONG_TIN_DANG_NHAP="Thon_tin_dang_nhap"
const val USERNAME_KEY = "USERNAME"
const val PASSWORD_KEY = "PASSWORD"
const val CHECKBOX_KEY = "REMEMBER"

class ActivityDangNhap : AppCompatActivity() {
    private lateinit var binding: ActivityDangNhapBinding
    private var listKhuTro = listOf<KhuTro>()

    private lateinit var adminApiService: AdminApiService
    private lateinit var khuTroApiService: KhuTroApiService
    private var loadingDialog: Loading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangNhapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = Loading(this@ActivityDangNhap)

        val pref: SharedPreferences = getSharedPreferences(THONG_TIN_DANG_NHAP, MODE_PRIVATE)

        binding.edTenDangNhap.setText(pref.getString(USERNAME_KEY, ""))
        if (pref.getBoolean(CHECKBOX_KEY, false)) {
            binding.edMatKhau.setText(pref.getString(PASSWORD_KEY, ""))
            binding.edCheckBox.isChecked = pref.getBoolean(CHECKBOX_KEY, false)
        } else {
            binding.edMatKhau.setText("")
            binding.edCheckBox.isChecked = false
        }

        adminApiService = RetrofitClient.instance.create(AdminApiService::class.java)
        khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)

        binding.btnLuuDN.setOnClickListener {
            val username = binding.edTenDangNhap.text.toString()
            val password = binding.edMatKhau.text.toString()
            val check = binding.edCheckBox.isChecked

            val loginRequest = AdminApiService.LoginRequest(username, password)

            loadingDialog?.show()

            val call = adminApiService.checkLogin(loginRequest)
            call.enqueue(object : Callback<AdminApiService.LoginResponse> {
                override fun onResponse(call: Call<AdminApiService.LoginResponse>, response: Response<AdminApiService.LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse.valid) {
                            checkKhuTro(username, password, check)
                        } else if (response.errorBody() != null) {
                            val errorMessage = response.errorBody()?.string()
                            thongBaoLoi("Tên tài khoản hoặc mật khẩu không đúng")
                        } else {
                            thongBaoLoi("Tên tài khoản hoặc mật khẩu không đúng")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        thongBaoLoi("Tên tài khoản hoặc mật khẩu không đúng")

                    }
                    loadingDialog?.dismiss()
                }

                override fun onFailure(call: Call<AdminApiService.LoginResponse>, t: Throwable) {
                    thongBaoLoi("Có lỗi xảy ra khi đăng nhập")
                    Log.d("API_ERROR", "Error: ${t.message}")
                    loadingDialog?.dismiss()
                }
            })
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

    private fun checkKhuTro(username: String, password: String, remember: Boolean) {
        loadingDialog?.show()
        val callCheckKhuTro = khuTroApiService.getAllInKhuTroByAdmin(username)
        callCheckKhuTro.enqueue(object : Callback<List<KhuTro>> {
            override fun onResponse(call: Call<List<KhuTro>>, response: Response<List<KhuTro>>) {
                if (response.isSuccessful) {
                    listKhuTro = response.body() ?: listOf()
                    Log.d("DEBUG", "Response Body: ${response.body()}")
                    Log.d("DEBUG", "List Khu Tro: $listKhuTro")
                    if (listKhuTro.isNotEmpty()) {
                        val intent = Intent(this@ActivityDangNhap, ActivityManHinhChinhChuTro::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@ActivityDangNhap, ActivityHuongDanTaoKhu::class.java)
                        startActivity(intent)
                        finish()
                    }
                    rememberUser(username, password, remember)
                } else {
                    Log.e("DEBUG", "Error Response Code: ${response.code()}")
                    Log.e("DEBUG", "Error Response Message: ${response.message()}")
                    thongBaoLoi("Có lỗi xảy ra khi kiểm tra khu trọ")
                }
                loadingDialog?.dismiss()
            }

            override fun onFailure(call: Call<List<KhuTro>>, t: Throwable) {
                Log.e("DEBUG", "API Call Failure: ${t.message}")
                thongBaoLoi("Có lỗi xảy ra khi kiểm tra khu trọ")
                loadingDialog?.dismiss()
            }
        })
    }


    private fun thongBaoLoi(loi: String) {
        val bundle = AlertDialog.Builder(this)
        bundle.setTitle("Lỗi đăng nhập")
        bundle.setMessage(loi)
        bundle.setNegativeButton("Hủy") { dialog, _ ->
            dialog.cancel()
        }
        bundle.show()
    }

    private fun rememberUser(username: String, password: String, remember: Boolean) {
        val pref: SharedPreferences = getSharedPreferences(THONG_TIN_DANG_NHAP, MODE_PRIVATE)
        val edit = pref.edit()
        edit.putString(USERNAME_KEY, username)
        edit.putString(PASSWORD_KEY, password)
        edit.putBoolean(CHECKBOX_KEY, remember)
        edit.apply()
    }
}
