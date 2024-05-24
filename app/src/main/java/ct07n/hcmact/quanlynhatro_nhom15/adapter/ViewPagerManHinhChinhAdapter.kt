package ct07n.hcmact.quanlynhatro_nhom15.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentCaNhan
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentDangTin
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentThongBao
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentTrangChu

class ViewPagerManHinhChinhAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                FragmentTrangChu()
            }
            1 -> {
                FragmentDangTin()
            }
            2 -> {
                FragmentThongBao()
            }
            3 -> {
                FragmentCaNhan()
            }
            else ->{
                FragmentTrangChu()
            }
        }
    }
}