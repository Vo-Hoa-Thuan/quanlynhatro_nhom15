package ct07n.hcmact.quanlynhatro_nhom15.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentPhongDaO
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentPhongTrong
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentTatCaPhong

class ViewpagerDanhSachPhongAdapter(fragmentManager: FragmentManager, lifecylce: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecylce) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                FragmentTatCaPhong()
            }
            1 -> {
                FragmentPhongTrong()
            }
            else ->{
                FragmentPhongDaO()
            }
        }
    }
}