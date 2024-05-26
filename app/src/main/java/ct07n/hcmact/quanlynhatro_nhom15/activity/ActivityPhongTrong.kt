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
    var listPhongTrong= listOf<Phong>()
    var maKhu=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhongTrongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tbPhongTrong
        setSupportActionBar(binding.tbPhongTrong)
        val ab = getSupportActionBar()
        if (ab != null){
            ab.setHomeAsUpIndicator(R.drawable.black_left)
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        val srf=this?.getSharedPreferences(FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        maKhu=srf?.getString(MA_KHU_KEY, "")!!
        reload()

    }

    override fun  onOptionsItemSelected(item : MenuItem): Boolean {
        val id : Int = item.itemId;
        if (id==android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item);
    }

    private fun reload() {

        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)

        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    listPhongTrong = response.body()?.filter { it.trang_thai_phong == 0 } ?: listOf()
                    val phongTroAdapter = PhongTroAdapter(this@ActivityPhongTrong, listPhongTrong)
                    binding.recyclerDanhSachPhongTrong.adapter = phongTroAdapter
                    binding.recyclerDanhSachPhongTrong.layoutManager = LinearLayoutManager(this@ActivityPhongTrong)
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Handle the error
            }
        })
    }
}