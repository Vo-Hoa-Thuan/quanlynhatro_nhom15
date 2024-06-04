package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AppCompatActivity
import ct07n.hcmact.quanlynhatro_nhom15.activity.ActivityThemPhong
import ct07n.hcmact.quanlynhatro_nhom15.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTroAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentPhongTrongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentPhongTrong : Fragment() {
    private var _binding: FragmentPhongTrongBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "FragmentPhongTrongBinding is null" }

    private lateinit var phongAdapter: PhongTroAdapter
    private var listPhong = mutableListOf<Phong>()
    private lateinit var phongApiService: PhongApiService
    private var maKhu = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhongTrongBinding.inflate(inflater, container, false)
        phongApiService = RetrofitClient.instance.create(PhongApiService::class.java)
        val sharedPreferences = activity?.getSharedPreferences(FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        maKhu = sharedPreferences?.getString(MA_KHU_KEY, "").orEmpty()
        Log.d("FragmentPhongTrong", "Retrieved maKhu: $maKhu")

        if (maKhu.isEmpty()) {
            Log.e("FragmentPhongTrong", "maKhu is empty or null.")
            // Display a message to the user if necessary
        } else {
            Log.d("FragmentPhongTrong", "maKhu: $maKhu")
        }

        // Call API to get the list of empty rooms
        getPhongTrongFromApi()

        // Navigate to ActivityThemPhong
        binding.imgAddPhong.setOnClickListener {
            val intent = Intent(activity, ActivityThemPhong::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize adapter and layout manager for RecyclerView
        phongAdapter = PhongTroAdapter(requireContext(),listPhong)
        binding.rcyPhongTrong.apply {
            adapter = phongAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getPhongTrongFromApi() {
        if (maKhu.isEmpty()) {
            Log.e("FragmentPhongTrong", "maKhu is not set, aborting API call.")
            return
        }
        val call = phongApiService.getAllInPhongByMaKhu(maKhu)
        call.enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    val phongList = response.body()?.filter { it.trang_thai_phong == 0 } ?: emptyList()
                    listPhong.clear()
                    listPhong.addAll(phongList)
                    listPhong.sortBy { it.ten_phong }
                    phongAdapter.notifyDataSetChanged()
                } else {
                    Log.e("FragmentPhongTrong", "Failed to retrieve the list of empty rooms from the server.")
                }
            }

            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                Log.e("FragmentPhongTrong", "Failed to make API call: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getPhongTrongFromApi()
    }
}
