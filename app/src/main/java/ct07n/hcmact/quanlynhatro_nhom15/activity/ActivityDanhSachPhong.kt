package ct07n.hcmact.quanlynhatro_nhom15.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import ct07n.hcmact.quanlynhatro_nhom15.R

import ct07n.hcmact.quanlynhatro_nhom15.adapter.ViewpagerDanhSachPhongAdapter
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityDanhSachPhongBinding
import ct07n.hcmact.quanlynhatro_nhom15.adapter.PhongTroAdapter


class ActivityDanhSachPhong : AppCompatActivity() {
    private lateinit var binding: ActivityDanhSachPhongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDanhSachPhongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbDanhSachPhong)
        val ab = supportActionBar
        if (ab != null){
            ab.setHomeAsUpIndicator(R.drawable.black_left)
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        val adapter = ViewpagerDanhSachPhongAdapter(supportFragmentManager, lifecycle)
        binding.viewpagerDanhSachPhong.adapter = adapter
        TabLayoutMediator(binding.tabDanhSachPhong, binding.viewpagerDanhSachPhong) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = "Tất cả phòng"
                }
                1 -> {
                    tab.text = "Phòng trống"
                }
                else -> tab.text = "Phòng đang ở"
            }
        }.attach()
    }
    override fun  onOptionsItemSelected(item : MenuItem): Boolean {
        val id : Int = item.getItemId();
        if (id==android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item);
    }
}