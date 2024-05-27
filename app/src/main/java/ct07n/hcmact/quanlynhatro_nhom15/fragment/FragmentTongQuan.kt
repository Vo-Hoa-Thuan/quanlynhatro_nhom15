package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityPhongDangThue
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityPhongTrong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentTongQuan:Fragment() {
    private lateinit var binding: TablayoutTongquanBinding
    var listPhong = listOf<Phong>()
    var listPhongTrong = listOf<Phong>()
    var listPhongDangThue = listOf<Phong>()
    private var maKhu = ""
    private var tenKhu = ""
    private var listKhuTro = listOf<KhuTro>()
    private val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
    private val khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TablayoutTongquanBinding.inflate(layoutInflater)


        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!
        //================================
        val admin =
            binding.root.context.getSharedPreferences(
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
                    onResume()
                }
            }

            override fun onFailure(call: Call<List<KhuTro>>, t: Throwable) {
                // Xử lý lỗi
            }
        })

        val pre =
            binding.root.context.getSharedPreferences(FILE_NAME, AppCompatActivity.MODE_PRIVATE)

        val khuTro = listKhuTro.find { it.ma_khu_tro == maKhu }
        binding.tvTenKhuTongQuan.text = ("Khu ") + khuTro?.ten_khu_tro
        pre.edit().putString(MA_KHU_KEY, maKhu).commit()
        onResume()
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


    override fun onResume() {
        super.onResume()

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
                // Xử lý lỗi
            }
        })
    }
}

