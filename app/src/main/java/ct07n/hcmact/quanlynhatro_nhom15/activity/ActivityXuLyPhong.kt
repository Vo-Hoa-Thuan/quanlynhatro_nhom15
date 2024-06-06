package ct07n.hcmact.quanlynhatro_nhom15.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import ct07n.hcmact.quanlynhatro_nhom15.adapter.ViewpagerDanhSachHopDongXuLyAdapter
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityXuLyPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong

class ActivityXuLyPhong : AppCompatActivity() {
    private lateinit var binding: ActivityXuLyPhongBinding
    var listHopDongSapHetHan = listOf<HopDong>()
    private var maKhu=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityXuLyPhongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbXuLyPhong)
        val ab = getSupportActionBar()
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        val adapter = ViewpagerDanhSachHopDongXuLyAdapter(supportFragmentManager, lifecycle)
        binding.viewpagerDanhSachHopDong.adapter = adapter
        TabLayoutMediator(binding.tabDanhSachHopDong, binding.viewpagerDanhSachHopDong) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = "Còn hạn"
                }
                1 -> {
                    tab.text = "Hết hạn"
                }
                else -> tab.text = "Còn hạn"
            }
        }.attach()
    }

    fun chuyenActivity(){
//        val intent = Intent(this@ActivityXuLyPhong, ActivityManHinhChinhChuTro::class.java)
//        startActivity(intent)
        finish()
    }
    override fun  onOptionsItemSelected(item : MenuItem): Boolean {
        val id : Int = item.getItemId();
        if (id==android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item);
    }
}