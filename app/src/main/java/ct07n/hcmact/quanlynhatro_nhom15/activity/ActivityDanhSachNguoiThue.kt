package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.ViewpagerDanhSachNguoiThueAdapter
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityDanhSachNguoiThueBinding
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityDanhSachNguoiThue : AppCompatActivity() {
    private lateinit var binding: ActivityDanhSachNguoiThueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDanhSachNguoiThueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tabDSNguoiThue)

        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        val adapter = ViewpagerDanhSachNguoiThueAdapter(supportFragmentManager, lifecycle)
        binding.viewpagerDanhSachNguoiThue.adapter = adapter
    }

    private fun postDataToApi(data: NguoiDung) {
        val apiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        val call = apiService.insert(data)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ActivityDanhSachNguoiThue, "Thêm người dùng thành công", Toast.LENGTH_SHORT).show()
                    chuyenActivity()
                } else {
                    Toast.makeText(this@ActivityDanhSachNguoiThue, "Lỗi khi thêm người dùng", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ActivityDanhSachNguoiThue, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun chuyenActivity() {
        val data = NguoiDung(
            ma_nguoi_dung = "123456", // Thay thế bằng dữ liệu thực tế
            ho_ten_nguoi_dung = "Nguyễn Văn A",
            cccd = "123456789",
            nam_sinh = "1990",
            sdt_nguoi_dung = "0987654321",
            que_quan = "Hà Nội",
            trang_thai_chu_hop_dong = 1,
            trang_thai_o = 1,
            ma_phong = "P101"
        )
        postDataToApi(data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
