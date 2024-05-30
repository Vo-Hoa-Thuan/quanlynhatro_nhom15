package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityCapNhatKhachThue
import ct07n.hcmact.quanlynhatro_nhom15.activity.THONG_TIN_PHONG
import ct07n.hcmact.quanlynhatro_nhom15.activity.FILE_NAME
import ct07n.hcmact.quanlynhatro_nhom15.activity.MA_KHU_KEY

import ct07n.hcmact.quanlynhatro_nhom15.adapter.*
import ct07n.hcmact.quanlynhatro_nhom15.adapter.KhachThueInterface
import ct07n.hcmact.quanlynhatro_nhom15.adapter.NguoiThueAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentNguoiTrongPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentNguoiThueTrongPhong : Fragment() {
    private var maKhu = ""
    private var maPhong = ""
    var listNguoiDung = listOf<NguoiDung>()
    private lateinit var binding: FragmentNguoiTrongPhongBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNguoiTrongPhongBinding.inflate(layoutInflater)
        val srf2 = binding.root.context.getSharedPreferences(THONG_TIN_PHONG, Context.MODE_PRIVATE)
        maPhong = srf2.getString(MA_PHONG_TRONG_CHI_TIET_PHONG, "")!!
        binding = FragmentNguoiTrongPhongBinding.inflate(inflater, container, false)
        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!

        Log.d("FragmentNguoiThueTrongPhong", "maKhu: $maKhu, maPhong: $maPhong")


        fetchNguoiDungData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        fetchNguoiDungData()
        Log.d("aaaa", "onResume: called ")
    }

    private fun setupRecyclerView() {
        val nguoiThueAdapter = NguoiThueAdapter(listNguoiDung, object : KhachThueInterface {
            override fun OnClickKhachThue(pos: Int) {
                val nguoiDung = listNguoiDung[pos]
                val intent = Intent(requireContext(), ActivityCapNhatKhachThue::class.java)
                intent.putExtra("khachThue", nguoiDung)
                startActivity(intent)
            }
        })
        binding.rcyNguoiDangOTrongPhong.adapter = nguoiThueAdapter
        binding.rcyNguoiDangOTrongPhong.layoutManager = LinearLayoutManager(activity)
    }


    private fun fetchNguoiDungData() {
        val nguoidungApiServiceService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        val call = nguoidungApiServiceService.getAllInNguoiDangOByMaKhu(maKhu)

        call.enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    listNguoiDung = response.body()?.filter { it.ma_phong == maPhong } ?: emptyList()

                    Log.d("FragmentNguoiThueTrongPhong", "Fetched data: $listNguoiDung")

                    setupRecyclerView()
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                Log.e("FragmentNguoiThueTrongPhong", "Error fetching data: ${t.message}")
            }
        })
    }
}
