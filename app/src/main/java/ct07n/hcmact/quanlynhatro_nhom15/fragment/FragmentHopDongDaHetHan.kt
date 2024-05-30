package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.adapter.HopDongPhongHetHanAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogGiaHanHdBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentHopDongDaHetHanBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FragmentHopDongDaHetHan : Fragment() {
    private lateinit var binding: FragmentHopDongDaHetHanBinding
    private var listHopDongHetHan = listOf<HopDong>()
    private var maKhu = ""
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHopDongDaHetHanBinding.inflate(inflater, container, false)

        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "") ?: ""
        reLoadList()
        return binding.root
    }

    private fun reLoadList() {
        val hopDongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)
        hopDongApiService.getHopDongHetHanByMaKhu(maKhu).enqueue(object : Callback<List<HopDong>> {
            override fun onResponse(call: Call<List<HopDong>>, response: Response<List<HopDong>>) {
                if (response.isSuccessful) {
                    listHopDongHetHan = response.body() ?: emptyList()
                    val hopDongAdapter = HopDongPhongHetHanAdapter(listHopDongHetHan, this@FragmentHopDongDaHetHan)
                    binding.rcyHetHan.adapter = hopDongAdapter
                    binding.rcyHetHan.layoutManager = LinearLayoutManager(requireContext())
                    hopDongAdapter.notifyDataSetChanged()
                } else {
                    showToast("Failed to load data")
                }
            }

            override fun onFailure(call: Call<List<HopDong>>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        reLoadList()
    }

    fun giaHanHopDong(hopDong: HopDong) {
        val builder = AlertDialog.Builder(binding.root.context).create()
        val dialog = DialogGiaHanHdBinding.inflate(LayoutInflater.from(binding.root.context))
        dialog.tvTenPhongGiaHanHD.text = hopDong.ma_phong
        dialog.edThoiHanCuGiaHan.setText("${hopDong.thoi_han}")
        dialog.edNgayKetThucHopDongCu.setText(chuyenDinhDangNgay(hopDong.ngay_hop_dong))

        dialog.edThoiHanMoiGiaHan.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank()) {
                val stringOldDate = chuyenDinhDangNgay(hopDong.ngay_hop_dong)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val newDate = dateFormat.parse(stringOldDate)
                val calendar = Calendar.getInstance()
                if (newDate != null) {
                    calendar.time = newDate
                }
                calendar.add(Calendar.MONTH, text.toString().toInt())
                dialog.edNgayKetThucHopDongMoi.setText(simpleDateFormat.format(calendar.time))
            }
        }

        dialog.btnLuuGiaHan.setOnClickListener {
            updateHopDong(hopDong, dialog, builder)
        }

        builder.setView(dialog.root)
        builder.show()
    }

    private fun updateHopDong(
        hopDong: HopDong,
        dialog: DialogGiaHanHdBinding,
        builder: AlertDialog
    ) {
        val ngayKetThucMoi = dialog.edNgayKetThucHopDongMoi.text.toString().trim()
        val thoiHanMoi = dialog.edThoiHanMoiGiaHan.text.toString().trim()

        if (ngayKetThucMoi.isEmpty() || thoiHanMoi.isEmpty()) {
            thongBaoLoi("Thời hạn và ngày kết thúc không được để trống!")
            return
        }

        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            sdf.parse(ngayKetThucMoi)
        } catch (e: ParseException) {
            thongBaoLoi("Ngày kết thúc không đúng định dạng(dd/MM/yyyy)")
            return
        }

        val hopDongNew = hopDong.copy(
            thoi_han = thoiHanMoi.toInt(),
            ngay_hop_dong = chuyenDinhDang(Editable.Factory.getInstance().newEditable(ngayKetThucMoi)),
            trang_thai_hop_dong = 1
        )
        val hopDongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)
        hopDongApiService.updateHopDong(hopDong, hopDongNew).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    thongBaoThanhCong("Gia hạn hợp đồng thành công!", builder)
                } else {
                    thongBaoLoi("Gia hạn không thành công!")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                thongBaoLoi("Error: ${t.message}")
            }
        })
    }

    private fun chuyenDinhDang(text: Editable?): String {
        var ngayChuanDinhDang = ""
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val objDate = sdf.parse(text.toString().trim())
            ngayChuanDinhDang = DateFormat.format("yyyy-MM-dd", objDate) as String
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ngayChuanDinhDang
    }

    private fun chuyenDinhDangNgay(ngay: String): String {
        val sdfNgay = SimpleDateFormat("yyyy-MM-dd")
        val objDateNgayO = sdfNgay.parse(ngay)
        return DateFormat.format("dd/MM/yyyy", objDateNgayO) as String
    }

    fun ketThucHopDong(hopDong: HopDong) {
        val intent = Intent(context, ActivityKetThucHopDong::class.java)
        intent.putExtra("hopDong", hopDong)
        startActivity(intent)
    }

    fun thongBaoLoi(loi: String) {
        val bundle = AlertDialog.Builder(requireContext())
        bundle.setTitle("Thông Báo Lỗi")
        bundle.setMessage(loi)
        bundle.setNegativeButton("OK") { dialog, _ ->
            dialog.cancel()
        }
        bundle.show()
    }

    fun thongBaoThanhCong(loi: String, builder: AlertDialog) {
        val bundle = AlertDialog.Builder(requireContext())
        bundle.setTitle("Thông Báo")
        bundle.setMessage(loi)
        bundle.setNegativeButton("OK") { _, _ ->
            reLoadList()
            builder.dismiss()
        }
        bundle.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}