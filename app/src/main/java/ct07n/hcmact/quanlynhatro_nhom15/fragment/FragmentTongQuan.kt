package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        maKhu = "your_khu_id" // Thay your_khu_id bằng mã khu thực tế

        getAllPhongByMaKhu()
    }

    private fun getAllPhongByMaKhu() {
        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    listPhong = response.body() ?: emptyList()
                    updateUIWithPhongList(listPhong)
                } else {
                    // Xử lý trường hợp không thành công
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Xử lý trường hợp gặp lỗi khi gọi API
            }
        })
    }

    private fun updateUIWithPhongList(phongList: List<Phong>) {
        binding.tvSoPhongTongQuan.text = phongList.size.toString()
        // Cập nhật các thành phần giao diện khác tại đây
    }
}
