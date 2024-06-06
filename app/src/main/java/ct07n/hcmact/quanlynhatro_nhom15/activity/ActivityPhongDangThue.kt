package ct07n.hcmact.quanlynhatro_nhom15.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTroAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityPhongDangThueBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityPhongDangThue : AppCompatActivity() {
    private lateinit var binding: ActivityPhongDangThueBinding
    private var listPhongDangThue = mutableListOf<Phong>()
    private var maKhu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhongDangThueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbPhongDangThue)
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.black_left)
            setDisplayHomeAsUpEnabled(true)
        }

        val sharedPreferences = getSharedPreferences(FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        maKhu = sharedPreferences.getString(MA_KHU_KEY, "") ?: ""

        reload()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reload() {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)

        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    listPhongDangThue.clear()
                    response.body()?.let { phongList ->
                        listPhongDangThue.addAll(phongList.filter { it.trang_thai_phong == 1 })
                        listPhongDangThue.sortWith(compareBy({ extractNumber(it.ten_phong) }, { it.ten_phong }))
                        binding.recyclerDanhSachPhongDangThue.apply {
                            adapter = PhongTroAdapter(this@ActivityPhongDangThue, listPhongDangThue)
                            layoutManager = LinearLayoutManager(this@ActivityPhongDangThue)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Xử lý lỗi nếu có
            }
        })
    }

    private fun extractNumber(tenPhong: String): Int {
        return Regex("\\d+").find(tenPhong)?.value?.toInt() ?: Int.MAX_VALUE
    }
}
