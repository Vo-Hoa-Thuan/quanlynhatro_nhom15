package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityCapNhatKhachThue
import ct07n.hcmact.quanlynhatro_nhom15.adapter.*
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
    private var maPhong = ""
    private lateinit var nguoiDungApiService: NguoidungApiService
    var listNguoiDung = listOf<NguoiDung>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNguoiDangOBinding.inflate(LayoutInflater.from(context))

        // Khởi tạo Retrofit client
        nguoiDungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)

        // Inflate the layout for this fragment
        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!

        // Gọi API để lấy danh sách người dùng đang ở
        nguoiDungApiService.getAllInNguoiDangOByMaKhu(maKhu).enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    listNguoiDung = response.body() ?: emptyList()
                    val nguoiThueAdapter = NguoiThueAdapter(listNguoiDung, object : KhachThueInterface {
                        override fun OnClickKhachThue(pos: Int) {
                            val nguoiDung = listNguoiDung[pos]
                            val intent = Intent(requireContext(), ActivityCapNhatKhachThue::class.java)
                            intent.putExtra("khachThue", nguoiDung)
                            startActivity(intent)
                        }
                    })
                    binding.rcyNguoiDangO.adapter = nguoiThueAdapter
                    binding.rcyNguoiDangO.layoutManager = LinearLayoutManager(activity)
                } else {
                    // Xử lý lỗi nếu cần
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                // Xử lý lỗi nếu cần
            }
        })

        // Gọi API để lấy danh sách phòng và xử lý logic khi nhấn nút thêm người thuê

        return binding.root
    }

    // Các phương thức khác của FragmentNguoiDangO
}
