package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.TablayoutTongquanBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentTongQuan : Fragment() {
    private lateinit var binding: TablayoutTongquanBinding
    private var listPhong = listOf<Phong>()
    private var listPhongTrong = listOf<Phong>()
    private var listPhongDangThue = listOf<Phong>()
    private var maKhu = ""
    private var listKhuTro = listOf<KhuTro>()
    private val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
    private val khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TablayoutTongquanBinding.inflate(layoutInflater)

        // Lấy mã khu từ SharedPreferences
        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!

        // Lấy tên khu từ API và cập nhật lên giao diện
        fetchKhuTroData()

        // Thiết lập sự kiện click cho các button
        setupButtonClickListeners()

        return binding.root
    }

    private fun setupButtonClickListeners() {
        binding.phongTrong.setOnClickListener {
            val intent = Intent(context, ActivityPhongTrong::class.java)
            startActivity(intent)
        }
        binding.phongDangChoThue.setOnClickListener {
            val intent = Intent(context, ActivityPhongDangThue::class.java)
            startActivity(intent)
        }
    }

    private fun fetchKhuTroData() {
        // Lấy dữ liệu tất cả khu trọ từ API dựa trên tên admin
        val admin = binding.root.context.getSharedPreferences(
            THONG_TIN_DANG_NHAP,
            AppCompatActivity.MODE_PRIVATE
        ).getString(
            USERNAME_KEY, ""
        )!!

        khuTroApiService.getAllInKhuTroByAdmin(admin).enqueue(object : Callback<List<KhuTro>> {
            override fun onResponse(call: Call<List<KhuTro>>, response: Response<List<KhuTro>>) {
                if (response.isSuccessful) {
                    listKhuTro = response.body() ?: listOf()
                    val khuTro = listKhuTro.find { it.ma_khu_tro == maKhu }
                    binding.tvTenKhuTongQuan.text = "Khu ${khuTro?.ten_khu_tro}"
                    // Gọi hàm lấy dữ liệu phòng khi đã lấy được dữ liệu khu trọ
                    fetchData()
                }
            }

            override fun onFailure(call: Call<List<KhuTro>>, t: Throwable) {
                Log.e("FragmentTongQuan", "Fetch KhuTro data failed: ${t.message}")
            }
        })
    }

    private fun fetchData() {
        // Lấy dữ liệu phòng từ API dựa trên mã khu
        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    listPhong = response.body() ?: listOf()
                    listPhongDangThue = listPhong.filter { it.trang_thai_phong == 1 }
                    listPhongTrong = listPhong.filter { it.trang_thai_phong == 0 }

                    // Cập nhật dữ liệu lên giao diện
                    binding.tvSoPhongTongQuan.text = listPhong.size.toString()
                    binding.tvPhongTrong.text = listPhongTrong.size.toString()
                    binding.tvPhongDangThue.text = listPhongDangThue.size.toString()
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                Log.e("FragmentTongQuan", "Fetch Phong data failed: ${t.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Cập nhật lại dữ liệu khi fragment resumed
        fetchData()
        fetchKhuTroData()
    }

}
