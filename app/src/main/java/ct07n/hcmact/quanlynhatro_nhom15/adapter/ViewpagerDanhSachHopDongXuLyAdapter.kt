package ct07n.hcmact.quanlynhatro_nhom15.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentHopDongConHan
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentHopDongDaHetHan

class ViewpagerDanhSachHopDongXuLyAdapter (fragmentManager: FragmentManager, lifecylce: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecylce) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                FragmentHopDongConHan()
            }
            1 -> {
                FragmentHopDongDaHetHan()
            }
            else ->{
                FragmentHopDongConHan()
            }
        }
    }
}