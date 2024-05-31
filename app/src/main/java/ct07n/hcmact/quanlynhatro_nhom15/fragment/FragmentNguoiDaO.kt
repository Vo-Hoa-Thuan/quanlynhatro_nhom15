package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityCapNhatKhachThue
import ct07n.hcmact.quanlynhatro_nhom15.activity.FILE_NAME
import ct07n.hcmact.quanlynhatro_nhom15.activity.MA_KHU_KEY
import ct07n.hcmact.quanlynhatro_nhom15.adapter.NguoiThueAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentNguoiDaOBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.adapter.KhachThueInterface // Import interface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentNguoiDaO : Fragment() {

    private lateinit var binding: FragmentNguoiDaOBinding
    private lateinit var maKhu: String
    private lateinit var nguoiThueAdapter: NguoiThueAdapter
    private var listNguoiDung = mutableListOf<NguoiDung>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNguoiDaOBinding.inflate(inflater, container, false)
        maKhu = requireContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .getString(MA_KHU_KEY, "")!!

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        nguoiThueAdapter = NguoiThueAdapter(listNguoiDung, object : KhachThueInterface {
            override fun OnClickKhachThue(position: Int) {
                showDeleteConfirmDialog(position)
            }
        })
        binding.rcyNguoiDaO.adapter = nguoiThueAdapter
        binding.rcyNguoiDaO.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showDeleteConfirmDialog(position: Int) {
        val nguoiDung = listNguoiDung[position]
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Xác nhận xóa")
            setMessage("Bạn có chắc chắn muốn xóa thông tin người đã thuê này?")
            setPositiveButton("Xóa") { _, _ ->
                deleteNguoiDung(nguoiDung, position)
            }
            setNegativeButton("Hủy", null)
        }.show()
    }

    private fun deleteNguoiDung(nguoiDung: NguoiDung, position: Int) {
        val nguoiDungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        nguoiDungApiService.delete(nguoiDung.ma_nguoi_dung).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    listNguoiDung.removeAt(position)
                    if (listNguoiDung.isEmpty()) {
                        nguoiThueAdapter.notifyDataSetChanged()
                    } else {
                        nguoiThueAdapter.notifyItemRemoved(position)
                    }
                    Snackbar.make(binding.root, "Xóa người dùng thành công", Snackbar.LENGTH_SHORT).show()
                } else {
                    showErrorSnackbar("Failed to delete người dùng")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorSnackbar("Network request failed: ${t.message}")
            }
        })
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload() {
        val nguoiDungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        nguoiDungApiService.getAllInNguoiDaOByMaKhu(maKhu).enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    listNguoiDung.clear()
                    listNguoiDung.addAll(response.body() ?: emptyList())
                    nguoiThueAdapter.notifyDataSetChanged()
                } else {
                    showErrorSnackbar("Failed to load người dùng data")
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                showErrorSnackbar("Network request failed: ${t.message}")
            }
        })
    }
}
