package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ct07n.hcmact.quanlynhatro_nhom15.adapter.FILE_NAME
import ct07n.hcmact.quanlynhatro_nhom15.adapter.MA_KHU_KEY
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.TablayoutTongquanBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityPhongDangThue
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityPhongTrong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentTongQuan : Fragment() {
    private lateinit var binding: TablayoutTongquanBinding
    var listHopDongSapHetHan = listOf<HopDong>()
    var listHopDong = listOf<HopDong>()
    var listPhong = listOf<Phong>()
    var listPhongTrong = listOf<Phong>()
    var listPhongDangThue = listOf<Phong>()
    private var maKhu = ""
    private var tenKhu = ""
    private var listKhuTro = listOf<KhuTro>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TablayoutTongquanBinding.inflate(layoutInflater, container, false)

        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!
        val admin = binding.root.context.getSharedPreferences(THONG_TIN_DANG_NHAP, AppCompatActivity.MODE_PRIVATE).getString(
            USERNAME_KEY, "")!!

        loadKhuTroData(admin) { khuTro ->
            binding.tvTenKhuTongQuan.text = ("Khu ") + khuTro?.ten_khu_tro
            onResume()  // Load data after setting the khuTro name
        }
        binding.phongTrong.setOnClickListener {
            val intent = Intent(context, ActivityPhongTrong::class.java)
            startActivity(intent)
        }
        binding.phongDangChoThue.setOnClickListener {
            val intent = Intent(context, ActivityPhongDangThue::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun loadKhuTroData(admin: String, callback: (KhuTro?) -> Unit) {
        val khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)
        khuTroApiService.getAllInKhuTroByAdmin(admin).enqueue(object : Callback<List<KhuTro>> {
            override fun onResponse(call: Call<List<KhuTro>>, response: Response<List<KhuTro>>) {
                if (response.isSuccessful) {
                    listKhuTro = response.body() ?: listOf()
                    val khuTro = listKhuTro.find { it.ma_khu_tro == maKhu }
                    callback(khuTro)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<KhuTro>>, t: Throwable) {
                // Xử lý lỗi nếu có
                callback(null)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadPhongData()
        loadHopDongData()
    }

    private fun loadPhongData() {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    listPhong = response.body() ?: listOf()
                    listPhongDangThue = listPhong.filter { it.trang_thai_phong == 1 }
                    listPhongTrong = listPhong.filter { it.trang_thai_phong == 0 }

                    binding.tvSoPhongTongQuan.text = listPhong.size.toString()
                    binding.tvPhongTrong.text = listPhongTrong.size.toString()
                    binding.tvPhongDangThue.text = listPhongDangThue.size.toString()
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Xử lý lỗi nếu có
            }
        })
    }

    private fun loadHopDongData() {
        val hopDongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)
        hopDongApiService.getAllHopDong().enqueue(object : Callback<List<HopDong>> {
            override fun onResponse(call: Call<List<HopDong>>, response: Response<List<HopDong>>) {
                if (response.isSuccessful) {
                    listHopDongSapHetHan = response.body() ?: listOf()
                }
            }

            override fun onFailure(call: Call<List<HopDong>>, t: Throwable) {
                // Xử lý lỗi nếu có
            }
        })
    }
}
