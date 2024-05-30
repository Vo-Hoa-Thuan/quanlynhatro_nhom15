package ct07n.hcmact.quanlynhatro_nhom15.adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Color
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogChiTietHopDongBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.LayoutItemDsHopDongBinding
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentHopDongConHan
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HopDongPhongConHanViewHolder(
    val binding: LayoutItemDsHopDongBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(hopDong: HopDong) {
        loadTenPhong(hopDong.ma_phong)
        loadTenNguoiDung(hopDong.ma_hop_dong)


        binding.tvDanhSachHopDongTrangThai.text = "Tình trạng hợp đồng: Còn hợp đồng"
        binding.tvDanhSachHopDongTrangThai.setTextColor(Color.BLACK)
        binding.layoutKetThuc.isVisible = true


        binding.tvDanhSachHopDongNgayO.text = chuyenDinhDangNgay(hopDong.ngay_o)
        binding.tvDanhSachHopDongNgayKetThuc.text = chuyenDinhDangNgay(hopDong.ngay_hop_dong)
        binding.tvDanhSachHopDongNgayLap.text = chuyenDinhDangNgay(hopDong.ngay_lap_hop_dong)

        binding.tvChiTietDanhSachHD.setOnClickListener {
            val build = AlertDialog.Builder(binding.root.context).create()
            val dialog = DialogChiTietHopDongBinding.inflate(LayoutInflater.from(binding.root.context))
            build.setView(dialog.root)

            loadChiTietHopDong(dialog, hopDong)
            dialog.btnDongChiTietHopDong.setOnClickListener { build.dismiss() }

            build.show()
        }
    }

    private fun loadTenPhong(maPhong: String) {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.getTenPhongById(maPhong).enqueue(object : Callback<PhongApiService.TenPhongResponse> {
            override fun onResponse(call: Call<PhongApiService.TenPhongResponse>, response: Response<PhongApiService.TenPhongResponse>) {
                if (response.isSuccessful) {
                    val tenPhongResponse = response.body()
                    val tenPhong = tenPhongResponse?.ten_phong ?: "N/A"
                    binding.tvDanhSachHopDongTenPhong.text = tenPhong
                } else {
                    binding.tvDanhSachHopDongTenPhong.text = "N/A"
                    Log.e(ContentValues.TAG, "Failed to retrieve room name: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PhongApiService.TenPhongResponse>, t: Throwable) {
                binding.tvDanhSachHopDongTenPhong.text = "N/A"
                Log.e(ContentValues.TAG, "Error retrieving room name: ${t.message}")
            }
        })
    }

    private fun loadTenNguoiDung(maHopDong: String) {
        val hopDongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)
        hopDongApiService.getTenNguoiDungByIDHopDong(maHopDong).enqueue(object : Callback<HopdongApiService.TenNguoiResponse> {
            override fun onResponse(call: Call<HopdongApiService.TenNguoiResponse>, response: Response<HopdongApiService.TenNguoiResponse>) {
                if (response.isSuccessful) {
                    val tenNguoiResponse = response.body()
                    val tenNguoiDung = tenNguoiResponse?.ho_ten_nguoi_dung ?: "N/A"
                    binding.tvDanhSachHopDongTenThanhVien.text = "Họ và tên: $tenNguoiDung"
                } else {
                    binding.tvDanhSachHopDongTenThanhVien.text = "N/A"
                    showToast("Không thể tải tên người dùng")
                }
            }

            override fun onFailure(call: Call<HopdongApiService.TenNguoiResponse>, t: Throwable) {
                binding.tvDanhSachHopDongTenThanhVien.text = "N/A"
                showToast("Lỗi kết nối: ${t.message}")
            }
        })
    }

    private fun loadChiTietHopDong(dialog: DialogChiTietHopDongBinding, hopDong: HopDong) {
        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.getTenPhongById(hopDong.ma_phong).enqueue(object : Callback<PhongApiService.TenPhongResponse> {
            override fun onResponse(call: Call<PhongApiService.TenPhongResponse>, response: Response<PhongApiService.TenPhongResponse>) {
                if (response.isSuccessful) {
                    val tenPhongResponse = response.body()
                    val tenPhong = tenPhongResponse?.ten_phong ?: "N/A"
                    dialog.tvChiTietHDTenPhong.text = tenPhong
                } else {
                    binding.tvDanhSachHopDongTenPhong.text = "N/A"
                    Log.e(ContentValues.TAG, "Failed to retrieve room name: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PhongApiService.TenPhongResponse>, t: Throwable) {
                showToast("Lỗi kết nối: ${t.message}")
            }
        })
        val hopDongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)
        hopDongApiService.getTenNguoiDungByIDHopDong(hopDong.ma_hop_dong).enqueue(object : Callback<HopdongApiService.TenNguoiResponse> {
            override fun onResponse(call: Call<HopdongApiService.TenNguoiResponse>, response: Response<HopdongApiService.TenNguoiResponse>) {
                if (response.isSuccessful) {
                    val tenNguoiResponse = response.body()
                    val tenNguoiDung = tenNguoiResponse?.ho_ten_nguoi_dung ?: "N/A"
                    dialog.tvChiTietHDTenNguoiDung.text = "Họ và tên: $tenNguoiDung"
                } else {
                    binding.tvDanhSachHopDongTenThanhVien.text = "N/A"
                    showToast("Không thể tải tên người dùng")
                }
            }

            override fun onFailure(call: Call<HopdongApiService.TenNguoiResponse>, t: Throwable) {
                binding.tvDanhSachHopDongTenThanhVien.text = "N/A"
                showToast("Lỗi kết nối: ${t.message}")
            }
        })

        dialog.apply {
            tvChiTietHDThoiHan.text = "Thời hạn: ${hopDong.thoi_han} tháng"
            tvChiTietHDNgayO.text = "Ngày ở: ${chuyenDinhDangNgay(hopDong.ngay_o)}"
            tvChiTietHDNgayHopDong.text = "Ngày kết thúc: ${chuyenDinhDangNgay(hopDong.ngay_hop_dong)}"
            tvNgayLapHopDong.text = "Ngày lập hợp đồng: ${chuyenDinhDangNgay(hopDong.ngay_lap_hop_dong)}"
            tvChiTietHDTienCoc.text = "Tiền cọc: ${hopDong.tien_coc}"
            tvChiTietHDTrangThai.text = ""
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun chuyenDinhDangNgay(ngay: String): String {
        val sdfNgay = SimpleDateFormat("yyyy-MM-dd")
        val objDateNgayO = sdfNgay.parse(ngay)
        return DateFormat.format("dd/MM/yyyy", objDateNgayO).toString()
    }
}

class HopDongPhongConHanAdapter(
    private val listHopDong: List<HopDong>,
    private val fragment: FragmentHopDongConHan
) : RecyclerView.Adapter<HopDongPhongConHanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HopDongPhongConHanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemDsHopDongBinding.inflate(inflater, parent, false)
        return HopDongPhongConHanViewHolder(binding)
    }

    override fun getItemCount() = listHopDong.size

    override fun onBindViewHolder(holder: HopDongPhongConHanViewHolder, position: Int) {
        val hopDong = listHopDong[position]
        holder.bind(hopDong)
        holder.binding.layoutKetThuc.setOnClickListener {
            fragment.ketThucHopDong(hopDong)
        }
    }
}