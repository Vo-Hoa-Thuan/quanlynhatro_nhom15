package ct07n.hcmact.quanlynhatro_nhom15.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentTongQuan
import ct07n.hcmact.quanlynhatro_nhom15.fragment.FragmentQuanLy
class ViewPagerTrangChuAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                FragmentQuanLy()
            }
            1 -> {
                FragmentTongQuan()
            }
            else ->{
                FragmentQuanLy()
            }
        }
    }
}