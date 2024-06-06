package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityThemPhong
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTroAdapter
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentTatCaPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class FragmentTatCaPhong : Fragment() {
    private var _binding: FragmentTatCaPhongBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentTatCaPhongBinding is null"
        }
    private lateinit var phongAdapter: PhongTroAdapter
    private var listPhong = mutableListOf<Phong>()
    private var ma_khu_tro = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTatCaPhongBinding.inflate(inflater, container, false)
        val srf = activity?.getSharedPreferences(FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        ma_khu_tro = srf?.getString(MA_KHU_KEY, "").orEmpty()
        Log.d("FragmentTatCaPhong", "Retrieved maKhu: $ma_khu_tro")

        if (ma_khu_tro.isEmpty()) {
            Log.e("FragmentTatCaPhong", "maKhu is empty or null.")
            // Hiển thị thông báo cho người dùng nếu cần
        } else {
            Log.d("FragmentTatCaPhong", "maKhu: $ma_khu_tro")
        }

        // Gọi API để lấy danh sách phòng
        getPhongs()

        // Chuyển đến ActivityThemPhong
        binding.imgAddPhong.setOnClickListener {
            val intent = Intent(activity, ActivityThemPhong::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Khởi tạo adapter và layout manager cho RecyclerView
        phongAdapter = PhongTroAdapter( requireContext(), listPhong)
        binding.rcyTatCaPhong.apply {
            adapter = phongAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getPhongs() {
        if (ma_khu_tro.isEmpty()) {
            Log.e("FragmentTatCaPhong", "maKhu is not set, aborting API call.")
            return
        }
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.getAllInPhongByMaKhu(ma_khu_tro).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    val phongList = response.body() ?: emptyList()
                    listPhong.clear()
                    listPhong.addAll(phongList)
                    listPhong.sortWith(compareBy({ extractNumber(it.ten_phong) }, { it.ten_phong }))
                    phongAdapter.notifyDataSetChanged()

            } else {
                    Log.e("FragmentTatCaPhong", "Không thể nhận danh sách phòng từ máy chủ.")
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                Log.e("FragmentTatCaPhong", "Lỗi khi gọi API: ${t.message}")
            }
        })
    }
    private fun extractNumber(tenPhong: String): Int {
        return Regex("\\d+").find(tenPhong)?.value?.toInt() ?: Int.MAX_VALUE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Giải phóng binding để tránh rò rỉ bộ nhớ
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getPhongs()
    }
}
