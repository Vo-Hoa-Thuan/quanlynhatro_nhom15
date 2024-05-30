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
        //==================================

        /*val srf = binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu = srf.getString(MA_KHU_KEY, "")!!
        listHopDongSapHetHan = HopDongDao(this@ActivityXuLyPhong).getHopDongSapHetHanByMaKhu(maKhu,0)
        //listHopDong=HopDongDao(this@ActivityDanhSachHopDong).getAllInHopDong()
        val hopDongAdapter= HopDongPhongHetHanAdapter(listHopDongSapHetHan,object :HopDongInterface{
            override fun OnClickHopDong(pos: Int) {
                val intent = Intent(this@ActivityXuLyPhong,ActivityKetThucHopDong::class.java)
                //val bundle = Bundle()
//                bundle.putString("tenPhong",listPhongChuaCoHopDong[pos].ten_phong)
//                bundle.putString("maPhong",listPhongChuaCoHopDong[pos].ma_phong)
                 //intent.putExtras(bundle)
                startActivity(intent)
            }

        })
        binding.recyclerXuLyPhong.adapter = hopDongAdapter
        binding.recyclerXuLyPhong.layoutManager= LinearLayoutManager(this)*/

        val adapter = ViewpagerDanhSachHopDongXuLyAdapter(supportFragmentManager, lifecycle)
        binding.viewpagerDanhSachHopDong.adapter = adapter
        TabLayoutMediator(binding.tabDanhSachHopDong, binding.viewpagerDanhSachHopDong) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = "Còn hạn"
                }
                1 -> {
                    tab.text = "Sắp hết hạn"
                }
                2 -> {
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