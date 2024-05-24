package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.KhuTroAdapter
import ct07n.hcmact.quanlynhatro_nhom15.adapter.ViewPagerManHinhChinhAdapter
import ct07n.hcmact.quanlynhatro_nhom15.api.HopdongApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.KhuTroApiService
import ct07n.hcmact.quanlynhatro_nhom15.api.RetrofitClient
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityManHinhChinhChuTroBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogDanhSachKhuTroBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

const val FILE_NAME = "file_name"
const val MA_KHU_KEY = "MA_KHU_KEY"

class ActivityManHinhChinhChuTro : AppCompatActivity() {
    private lateinit var binding: ActivityManHinhChinhChuTroBinding
    private var listKhuTro = listOf<KhuTro>()
    private var maKhu = ""
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var khuTroApiService: KhuTroApiService
    private lateinit var hopdongApiService: HopdongApiService
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManHinhChinhChuTroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheetDialog = BottomSheetDialog(this)

        khuTroApiService = RetrofitClient.instance.create(KhuTroApiService::class.java)
        hopdongApiService = RetrofitClient.instance.create(HopdongApiService::class.java)

        val admin = getSharedPreferences(THONG_TIN_DANG_NHAP, MODE_PRIVATE).getString(USERNAME_KEY, "")!!

        fetchKhuTroData(admin)

        binding.imgMenuManHinhChinh.setOnClickListener {
            showKhuTroDialog()
        }

        setupViewPager()
    }

    private fun showKhuTroDialog() {
        TODO("Not yet implemented")
    }

    private fun fetchKhuTroData(admin: String) {
        val call = khuTroApiService.getAllInKhuTroByAdmin(admin)
        call.enqueue(object : Callback<List<KhuTro>> {
            override fun onResponse(call: Call<List<KhuTro>>, response: Response<List<KhuTro>>) {
                if (response.isSuccessful) {
                    listKhuTro = response.body() ?: listOf()
                    initializeKhuTro()
                } else {
                    // Handle the error
                }
            }

            override fun onFailure(call: Call<List<KhuTro>>, t: Throwable) {
                // Handle the error
            }
        })
    }

    private fun initializeKhuTro() {
        val pre: SharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE)
        if (listKhuTro.isEmpty()) {
            val intent = Intent(this@ActivityManHinhChinhChuTro, ActivityHuongDanTaoKhu::class.java)
            startActivity(intent)
            finish()
        }
        when {
            intent.getStringExtra(MA_KHU_KEY) == null -> {
                if (listKhuTro.isNotEmpty()) maKhu = listKhuTro[0].ma_khu_tro
            }
            else -> maKhu = intent.getStringExtra(MA_KHU_KEY)!!
        }
        val khuTro = listKhuTro.find { it.ma_khu_tro == maKhu }
        binding.titleTenKhuTro.text = "Khu " + khuTro?.ten_khu_tro
        pre.edit().putString(MA_KHU_KEY, maKhu).apply()

    }



    private fun setupViewPager() {
        val adapter = ViewPagerManHinhChinhAdapter(supportFragmentManager, lifecycle)
        binding.viewPager2ManHinhChinh.adapter = adapter
        TabLayoutMediator(binding.tabLayoutManHinhChinh, binding.viewPager2ManHinhChinh) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.setIcon(R.drawable.home_icon)
                    tab.text = "Trang chủ"
                }
                1 -> {
                    tab.setIcon(R.drawable.dangtin_icon)
                    tab.text = "Đăng tin"
                }
                2 -> {
                    tab.setIcon(R.drawable.thongbao)
                    tab.text = "Thông báo"
                }
                3 -> {
                    tab.setIcon(R.drawable.canhan_icon)
                    tab.text = "Cá nhân"
                }
                else -> tab.text = "Trang chủ"
            }
        }.attach()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channel: String, name: String, desc: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channel, name, importance)
        notificationChannel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

}
