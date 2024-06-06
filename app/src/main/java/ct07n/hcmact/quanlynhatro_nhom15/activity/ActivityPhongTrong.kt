package ct07n.hcmact.quanlynhatro_nhom15.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTroAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityPhongTrongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityPhongTrong : AppCompatActivity() {
    private lateinit var binding: ActivityPhongTrongBinding
    private var listPhongTrong = mutableListOf<Phong>()
    private var maKhu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhongTrongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbPhongTrong)
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.black_left)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        val sharedPreferences = getSharedPreferences(FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        maKhu = sharedPreferences.getString(MA_KHU_KEY, "") ?: ""

        reload()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun reload() {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    response.body()?.let { phongList ->
                        listPhongTrong.clear()
                        listPhongTrong.addAll(phongList.filter { it.trang_thai_phong == 0 })
                        listPhongTrong.sortWith(compareBy({ extractNumber(it.ten_phong) }, { it.ten_phong }))
                        binding.recyclerDanhSachPhongTrong.apply {
                            adapter = PhongTroAdapter(this@ActivityPhongTrong, listPhongTrong)
                            layoutManager = LinearLayoutManager(this@ActivityPhongTrong)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Handle the error
            }
        })
    }

    private fun extractNumber(tenPhong: String): Int {
        return Regex("\\d+").find(tenPhong)?.value?.toInt() ?: Int.MAX_VALUE
    }
}
