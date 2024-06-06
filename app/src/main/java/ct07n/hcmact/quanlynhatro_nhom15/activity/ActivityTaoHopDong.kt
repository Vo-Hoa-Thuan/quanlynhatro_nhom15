package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongInterface
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTrotaoHopDongAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityTaoHopDongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityTaoHopDong : AppCompatActivity() {
    private lateinit var binding: ActivityTaoHopDongBinding
    private var listPhongChuaCoHopDong = mutableListOf<Phong>()
    private var maKhu: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaoHopDongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbDanhSachPhong)
        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        // Lấy maKhu từ SharedPreferences
        val srf = applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")
        if (maKhu.isNullOrEmpty()) {
            Toast.makeText(this, "Mã khu không tồn tại", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        fetchPhongChuaCoHopDong()
    }

    private fun fetchPhongChuaCoHopDong() {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        val call = phongApiService.getPhongChuaCoHopDong(maKhu!!)

        call.enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful && response.body() != null) {
                    listPhongChuaCoHopDong.clear()
                    listPhongChuaCoHopDong.addAll(response.body()!!)
                    listPhongChuaCoHopDong.sortWith(compareBy({ extractNumber(it.ten_phong) }, { it.ten_phong }))
                    setupRecyclerView()
                } else {
                    Toast.makeText(this@ActivityTaoHopDong, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            }

            private fun extractNumber(tenPhong: String): Int {
                return Regex("\\d+").find(tenPhong)?.value?.toInt() ?: Int.MAX_VALUE
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                Toast.makeText(this@ActivityTaoHopDong, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        val adapter = PhongTrotaoHopDongAdapter(listPhongChuaCoHopDong, object : PhongInterface {
            override fun OnCLickPhong(pos: Int) {
                val intent = Intent(this@ActivityTaoHopDong, ActivitytaoHopDongMoi::class.java)
                val bundle = Bundle().apply {
                    putString("tenPhong", listPhongChuaCoHopDong[pos].ten_phong)
                    putString("maPhong", listPhongChuaCoHopDong[pos].ma_phong)
                }
                intent.putExtras(bundle)
                startActivity(intent)

            }
        })
        binding.rcyPhongTrongCanTaoHopDong.layoutManager = LinearLayoutManager(this)
        binding.rcyPhongTrongCanTaoHopDong.adapter = adapter
    }
}