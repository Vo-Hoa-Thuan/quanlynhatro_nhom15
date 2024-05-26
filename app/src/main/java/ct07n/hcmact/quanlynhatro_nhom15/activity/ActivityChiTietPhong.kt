package ct07n.hcmact.quanlynhatro_nhom15.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.MA_PHONG_TRONG_CHI_TIET_PHONG
import ct07n.hcmact.quanlynhatro_nhom15.adapter.ViewPagerChiTietPhongAdapter
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityChiTietPhongBinding

const val THONG_TIN_PHONG="THONG_TIN_PHONG"
class ActivityChiTietPhong : AppCompatActivity() {
    private lateinit var binding: ActivityChiTietPhongBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietPhongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbChiTietPhong)
        val ab = supportActionBar
        if (ab != null){
            ab.setHomeAsUpIndicator(R.drawable.black_left)
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
        val adapter = ViewPagerChiTietPhongAdapter(supportFragmentManager, lifecycle)
        binding.viewPager2ChiTietPhong.adapter = adapter
        TabLayoutMediator(
            binding.tabLayoutChiTietPhong,
            binding.viewPager2ChiTietPhong
        ) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.setIcon(R.drawable.home_icon)
                    tab.text = "Thông tin"
                }

                else -> {
                    tab.setIcon(R.drawable.icon_nguoi_thue)
                    tab.text = "Người Thuê"
                }
            }

        }.attach()
        val srf=getSharedPreferences(THONG_TIN_PHONG, MODE_PRIVATE)
        val maPhong=intent.getStringExtra(MA_PHONG_TRONG_CHI_TIET_PHONG)!!
        srf.edit().putString(MA_PHONG_TRONG_CHI_TIET_PHONG, maPhong).apply()
    }
    override fun  onOptionsItemSelected(item : MenuItem): Boolean {
        val id : Int = item.getItemId();
        if (id==android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item);
    }
}