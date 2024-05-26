package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTroAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityPhongDangThueBinding
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityManHinhChinhChuTro
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityPhongDangThue : AppCompatActivity() {
    private lateinit var binding: ActivityPhongDangThueBinding
    var listPhongDangThue= listOf<Phong>()
    var maKhu=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhongDangThueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tbPhongDangThue
        setSupportActionBar(binding.tbPhongDangThue)
        val ab = getSupportActionBar()
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        val srf=this?.getSharedPreferences(FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        maKhu=srf?.getString(MA_KHU_KEY, "")!!
        reload()
    }

    fun chuyenActivity(){
        val intent = Intent(this@ActivityPhongDangThue, ActivityManHinhChinhChuTro::class.java)
        startActivity(intent)
        finish()
    }
    override fun  onOptionsItemSelected(item : MenuItem): Boolean {
        val id : Int = item.getItemId();
        if (id==android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item);
    }

    private fun reload() {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)

        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    listPhongDangThue = response.body()?.filter { it.trang_thai_phong == 1 } ?: listOf()
                    val phongTroAdapter = PhongTroAdapter(this@ActivityPhongDangThue, listPhongDangThue)
                    binding.recyclerDanhSachPhongDangThue.adapter = phongTroAdapter
                    binding.recyclerDanhSachPhongDangThue.layoutManager = LinearLayoutManager(this@ActivityPhongDangThue)
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Xử lý lỗi nếu có
            }
        })
    }
}