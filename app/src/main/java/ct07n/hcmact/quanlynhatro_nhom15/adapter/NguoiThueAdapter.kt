package ct07n.hcmact.quanlynhatro_nhom15.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogChiTietNguoiThueBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.LayoutItemNguoiThueBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NguoiThueViewHolder(
    private val binding: LayoutItemNguoiThueBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(nguoiDung: NguoiDung) {
        val phongApiService = PhongApiService.getInstance()
        val nguoidungApiService = NguoidungApiService.getInstance()

        phongApiService.getTenPhongById(nguoiDung.ma_phong).enqueue(object : Callback<PhongApiService.TenPhongResponse> {
            override fun onResponse(call: Call<PhongApiService.TenPhongResponse>, response: Response<PhongApiService.TenPhongResponse>) {
                if (response.isSuccessful) {
                    val tenPhongResponse = response.body()
                    val tenPhong = tenPhongResponse?.ten_phong ?: "N/A"
                    binding.tvTenPhong.text = tenPhong
                } else {
                    binding.tvTenPhong.text = "N/A"
                    Log.e(ContentValues.TAG, "Failed to retrieve room name: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PhongApiService.TenPhongResponse>, t: Throwable) {
                binding.tvTenPhong.text = "N/A"
                Log.e(ContentValues.TAG, "Error retrieving room name: ${t.message}")
            }
        })

        binding.tvMaNguoiDung.text = nguoiDung.ma_nguoi_dung
        binding.tvSDT.text = "SĐT: ${nguoiDung.sdt_nguoi_dung}"
        binding.tvTenNguoiThue.text = "Họ tên: ${nguoiDung.ho_ten_nguoi_dung}"
        binding.edTrangThaiO.isChecked = nguoiDung.trang_thai_o == 1

        // Sử dụng API để lấy mã người đang ở
        nguoidungApiService.getMaNguoiDangOByMaPhong(nguoiDung.ma_phong).enqueue(object : Callback<NguoidungApiService.MaNguoiDangOResponse> {
            override fun onResponse(call: Call<NguoidungApiService.MaNguoiDangOResponse>, response: Response<NguoidungApiService.MaNguoiDangOResponse>) {
                if (response.isSuccessful) {
                    val maNguoiDangO = response.body()?.ma_nguoi_dang_o
                    // Thiết lập trạng thái CheckBox cho chủ hợp đồng
                    binding.edTrangThaiChuHopDong.isChecked = nguoiDung.ma_nguoi_dung == maNguoiDangO
                    // Log để kiểm tra dữ liệu
                    Log.d("BindViewHolder", "MaNguoiDangO: $maNguoiDangO")
                } else {
                    binding.edTrangThaiChuHopDong.isChecked = false
                    Log.e(ContentValues.TAG, "Failed to retrieve maNguoiDangO: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<NguoidungApiService.MaNguoiDangOResponse>, t: Throwable) {
                binding.edTrangThaiChuHopDong.isChecked = false
                Log.e(ContentValues.TAG, "Error retrieving maNguoiDangO: ${t.message}")
            }
        })

        binding.layoutPhone.setOnClickListener {
            goiDien(nguoiDung.sdt_nguoi_dung, binding.root.context)
        }
        binding.layoutMessage.setOnClickListener {
            nhanTin(nguoiDung.sdt_nguoi_dung, "", binding.root.context)
        }

        binding.layoutChuyenChiTietNguoiThue.setOnClickListener {
            val dialogBinding = DialogChiTietNguoiThueBinding.inflate(LayoutInflater.from(binding.root.context))
            val bottomSheetDialog = BottomSheetDialog(binding.root.context)

            bottomSheetDialog.setContentView(dialogBinding.root)
            dialogBinding.tvChiTietNguoiDungTenPhong.text = "Tên phòng: ${binding.tvTenPhong.text}"
            dialogBinding.tvChiTietNguoiThueHoTen.text = nguoiDung.ho_ten_nguoi_dung
            dialogBinding.tvChiTietNguoiThueSDT.text = nguoiDung.sdt_nguoi_dung
            dialogBinding.tvChiTietNguoiThueNgaySinh.text = nguoiDung.nam_sinh
            dialogBinding.tvChiTietNguoiThueCCCD.text = nguoiDung.cccd
            dialogBinding.tvChiTietNguoiThueQueQuan.text = nguoiDung.que_quan

            nguoidungApiService.getMaNguoiDangOByMaPhong(nguoiDung.ma_phong).enqueue(object : Callback<NguoidungApiService.MaNguoiDangOResponse> {
                override fun onResponse(call: Call<NguoidungApiService.MaNguoiDangOResponse>, response: Response<NguoidungApiService.MaNguoiDangOResponse>) {
                    dialogBinding.tvLoaiNguoiThue.text = if (nguoiDung.ma_nguoi_dung == response.body()?.ma_nguoi_dang_o) "Là chủ hợp đồng" else "Là thành viên"
                }

                override fun onFailure(call: Call<NguoidungApiService.MaNguoiDangOResponse>, t: Throwable) {
                    dialogBinding.tvLoaiNguoiThue.text = "Là thành viên"
                }
            })

            dialogBinding.btnDongChiTietNguoiThue.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }
    }

    private fun nhanTin(sdt: String, message: String, context: Context) {
        val uri = Uri.parse("smsto:+$sdt")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        with(intent) {
            putExtra("address", "+$sdt")
            putExtra("sms_body", message)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context)
            if (defaultSmsPackageName != null) intent.setPackage(defaultSmsPackageName)
        }
        context.startActivity(intent)
    }

    private fun goiDien(sdt: String, context: Context) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$sdt")
        context.startActivity(dialIntent)
    }
}


class NguoiThueAdapter(
    private var listNguoiDung: List<NguoiDung>,
    private val onClick: KhachThueInterface
) : RecyclerView.Adapter<NguoiThueViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NguoiThueViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemNguoiThueBinding.inflate(inflater, parent, false)
        return NguoiThueViewHolder(binding)
    }

    override fun getItemCount() = listNguoiDung.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NguoiThueViewHolder, position: Int) {
        val user = listNguoiDung[position]
        holder.apply {
            bind(user)
        }
        holder.itemView.setOnClickListener {
            onClick.OnClickKhachThue(position)
        }
    }

    fun updateList(newList: List<NguoiDung>) {
        listNguoiDung = newList
        notifyDataSetChanged()
    }
}
