package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ct07n.hcmact.quanlynhatro_nhom15.api.AdminApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityDangNhapBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
            val check = binding.edCheckBox

            val loginRequest = AdminApiService.LoginRequest(username, password)

            loadingDialog?.show()

            val call = adminApiService.checkLogin(loginRequest)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null) {
                            checkKhuTro(username, password, check.isChecked)
                        } else {
                            thongBaoLoi("Tên tài khoản hoặc mật khẩu không đúng")
                        }
                    } else {
                        thongBaoLoi("Có lỗi xảy ra khi đăng nhập")
                    }
                    loadingDialog?.dismiss()
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    thongBaoLoi("Có lỗi xảy ra khi đăng nhập")
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
                    thongBaoLoi("Có lỗi xảy ra khi kiểm tra khu trọ")
                }
                loadingDialog?.dismiss()
            }

            override fun onFailure(call: Call<List<KhuTro>>, t: Throwable) {
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
