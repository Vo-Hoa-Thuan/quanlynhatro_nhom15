package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AppCompatActivity
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityThemPhong
import ct07n.hcmact.quanlynhatro_nhom15.adapter.FILE_NAME
import ct07n.hcmact.quanlynhatro_nhom15.adapter.MA_KHU_KEY
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTroAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentPhongDaOBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentPhongDaO : Fragment() {
    private var _binding: FragmentPhongDaOBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentPhongDaOBinding is null"
        }

    private lateinit var phongAdapter: PhongTroAdapter
    private var listPhong = mutableListOf<Phong>()
    private lateinit var phongApiService: PhongApiService
    private var maKhu = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhongDaOBinding.inflate(inflater, container, false)
        phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        val srf = activity?.getSharedPreferences(FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        maKhu = srf?.getString(MA_KHU_KEY, "").orEmpty()
        Log.d("FragmentPhongDaO", "Retrieved maKhu: $maKhu")

        if (maKhu.isEmpty()) {
            Log.e("FragmentPhongDaO", "maKhu is empty or null.")
            // Có thể hiển thị thông báo cho người dùng nếu cần
        } else {
            Log.d("FragmentPhongDaO", "maKhu: $maKhu")
        }

        // Gọi API để lấy danh sách phòng đã ở
        getPhongDaOFromApi()

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
        phongAdapter = PhongTroAdapter(requireContext(), listPhong)
        binding.rcyPhongDaO.apply {
            adapter = phongAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getPhongDaOFromApi() {
        if (maKhu.isEmpty()) {
            Log.e("FragmentPhongDaO", "maKhu is not set, aborting API call.")
            return
        }
        val call = phongApiService.getAllInPhongByMaKhu(maKhu)
        call.enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    val phongList = response.body()?.filter { it.trang_thai_phong == 1 } ?: emptyList()
                    listPhong.clear()
                    listPhong.addAll(phongList)
                    phongAdapter.notifyDataSetChanged()
                } else {
                    Log.e("FragmentPhongDaO", "Không thể nhận danh sách phòng từ máy chủ.")
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                Log.e("FragmentPhongDaO", "Lỗi khi gọi API: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getPhongDaOFromApi()
    }
}
