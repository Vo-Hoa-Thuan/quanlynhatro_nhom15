package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import ct07n.hcmact.quanlynhatro_nhom15.activity.THONG_TIN_PHONG
import ct07n.hcmact.quanlynhatro_nhom15.adapter.*
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogThemLoaiDichVuBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentThongTinBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class FragmentThongTin : Fragment() {
    private lateinit var binding: FragmentThongTinBinding
    private val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
    private val nguoiDungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
    internal lateinit var context: Context
    private var maPhong=""
    private var maKhu=""
    private lateinit var phong: Phong

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThongTinBinding.inflate(layoutInflater)
        val srf = binding.root.context.getSharedPreferences(THONG_TIN_PHONG, Context.MODE_PRIVATE)
        val srf1 = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf1.getString(MA_KHU_KEY,"")!!
        maPhong = srf.getString(MA_PHONG_TRONG_CHI_TIET_PHONG,"")!!
        context = binding.root.context

        // Không cần sử dụng DAO nữa, thay vào đó sử dụng Retrofit để lấy thông tin phòng
        phongApiService.getPhongById(maPhong).enqueue(object : Callback<Phong> {
            override fun onResponse(call: Call<Phong>, response: Response<Phong>) {
                if (response.isSuccessful) {
                    phong = response.body()!!
                    setUpView()
                } else {
                    // Xử lý khi có lỗi từ server
                    Log.e("FragmentThongTin", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Phong>, t: Throwable) {
                // Xử lý khi có lỗi từ mạng
                Log.e("FragmentThongTin", "Network Error: ${t.message}")
            }
        })

        return binding.root
    }

    private fun setUpView() {
        val soNguoiO = phong.so_nguoi_o
        binding.edChiTietTenPhong.setText(phong.ten_phong)
        binding.edChiTietDienTich.setText(phong.dien_tich.toString())
        binding.edGiaThue.setText(phong.gia_thue.toString())
        binding.tvSoNguoiHienTai.setText(soNguoiO.toString())
        binding.edSoNguoiOToiDa.setText(phong.so_nguoi_o.toString())

        binding.btnXoaPhong.setOnClickListener {
            val soNguoiTrongPhong = soNguoiO
            if (soNguoiTrongPhong <= 0) {
                phongApiService.xoaPhongById(maPhong).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            activity?.finish()
                        } else {
                            thongBaoLoi("Không thể xoá phòng có thông tin!!!")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("FragmentThongTin", "Network Error: ${t.message}")
                    }
                })
            } else {
                thongBaoLoi("Không thể xoá phòng có thông tin!!!")
            }
        }

        binding.btnCapNhapPhong.setOnClickListener {
            val tenPhong = binding.edChiTietTenPhong.text.toString()
            val dienTich = binding.edChiTietDienTich.text.toString().toInt()
            val giaThue = binding.edGiaThue.text.toString().toLong()
            val soNguoiOToiDa = binding.edSoNguoiOToiDa.text.toString().toInt()
            val phongUpdate = Phong(
                ma_phong = maPhong,
                ma_khu_tro = maKhu,
                dien_tich = dienTich,
                gia_thue = giaThue,
                ten_phong = tenPhong,
                so_nguoi_o = soNguoiOToiDa,
                trang_thai_phong = 1
            )

            phongApiService.updatePhong(maPhong, phongUpdate).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        activity?.finish()
                    } else {
                        // Xử lý khi có lỗi từ server
                        Log.e("FragmentThongTin", "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Xử lý khi có lỗi từ mạng
                    Log.e("FragmentThongTin", "Network Error: ${t.message}")
                }
            })
        }
    }

    private fun thongBaoLoi(loi: String) {
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setTitle("Thông Báo Lỗi")
        builder.setMessage(loi)
        builder.setNegativeButton("Ok", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }
}
