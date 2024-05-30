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
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogThemKhachThueBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentNguoiDaOBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.adapter.KhachThueInterface // Import interface
import ct07n.hcmact.quanlynhatro_nhom15.adapter.MaPhongSpinnerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FragmentNguoiDaO : Fragment() {

    private lateinit var binding: FragmentNguoiDaOBinding
    private lateinit var maKhu: String
    private lateinit var maPhong: String
    private var listNguoiDung = mutableListOf<NguoiDung>()
    private lateinit var nguoiThueAdapter: NguoiThueAdapter

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
                onClickNguoiDung(position)
            }
        })
        binding.rcyNguoiDaO.adapter = nguoiThueAdapter
        binding.rcyNguoiDaO.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onClickNguoiDung(position: Int) {
        val nguoiDung = listNguoiDung[position]
        val intent = Intent(requireContext(), ActivityCapNhatKhachThue::class.java)
        intent.putExtra("hopDong", nguoiDung)
        startActivity(intent)
    }

    private fun showAddNguoiThueDialog() {
        val dialogBinding = DialogThemKhachThueBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).create()

        val phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        phongApiService.getAllPhong().enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    val listPhong = response.body() ?: emptyList()
                    // Initialize MaPhongSpinner adapter with the list of Phong objects
                    val spinnerAdapter = MaPhongSpinnerAdapter(requireContext(), listPhong)
                    dialogBinding.spinnerThemNguoiDung.adapter = spinnerAdapter
                } else {
                    showErrorSnackbar("Error loading phong data")
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                showErrorSnackbar("Network request failed: ${t.message}")
            }
        })

        dialogBinding.btnLuuThemNguoiDung.setOnClickListener {
            // Thực hiện gọi API để đẩy dữ liệu lên database
            val nguoiDungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
            val maNguoiDung = UUID.randomUUID().toString()
            val nguoiDung = NguoiDung(
                maNguoiDung,
                dialogBinding.edHoTenThemNguoiDung.text.toString(),
                dialogBinding.edNgaySinhThemNguoiDung.text.toString(),
                dialogBinding.edSDTThemNguoiDung.text.toString(),
                dialogBinding.edQueQuanThemNguoiDung.text.toString(),
                dialogBinding.edCCCDThemNguoiDung.text.toString(),
                maPhong,
                0,
                0

            )

            nguoiDungApiService.insert(nguoiDung).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Snackbar.make(it, "Thêm người dùng thành công", Snackbar.LENGTH_SHORT).show()
                        dialog.dismiss()
                        onResume()
                    } else {
                        showErrorSnackbar("Failed to add người dùng")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showErrorSnackbar("Network request failed: ${t.message}")
                }
            })
        }

        dialogBinding.btnHuyThemNguoiDung.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setView(dialogBinding.root)
        dialog.show()
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload() {
        // Load lại dữ liệu từ API
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
