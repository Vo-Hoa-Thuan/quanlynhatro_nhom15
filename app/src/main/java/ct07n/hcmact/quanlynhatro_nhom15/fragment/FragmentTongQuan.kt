package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityPhongDangThue
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityPhongTrong
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.TablayoutTongquanBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentTongQuan : Fragment() {
    private lateinit var binding: TablayoutTongquanBinding
    private var maKhu = ""
    private var listPhong = listOf<Phong>()
    private var listPhongTrong = listOf<Phong>()
    private var listPhongDangThue = listOf<Phong>()
    private var listKhuTro = listOf<KhuTro>()
    private val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
    private val khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)
    private lateinit var maKhuListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TablayoutTongquanBinding.inflate(layoutInflater)

        // Lắng nghe sự kiện thay đổi mã khu trọ
        val sharedPreferences = requireContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhuListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == MA_KHU_KEY) {
                maKhu = sharedPreferences.getString(MA_KHU_KEY, "")!!
                fetchKhuTroDataAndSetupUI()
                fetchPhongDataAndSetupUI()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(maKhuListener)

        // Lấy và cập nhật dữ liệu ban đầu
        maKhu = sharedPreferences.getString(MA_KHU_KEY, "")!!
        fetchKhuTroDataAndSetupUI()
        fetchPhongDataAndSetupUI()
        setupButtonClickListeners()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Hủy đăng ký lắng nghe sự kiện khi fragment bị hủy
        val sharedPreferences = requireContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(maKhuListener)
    }

    // Phương thức để cập nhật dữ liệu khu trọ và giao diện
    private fun fetchKhuTroDataAndSetupUI() {
        val admin = requireContext().getSharedPreferences(THONG_TIN_DANG_NHAP, AppCompatActivity.MODE_PRIVATE)
            .getString(USERNAME_KEY, "")!!

        khuTroApiService.getAllInKhuTroByAdmin(admin).enqueue(object : Callback<List<KhuTro>> {
            override fun onResponse(call: Call<List<KhuTro>>, response: Response<List<KhuTro>>) {
                if (response.isSuccessful) {
                    val khuTroList = response.body()
                    if (khuTroList != null) {
                        listKhuTro = khuTroList
                        val khuTro = listKhuTro.find { it.ma_khu_tro == maKhu }
                        binding.tvTenKhuTongQuan.text = ("Khu ") + khuTro?.ten_khu_tro
                    }
                } else {
                    // Xử lý lỗi
                }
            }

            override fun onFailure(call: Call<List<KhuTro>>, t: Throwable) {
                // Xử lý lỗi
            }
        })
    }

    // Phương thức để cập nhật dữ liệu phòng và giao diện
    private fun fetchPhongDataAndSetupUI() {
        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    val rooms = response.body()
                    rooms?.let {
                        listPhong = it
                        listPhongDangThue = it.filter { room -> room.trang_thai_phong == 1 }
                        listPhongTrong = it.filter { room -> room.trang_thai_phong == 0 }

                        // Cập nhật giao diện với dữ liệu mới
                        binding.tvSoPhongTongQuan.text = listPhong.size.toString()
                        binding.tvPhongTrong.text = listPhongTrong.size.toString()
                        binding.tvPhongDangThue.text = listPhongDangThue.size.toString()
                    }
                } else {
                    // Xử lý lỗi
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Xử lý lỗi
            }
        })
    }



// Thiết lập sự kiện cho các nút
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
}
