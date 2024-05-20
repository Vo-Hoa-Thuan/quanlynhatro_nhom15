package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import ct07n.hcmact.quanlynhatro_nhom15.R

class ActivityManHinhChao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_man_hinh_chao)

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, ActivityDangNhap::class.java)
            startActivity(intent)
            finishAffinity()
        }, 2000)
    }
}
