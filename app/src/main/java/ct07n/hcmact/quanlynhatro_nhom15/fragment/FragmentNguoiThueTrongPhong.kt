package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.activity.THONG_TIN_PHONG
import ct07n.hcmact.quanlynhatro_nhom15.adapter.*
import ct07n.hcmact.quanlynhatro_nhom15.api.NguoidungApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentNguoiTrongPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentNguoiThueTrongPhong : Fragment() {
    private var maKhu=""
    private var maPhong=""
    private lateinit var nguoiDungApiService: NguoidungApiService
    private lateinit var binding: FragmentNguoiTrongPhongBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNguoiTrongPhongBinding.inflate(inflater,container,false)
        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val srf2 = binding.root.context.getSharedPreferences(THONG_TIN_PHONG, Context.MODE_PRIVATE)
        maPhong = srf2.getString(MA_PHONG_TRONG_CHI_TIET_PHONG,"")!!
        maKhu = srf.getString(MA_KHU_KEY, "")!!
        nguoiDungApiService = RetrofitClient.instance.create(NguoidungApiService::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNguoiDungInPhong()
    }

    private fun getNguoiDungInPhong() {
        nguoiDungApiService.getNguoiDungByMaPhong( maPhong).enqueue(object : Callback<List<NguoiDung>> {
            override fun onResponse(call: Call<List<NguoiDung>>, response: Response<List<NguoiDung>>) {
                if (response.isSuccessful) {
                    val listNguoiDung = response.body() ?: emptyList()
                    setUpRecyclerView(listNguoiDung)
                } else {
                    Log.e("FragmentNguoiThueTrongPhong", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<NguoiDung>>, t: Throwable) {
                Log.e("FragmentNguoiThueTrongPhong", "Network Error: ${t.message}")
            }
        })
    }

    private fun setUpRecyclerView(listNguoiDung: List<NguoiDung>) {
        val nguoiThueAdapter = NguoiThueAdapter(listNguoiDung, object : KhachThueInterface {
            override fun OnClickKhachThue(pos: Int) {
//                val nguoiDung = listNguoiDung[pos]
//                val intent = Intent(requireContext(), ActivityCapNhatKhachThue::class.java)
//                intent.putExtra("khachThue", nguoiDung)
//                startActivity(intent)
            }
        })
        binding.rcyNguoiDangOTrongPhong.adapter = nguoiThueAdapter
        binding.rcyNguoiDangOTrongPhong.layoutManager = LinearLayoutManager(activity)
    }
}
