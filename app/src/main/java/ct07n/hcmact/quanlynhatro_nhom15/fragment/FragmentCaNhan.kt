package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityDangNhap
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityThongTinChuNha
import ct07n.hcmact.quanlynhatro_nhom15.activity.THONG_TIN_DANG_NHAP
import ct07n.hcmact.quanlynhatro_nhom15.activity.USERNAME_KEY
import ct07n.hcmact.quanlynhatro_nhom15.api.AdminApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentCaNhanBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Admin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCaNhan : Fragment() {
    private lateinit var binding: FragmentCaNhanBinding
    private lateinit var apiService: AdminApiService
    private var username = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCaNhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = RetrofitClient.instance.create(AdminApiService::class.java)

        val pref = requireContext().getSharedPreferences(
            THONG_TIN_DANG_NHAP,
            AppCompatActivity.MODE_PRIVATE
        )
        username = pref.getString(USERNAME_KEY, "")!!

        apiService.getAdmin(username).enqueue(object : Callback<Admin> {
            override fun onResponse(call: Call<Admin>, response: Response<Admin>) {
                if (response.isSuccessful) {
                    val admin = response.body()
                    binding.tvTenChuNha.text = admin?.ho_ten ?: ""
                    binding.tvSDT.text = admin?.sdt ?: ""
                } else {
                    showAlertDialog("Lỗi", "Không thể lấy thông tin người dùng.")
                }
            }

            override fun onFailure(call: Call<Admin>, t: Throwable) {
                showAlertDialog("Lỗi", "Không thể kết nối tới server.")
            }
        })

        binding.tvDangXuat.setOnClickListener {
            showConfirmationDialog("Xác nhận", "Bạn có muốn đăng xuất không?") {
                val intent = Intent(activity, ActivityDangNhap::class.java)
                activity?.finish()
                startActivity(intent)
            }
        }

        binding.tvThongTinChuNha.setOnClickListener {
            val intent = Intent(activity, ActivityThongTinChuNha::class.java)
            startActivity(intent)
        }

        binding.tvCapNhat.setOnClickListener {
            val intent = Intent(activity, ActivityThongTinChuNha::class.java)
            startActivity(intent)
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setNegativeButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
        val confirmationDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        confirmationDialog.setTitle(title)
        confirmationDialog.setMessage(message)
        confirmationDialog.setPositiveButton("Đồng ý") { dialog, _ ->
            onConfirm()
            dialog.dismiss()
        }
        confirmationDialog.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }
        confirmationDialog.show()
    }
}
