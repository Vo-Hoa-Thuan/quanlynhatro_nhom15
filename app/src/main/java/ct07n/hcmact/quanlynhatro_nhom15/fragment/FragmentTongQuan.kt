package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.TablayoutTongquanBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentTongQuan : Fragment() {
    private lateinit var binding: TablayoutTongquanBinding
    private lateinit var phongApiService: PhongApiService
    private var maKhu = ""
    private var listPhong = listOf<Phong>()
    private var totalPhong: Int = 0 // Khai báo biến totalPhong

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TablayoutTongquanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)

        val sharedPreferences = requireContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = sharedPreferences.getString(MA_KHU_KEY, "") ?: ""

        val admin = requireActivity().getSharedPreferences(THONG_TIN_DANG_NHAP, AppCompatActivity.MODE_PRIVATE).getString(USERNAME_KEY, "")!!

        binding.tvTenKhuTongQuan.text = "Khu $admin"
        getAllPhongByMaKhu() // Gọi hàm để lấy danh sách phòng
        Log.d("FragmentTongQuan", "Mã khu: $maKhu")
    }

    private fun getAllPhongByMaKhu() {
        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    // Nếu response thành công
                    val phongList = response.body() ?: emptyList() // Lấy danh sách phòng từ response hoặc trả về danh sách rỗng nếu null
                    totalPhong = phongList.size // Cập nhật giá trị của totalPhong bằng số lượng phòng
                    Log.d("FragmentTongQuan", "Danh sách phòng: $phongList") // Log danh sách phòng
                    updateUIWithPhongList(phongList) // Cập nhật giao diện với danh sách phòng
                } else {
                    // Xử lý trường hợp không thành công (ví dụ: hiển thị thông báo lỗi)
                    Log.e("FragmentTongQuan", "Lỗi khi lấy danh sách phòng: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Xử lý trường hợp gặp lỗi khi gọi API (ví dụ: hiển thị thông báo lỗi)
                Log.e("FragmentTongQuan", "Lỗi khi gọi API lấy danh sách phòng: ${t.message}", t)
            }
        })
    }


    private fun updateUIWithPhongList(phongList: List<Phong>) {
        binding.tvSoPhongTongQuan.text = totalPhong.toString() // Hiển thị số phòng lên giao diện
        // Cập nhật các thành phần giao diện khác tại đây nếu cần
    }
}
