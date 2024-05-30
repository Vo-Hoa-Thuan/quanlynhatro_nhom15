package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityCapNhatKhachThue
import ct07n.hcmact.quanlynhatro_nhom15.activity.FILE_NAME
import ct07n.hcmact.quanlynhatro_nhom15.activity.MA_KHU_KEY
import ct07n.hcmact.quanlynhatro_nhom15.adapter.KhachThueInterface
import ct07n.hcmact.quanlynhatro_nhom15.adapter.MaPhongSpinner
import ct07n.hcmact.quanlynhatro_nhom15.adapter.NguoiThueAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogThemKhachThueBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentNguoiDangOBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FragmentNguoiDangO : Fragment() {
    private lateinit var binding: FragmentNguoiDangOBinding
    private var maKhu = ""
    private lateinit var nguoiDungApiService: NguoidungApiService
    private lateinit var phongApiService: PhongApiService
    private lateinit var nguoiThueAdapter: NguoiThueAdapter
    private var listNguoiDung = mutableListOf<NguoiDung>()
    private var maPhong = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNguoiDangOBinding.inflate(inflater, container, false)

        // Khởi tạo Retrofit client
        nguoiDungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)

        // Inflate the layout for this fragment
        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!

        // Setup RecyclerView
        setupRecyclerView()

        // Gọi API để lấy danh sách người dùng đang ở
        nguoiDungApiService.getAllInNguoiDangOByMaKhu(maKhu).enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    listNguoiDung = response.body()?.toMutableList() ?: mutableListOf()
                    nguoiThueAdapter.updateList(listNguoiDung)
                } else {
                    // Xử lý lỗi nếu cần
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                // Xử lý lỗi nếu cần
            }
        })

        // Setup thêm người dùng
        binding.imgAddNguoiThue.setOnClickListener {
            showAddNguoiThueDialog()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        nguoiThueAdapter = NguoiThueAdapter(listNguoiDung, object : KhachThueInterface {
            override fun OnClickKhachThue(pos: Int) {
                val nguoiDung = listNguoiDung[pos]
                val intent = Intent(requireContext(), ActivityCapNhatKhachThue::class.java)
                intent.putExtra("khachThue", nguoiDung)
                startActivity(intent)
            }
        })
        binding.rcyNguoiDangO.adapter = nguoiThueAdapter
        binding.rcyNguoiDangO.layoutManager = LinearLayoutManager(activity)
    }

    private fun showAddNguoiThueDialog() {
        val dialogBinding = DialogThemKhachThueBinding.inflate(LayoutInflater.from(activity))
        val dialog = AlertDialog.Builder(activity).create()

        phongApiService.getAllInPhongByMaKhu(maKhu).enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    val listPhong = response.body() ?: emptyList()
                    val spinnerAdapter = MaPhongSpinner(requireActivity(), listPhong)
                    dialogBinding.spinnerThemNguoiDung.adapter = spinnerAdapter
                    dialogBinding.spinnerThemNguoiDung.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            maPhong = listPhong[position].ma_phong
                            Toast.makeText(activity, "Tên phòng: ${listPhong[position].ten_phong}", Toast.LENGTH_SHORT).show()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                } else {
                    showErrorSnackbar("Error loading phòng data")
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                showErrorSnackbar("Network request failed: ${t.message}")
            }
        })

        dialogBinding.btnLuuThemNguoiDung.setOnClickListener {
            val maNguoiDung = UUID.randomUUID().toString()
            val nguoiDung = NguoiDung(
                maNguoiDung,
                dialogBinding.edHoTenThemNguoiDung.text.toString(),
                dialogBinding.edNgaySinhThemNguoiDung.text.toString(),
                dialogBinding.edSDTThemNguoiDung.text.toString(),
                dialogBinding.edCCCDThemNguoiDung.text.toString(),
                dialogBinding.edQueQuanThemNguoiDung.text.toString(),
                maPhong,
                1,
                0
            )

            nguoiDungApiService.insert(nguoiDung).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("API_SUCCESS", "User added successfully")
                        Snackbar.make(dialogBinding.root, "Thêm người dùng thành công", Snackbar.LENGTH_SHORT).show()
                        dialog.dismiss()
                        onResume()
                    } else {
                        Log.e("API_ERROR", "Failed to add người dùng: ${response.errorBody()?.string()}")
                        showErrorSnackbar("Failed to add người dùng")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("API_FAILURE", "Network request failed: ${t.message}")
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
        nguoiDungApiService.getAllInNguoiDangOByMaKhu(maKhu).enqueue(object : Callback<List<NguoiDung>> {
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
