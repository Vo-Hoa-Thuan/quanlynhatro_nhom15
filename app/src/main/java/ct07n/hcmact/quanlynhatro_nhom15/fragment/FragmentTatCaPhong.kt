package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ct07n.hcmact.quanlynhatro_nhom15.R
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityThemPhong
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTroAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentTatCaPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import android.os.Handler

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

        // Xử lý khi nhấn nút Lọc
        binding.btnFilterPhong.setOnClickListener {
            showFilterDialog()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Khởi tạo adapter và layout manager cho RecyclerView
        phongAdapter = PhongTroAdapter(requireContext(), listPhong)
        binding.rcyTatCaPhong.apply {
            adapter = phongAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showFilterDialog() {
        // Tạo một AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        // Nạp layout cho dialog
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter, null)
        builder.setView(dialogView)

        // Cài đặt các thuộc tính cho dialog
        val alertDialog = builder.create()

        // Đặt vị trí cho dialog ở dưới cùng của màn hình
        val window = alertDialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            layoutParams.gravity = Gravity.BOTTOM
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = layoutParams
        }

        val edtMinGiaThue = dialogView.findViewById<EditText>(R.id.edtMinGiaThue)
        val edtMaxGiaThue = dialogView.findViewById<EditText>(R.id.edtMaxGiaThue)
        val edtSoNguoiO = dialogView.findViewById<EditText>(R.id.edtSoNguoiO)
        val edtDienTich = dialogView.findViewById<EditText>(R.id.edtDienTich)
        val btnXacNhan = dialogView.findViewById<Button>(R.id.btnXacNhan)
        val btnHuy = dialogView.findViewById<Button>(R.id.btnHuy)


        btnHuy.setOnClickListener {
            alertDialog.dismiss()
        }

        // Xử lý khi nhấn nút Xác nhận
        btnXacNhan.setOnClickListener {
            val minGiaThue = edtMinGiaThue.text.toString().toIntOrNull()
            val maxGiaThue = edtMaxGiaThue.text.toString().toIntOrNull()
            val soNguoiO = edtSoNguoiO.text.toString().toIntOrNull()
            val dienTich = edtDienTich.text.toString().toIntOrNull()


            // Thực hiện hành động lọc với các tiêu chí
            filterPhongs(minGiaThue, maxGiaThue, soNguoiO, dienTich)

            // Đóng dialog
            alertDialog.dismiss()
        }

        // Hiển thị dialog
        alertDialog.show()
    }

    private fun filterPhongs(minGiaThue: Int?, maxGiaThue: Int?, soNguoiO: Int?, dienTich: Int?) {
        // Thiết lập giá trị min và max mặc định nếu ô giá trị trống
        val minGiaThueDefault = minGiaThue ?: 0
        val maxGiaThueDefault = maxGiaThue ?: listPhong.maxByOrNull { it.gia_thue }?.gia_thue?.toInt() ?: 0

        // Tạo chuỗi thông tin lọc cụ thể
        val filterInfo = buildString {
            append("Đã lọc phòng theo:")
            if (minGiaThue != null || maxGiaThue != null) {
                append("\n- Giá thuê từ ${minGiaThue ?: "0"} đến ${maxGiaThue ?: "tối đa"}")
            }
            if (soNguoiO != null) {
                append("\n- Số người ở: $soNguoiO")
            }
            if (dienTich != null) {
                append("\n- Diện tích: $dienTich")
            }
        }

        // Logic để lọc danh sách phòng theo các tiêu chí
        val filteredList = listPhong.filter { phong ->
            (phong.gia_thue in minGiaThueDefault..maxGiaThueDefault) &&
                    (soNguoiO == null || phong.so_nguoi_o == soNguoiO) &&
                    (dienTich == null || phong.dien_tich == dienTich)
        }

        if (filteredList.isEmpty()) {
            // Hiển thị thông báo nếu không có phòng phù hợp
            val toast = Toast.makeText(requireContext(), "Không có phòng phù hợp", Toast.LENGTH_SHORT)
            toast.show()

            val handler = Handler()
            handler.postDelayed({
                toast.cancel()
            }, 2000)
        } else {
            // Hiển thị thông báo với thông tin lọc cụ thể
            val toast = Toast.makeText(requireContext(), filterInfo, Toast.LENGTH_LONG)
            toast.show()

            val handler = Handler()
            handler.postDelayed({
                toast.cancel()
            }, 3000)

            // Cập nhật danh sách phòng và cập nhật RecyclerView
            listPhong.clear()
            listPhong.addAll(filteredList)
            listPhong.sortBy { it.ten_phong }
            phongAdapter.notifyDataSetChanged()
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
                    listPhong.sortBy { it.ten_phong }

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
