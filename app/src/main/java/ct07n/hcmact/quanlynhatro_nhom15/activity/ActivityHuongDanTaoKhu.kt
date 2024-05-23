package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityHuongDanTaoKhuBinding


class ActivityHuongDanTaoKhu : AppCompatActivity() {
    private lateinit var binding: ActivityHuongDanTaoKhuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHuongDanTaoKhuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnHuongDanTaoKhu.setOnClickListener {
            val intent = Intent(this@ActivityHuongDanTaoKhu, ActivityThemKhuTro::class.java)
            startActivity(intent)
            finish()
        }
        binding.imgHuongDanTaoKhu.setOnClickListener {
            ////Nhớ tạo video hướng dẫn
            ///thanhhhhh
            ///vvvvvv
        }
    }
}