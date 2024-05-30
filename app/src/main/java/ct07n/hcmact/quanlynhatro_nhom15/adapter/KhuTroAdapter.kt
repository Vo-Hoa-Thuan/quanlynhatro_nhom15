package ct07n.hcmact.quanlynhatro_nhom15.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityManHinhChinhChuTro
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.LayoutItemKhuTroBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

const val MA_KHU_KEY = "ma_khu"
const val TEN_KHU_KEY = "ten_khu_tro"
const val FILE_NAME = "USER_FILE"

class KhuTroAdapter(
    private val list: List<KhuTro>,
    private val onKhuTroSelected: (KhuTro) -> Unit
) : RecyclerView.Adapter<KhuTroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KhuTroViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemKhuTroBinding.inflate(inflater, parent, false)
        return KhuTroViewHolder(binding, onKhuTroSelected)
    }

    override fun onBindViewHolder(holder: KhuTroViewHolder, position: Int) {
        val khuTro = list[position]
        holder.bind(khuTro)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class KhuTroViewHolder(
    val binding: LayoutItemKhuTroBinding,
    private val onKhuTroSelected: (KhuTro) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
    private val khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)

    fun bind(khuTro: KhuTro) {
        val context = binding.root.context
        val pre = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val maKhu = pre.getString(MA_KHU_KEY, "")

        binding.tvTenKhuTro.text = "Khu: " + khuTro.ten_khu_tro
        binding.tvDiaChiKhuTro.text = "Địa chỉ: " + khuTro.dia_chi

        fetchRooms(khuTro.ten_khu_tro)

        binding.btnQuanLyKhuTro.setOnClickListener {
            onKhuTroSelected(khuTro)
            Log.d("KhuTroAdapter", "Selected khuTro: ${khuTro.ten_khu_tro}")
        }

        if (maKhu == khuTro.ma_khu_tro) {
            binding.chkTrangThaiKhuTro.isVisible = true
            binding.chkTrangThaiKhuTro.isChecked = true
        }

        binding.btnXoaKhuTro.setOnClickListener {
            deleteKhuTro(khuTro)
        }
    }

    private fun fetchRooms(tenKhuTro: String) {
        val call = phongApiService.getAllInPhongByTenKhuTro(tenKhuTro)
        call.enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    val listPhong = response.body() ?: listOf()
                    val listPhongTrong = listPhong.filter { it.trang_thai_phong == 0 }
                    binding.tvSoPhongKhuTro.text = "Tổng số phòng: " + listPhong.size
                    binding.tvSoPhongKhuTroConTrong.text = "" + listPhongTrong.size + " phòng trống"
                } else {
                    // Handle the error
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Handle the error
            }
        })
    }

    private fun deleteKhuTro(khuTro: KhuTro) {
        val call = khuTroApiService.deleteKhuTro(khuTro.ma_khu_tro)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    thongBaoThanhCong("Bạn đã xóa thành công !!!")
                } else {
                    thongBaoLoi("Bạn xóa không thành công !!!")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                thongBaoLoi("Bạn xóa không thành công !!!")
            }
        })
    }

    private fun thongBaoLoi(loi: String) {
        val bundle = AlertDialog.Builder(binding.root.context)
        bundle.setTitle("Thông Báo Lỗi !!!")
        bundle.setMessage(loi)
        bundle.setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }
        bundle.show()
    }

    private fun thongBaoThanhCong(loi: String) {
        val context = binding.root.context
        val bundle = AlertDialog.Builder(context)
        bundle.setTitle("Thông Báo thành công !!!")
        bundle.setMessage(loi)
        bundle.setNegativeButton("OK") { _, _ ->
            val intent = Intent(context, ActivityManHinhChinhChuTro::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
        bundle.show()
    }
}
