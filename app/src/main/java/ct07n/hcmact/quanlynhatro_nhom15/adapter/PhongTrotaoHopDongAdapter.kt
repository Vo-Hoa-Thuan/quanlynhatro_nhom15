package ct07n.hcmact.quanlynhatro_nhom15.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.LayoutItemPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhongTroTaoHopDongViewHolder(
    val binding: LayoutItemPhongBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val nguoidungApiService: NguoidungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)

    fun bind(phong: Phong) {
        binding.tvTenPhong.text = phong.ten_phong
        binding.tvGiaThue.text = phong.gia_thue.toString()
        binding.chkTrangThaiPhongTrong.isChecked = phong.trang_thai_phong == 0
        binding.tvGioiHanNguoiO.text = if (phong.so_nguoi_o == 0) {
            "Tối đa: Không giới hạn"
        } else {
            "Tối đa: ${phong.so_nguoi_o} người"
        }

        // Fetch the number of current residents via API
//        nguoidungApiService.getSoNguoiDungByMaPhong(phong.ma_phong).enqueue(object : Callback<Int> {
//            override fun onResponse(call: Call<Int>, response: Response<Int>) {
//                if (response.isSuccessful) {
//                    val soNguoiHienTai = response.body() ?: 0
//                    binding.tvSoNguoiHienTai.text = "có $soNguoiHienTai người đang ở"
//                } else {
//                    binding.tvSoNguoiHienTai.text = "có 0 người đang ở"
//                }
//            }
//
//            override fun onFailure(call: Call<Int>, t: Throwable) {
//                binding.tvSoNguoiHienTai.text = "có 0 người đang ở"
//                Toast.makeText(binding.root.context, "Failed to fetch residents", Toast.LENGTH_SHORT).show()
//            }
//        })

        binding.tvTenPhong.setOnClickListener {
            nguoidungApiService.getListNguoiDungByMaPhong(phong.ma_phong).enqueue(object : Callback<List<NguoiDung>> {
                override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                    if (response.isSuccessful) {
                        val listNguoiDung = response.body()
                        Toast.makeText(binding.root.context, listNguoiDung?.size.toString(), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(binding.root.context, "Failed to fetch services", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                    Toast.makeText(binding.root.context, "Failed to fetch services", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
class PhongTrotaoHopDongAdapter(
    private val listPhong: List<Phong>,
    private val onClick: PhongInterface
) : RecyclerView.Adapter<PhongTroTaoHopDongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhongTroTaoHopDongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemPhongBinding.inflate(inflater, parent, false)
        return PhongTroTaoHopDongViewHolder(binding)
    }

    override fun getItemCount() = listPhong.size

    override fun onBindViewHolder(holder: PhongTroTaoHopDongViewHolder, position: Int) {
        val phong = listPhong[position]
        holder.bind(phong)
        holder.itemView.setOnClickListener {
            onClick.OnCLickPhong(position)
        }
    }
}