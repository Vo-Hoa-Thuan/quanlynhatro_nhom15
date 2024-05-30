package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.TablayoutQuanlyBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentQuanLy : Fragment() {
    private lateinit var binding: TablayoutQuanlyBinding
    private lateinit var apiService: HopdongApiService
    private var maKhu = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TablayoutQuanlyBinding.inflate(inflater, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = sharedPreferences.getString(MA_KHU_KEY, "")!!
        apiService = RetrofitClient.instance.create(HopdongApiService::class.java)

        binding.taoHopDong.setOnClickListener {
            val intent = Intent(context, ActivityTaoHopDong::class.java)
            startActivity(intent)
        }

        binding.TraPhong
            .setOnClickListener {
            val intent = Intent(context, ActivityXuLyPhong::class.java)
            startActivity(intent)
        }

        binding.dsPhongThue.setOnClickListener {
            val intent = Intent(context, ActivityDanhSachPhong::class.java)
            startActivity(intent)
        }

        binding.dsKhachThue.setOnClickListener {
            val intent = Intent(context, ActivityDanhSachNguoiThue::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun updateHopDong() {
        // Bạn muốn làm gì ở đây?
    }

    private fun tinhNgaySapHetHanHopDong(hopDong: HopDong, a: Int) {
        // Bạn muốn làm gì ở đây?
    }
    //thahdhd
    private fun updateHopDongStatus(hopDong: HopDong, newStatus: Int) {
        // Bạn muốn làm gì ở đây?
    }
}
