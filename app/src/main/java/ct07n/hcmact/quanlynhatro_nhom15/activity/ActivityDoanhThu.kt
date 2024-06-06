package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityDoanhThuBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class ActivityDoanhThu : AppCompatActivity() {
    private lateinit var binding: ActivityDoanhThuBinding

    private var maKhu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoanhThuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tbDoanhThu
        setSupportActionBar(binding.tbDoanhThu)
        val ab = getSupportActionBar()
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)
        //====================================================

        val srf=binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu= srf.getString(MA_KHU_KEY, "")!!


        //Hiển thị tổng tiền cọc
        calculateTotalDeposit()
    }

    private fun calculateTotalDeposit() {
        val service = RetrofitClient.instance.create(HopdongApiService::class.java)
        val call = service.getAllHopDongByMaKhu(maKhu)
        call.enqueue(object : Callback<List<HopDong>> {
            override fun onResponse(call: Call<List<HopDong>>, response: Response<List<HopDong>>) {
                if (response.isSuccessful) {
                    val hopDongs = response.body()
                    if (hopDongs != null) {
                        val sum = hopDongs.sumOf { it.tien_coc.toLong() ?: 0L }
                        val sumFormat = String.format("%,d", sum).replace(',', '.')
                        binding.tvTongDoanhThu.text = "$sumFormat đ"
                        Toast.makeText(binding.root.context, "Tổng tiền cọc: $sumFormat đ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<List<HopDong>>, t: Throwable) {
            }
        })
    }
    override fun  onOptionsItemSelected(item : MenuItem): Boolean {
        val id : Int = item.getItemId();
        if (id==android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item);
    }

}