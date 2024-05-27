package ct07n.hcmact.quanlynhatro_nhom15.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
 import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityChiTietPhong
import ct07n.hcmact.quanlynhatro_nhom15.api.*
import ct07n.hcmact.quanlynhatro_nhom15.databinding.LayoutItemPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.adapter.TEN_KHU_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.content.Context



const val MA_PHONG_TRONG_CHI_TIET_PHONG="MA_PHONG_TRONG_CHI_TIET_PHONG"


class PhongTroViewHolder(private val context: Context, private val binding: LayoutItemPhongBinding) : RecyclerView.ViewHolder(binding.root) {

    private val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
    private val nguoidungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)

    fun bind(phong: Phong) {
        binding.tvTenPhong.text = phong.ten_phong
        binding.tvGiaThue.text = phong.gia_thue.toString()
        binding.tvGioiHanNguoiO.text = if (phong.so_nguoi_o == 0) {
            "Tối đa: Không giới hạn"
        } else {
            "Tối đa: ${phong.so_nguoi_o} người"
        }

        // Gọi API để lấy danh sách người đang ở trong phòng
        val call = nguoidungApiService.getListNguoiDungByMaPhong(phong.ma_phong)
        call.enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    val listNguoiDung = response.body() ?: listOf()
                    binding.tvSoNguoiHienTai.text = "có ${listNguoiDung.size} người đang ở"
                    binding.chkTrangThaiPhongDaCoc.isChecked = listNguoiDung.isNotEmpty()
                    binding.chkTrangThaiPhongTrong.isChecked = listNguoiDung.isEmpty()
                } else {
                    // Xử lý lỗi khi không thành công
                    val errorBody = response.errorBody()?.string()
                    Log.e("getNguoiDungByMaPhong", "Không thành công: ${response.code()}, $errorBody")
                    // Hoặc thông báo lỗi cho người dùng
                    Toast.makeText(binding.root.context, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                // Xử lý lỗi khi thất bại
                Log.e("nguoidungApiService", "Lỗi khi gọi API: ${t.message}")
                // Hoặc thông báo lỗi cho người dùng
                Toast.makeText(binding.root.context, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        })

        binding.lnPhong.setOnClickListener {
            val intent = Intent(context, ActivityChiTietPhong::class.java)
            intent.putExtra(MA_PHONG_TRONG_CHI_TIET_PHONG, phong.ma_phong)
            context.startActivity(intent)
        }
    }
}


class PhongTroAdapter(private val context: Context, private val listPhong: List<Phong>) : RecyclerView.Adapter<PhongTroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhongTroViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemPhongBinding.inflate(inflater, parent, false)
        return PhongTroViewHolder(context, binding)
    }

    override fun getItemCount() = listPhong.size

    override fun onBindViewHolder(holder: PhongTroViewHolder, position: Int) {
        val phong = listPhong[position]
        holder.bind(phong)
    }
}