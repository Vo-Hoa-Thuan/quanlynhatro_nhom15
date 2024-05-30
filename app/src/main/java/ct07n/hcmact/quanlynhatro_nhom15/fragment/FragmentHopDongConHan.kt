package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.adapter.HopDongPhongConHanAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentHopDongConHanBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class FragmentHopDongConHan : Fragment() {
    private lateinit var binding: FragmentHopDongConHanBinding
    private var listHopDongConHan = listOf<HopDong>()
    private var maKhu = ""
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHopDongConHanBinding.inflate(inflater, container, false)

        val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "") ?: ""
        reLoadList()
        return binding.root
    }

    private fun reLoadList() {
        val hopDongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)
        hopDongApiService.getHopDongConHanByMaKhu(maKhu).enqueue(object : Callback<List<HopDong>> {
            override fun onResponse(call: Call<List<HopDong>>, response: Response<List<HopDong>>) {
                if (response.isSuccessful) {
                    listHopDongConHan = response.body() ?: emptyList()
                    val hopDongAdapter = HopDongPhongConHanAdapter(listHopDongConHan, this@FragmentHopDongConHan)
                    binding.rcyConHetHan.adapter = hopDongAdapter
                    binding.rcyConHetHan.layoutManager = LinearLayoutManager(requireContext())
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

    fun ketThucHopDong(hopDong: HopDong) {
        val intent = Intent(context, ActivityKetThucHopDong::class.java)
        intent.putExtra("hopDong", hopDong)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}