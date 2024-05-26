package ct07n.hcmact.quanlynhatro_nhom15.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Telephony
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
    val binding: LayoutItemNguoiThueBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(nguoiDung: NguoiDung) {
        val phongApiService = PhongApiService.getInstance()
        val nguoidungApiService = NguoidungApiService.getInstance()

        phongApiService.getTenPhongById(nguoiDung.ma_phong).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    binding.tvTenPhong.text = response.body() ?: "N/A"
                } else {
                    binding.tvTenPhong.text = "N/A"
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                binding.tvTenPhong.text = "N/A"
            }
        })

        binding.tvMaNguoiDung.text = nguoiDung.ma_nguoi_dung
        binding.tvSDT.text = "SĐT: ${nguoiDung.sdt_nguoi_dung}"
        binding.tvTenNguoiThue.text = "Họ tên: ${nguoiDung.ho_ten_nguoi_dung}"
        binding.edTrangThaiO.isChecked = nguoiDung.trang_thai_o == 1

        nguoidungApiService.getMaNguoiDangOByMaPhong(nguoiDung.ma_phong).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    binding.edTrangThaiChuHopDong.isChecked = nguoiDung.ma_nguoi_dung == response.body()
                } else {
                    binding.edTrangThaiChuHopDong.isChecked = false
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                binding.edTrangThaiChuHopDong.isChecked = false
            }
        })

        binding.layoutPhone.setOnClickListener {
            goiDien(nguoiDung.sdt_nguoi_dung, binding.root.context)
        }
        binding.layoutMessage.setOnClickListener {
            nhanTin(nguoiDung.sdt_nguoi_dung, "", binding.root.context)
        }

        binding.layoutChuyenChiTietNguoiThue.setOnClickListener {
            val dialog = DialogChiTietNguoiThueBinding.inflate(LayoutInflater.from(binding.root.context))
            val bottomSheetDialog = BottomSheetDialog(binding.root.context)

            bottomSheetDialog.setContentView(dialog.root)
            dialog.tvChiTietNguoiDungTenPhong.text = "Tên phòng: ${binding.tvTenPhong.text}"
            dialog.tvChiTietNguoiThueHoTen.text = nguoiDung.ho_ten_nguoi_dung
            dialog.tvChiTietNguoiThueSDT.text = nguoiDung.sdt_nguoi_dung
            dialog.tvChiTietNguoiThueNgaySinh.text = nguoiDung.nam_sinh
            dialog.tvChiTietNguoiThueCCCD.text = nguoiDung.cccd
            dialog.tvChiTietNguoiThueQueQuan.text = nguoiDung.que_quan

            nguoidungApiService.getMaNguoiDangOByMaPhong(nguoiDung.ma_phong).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    dialog.tvLoaiNguoiThue.text = if (nguoiDung.ma_nguoi_dung == response.body()) "Là chủ hợp đồng" else "Là thành viên"
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    dialog.tvLoaiNguoiThue.text = "Là thành viên"
                }
            })

            dialog.btnDongChiTietNguoiThue.setOnClickListener {
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
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context)
                if (defaultSmsPackageName != null) intent.setPackage(defaultSmsPackageName)
                context.startActivity(intent)
            }
            else -> context.startActivity(intent)
        }
    }

    private fun goiDien(sdt: String, context: Context) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$sdt")
        context.startActivity(dialIntent)
    }
}

class NguoiThueAdapter(
    val listNguoiDung: List<NguoiDung>,
    val onClick: KhachThueInterface
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
}