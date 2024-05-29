package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.os.Bundle
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.adapter.ViewpagerDanhSachNguoiThueAdapter
import ct07n.hcmact.quanlynhatro_nhom15.databinding.ActivityDanhSachNguoiThueBinding


class ActivityDanhSachNguoiThue : AppCompatActivity() {
    private lateinit var binding: ActivityDanhSachNguoiThueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDanhSachNguoiThueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tabDSNguoiThue)

        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.black_left)
        ab?.setDisplayHomeAsUpEnabled(true)

        val adapter = ViewpagerDanhSachNguoiThueAdapter(supportFragmentManager, lifecycle)
        binding.viewpagerDanhSachNguoiThue.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
