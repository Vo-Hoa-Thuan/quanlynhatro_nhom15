package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityManHinhChinhChuTroBinding

class ActivityManHinhChinhChuTro : AppCompatActivity() {
    private lateinit var binding: ActivityManHinhChinhChuTroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManHinhChinhChuTroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Các xử lý khi vào màn hình chính của chủ trọ sau khi đăng nhập thành công
        // Ví dụ: Hiển thị danh sách phòng, tạo mới thông tin khu trọ, vv...

        // Ví dụ:
        // binding.btnXemDanhSachPhong.setOnClickListener {
        //     val intent = Intent(this, DanhSachPhongActivity::class.java)
        //     startActivity(intent)
        // }
        // ...
    }
}
