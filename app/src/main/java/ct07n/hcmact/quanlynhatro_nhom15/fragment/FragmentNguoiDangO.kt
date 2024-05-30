package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityCapNhatKhachThue
import ct07n.hcmact.quanlynhatro_nhom15.adapter.KhachThueInterface
import ct07n.hcmact.quanlynhatro_nhom15.adapter.NguoiThueAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentNguoiDangOBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class FragmentNguoiDangO : Fragment() {
    private lateinit var binding: FragmentNguoiDangOBinding
    private var maKhu = ""
    private lateinit var nguoiDungApiService: NguoidungApiService
    private lateinit var nguoiThueAdapter: NguoiThueAdapter
    private var listNguoiDung = listOf<NguoiDung>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNguoiDangOBinding.inflate(inflater, container, false)

        // Khởi tạo Retrofit client
        nguoiDungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)

        // Inflate the layout for this fragment
        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!

        // Setup RecyclerView
        setupRecyclerView()

        // Gọi API để lấy danh sách người dùng đang ở
        fetchNguoiDungData()

        // Lắng nghe sự kiện nút tìm kiếm
        binding.searchTenPhong.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchByTenPhong(newText)
                }
                return false
            }
        })

        return binding.root
    }

    private fun fetchNguoiDungData() {
        nguoiDungApiService.getAllInNguoiDangOByMaKhu(maKhu).enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    listNguoiDung = response.body() ?: emptyList()
                    nguoiThueAdapter.updateList(listNguoiDung)
                } else {
                    // Xử lý lỗi nếu cần
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                // Xử lý lỗi nếu cần
            }
        })
    }

    private fun searchByTenPhong(tenPhong: String) {
        nguoiDungApiService.getAllInNguoiDangOByMaKhu(maKhu).enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    var list = response.body() ?: emptyList()
                    list = list.filter { it.ho_ten_nguoi_dung.contains(tenPhong, ignoreCase = true) }
                    nguoiThueAdapter.updateList(list)
                } else {
                    // Xử lý lỗi nếu cần
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                // Xử lý lỗi nếu cần
            }
        })
    }

    private fun setupRecyclerView() {
        nguoiThueAdapter = NguoiThueAdapter(listNguoiDung, object : KhachThueInterface {
            override fun OnClickKhachThue(pos: Int) {
                val nguoiDung = listNguoiDung[pos]
                val intent = Intent(requireContext(), ActivityCapNhatKhachThue::class.java)
                intent.putExtra("khachThue", nguoiDung)
                startActivity(intent)
            }
        })
        binding.rcyNguoiDangO.adapter = nguoiThueAdapter
        binding.rcyNguoiDangO.layoutManager = LinearLayoutManager(activity)
    }

    // Các phương thức khác của FragmentNguoiDangO
}
