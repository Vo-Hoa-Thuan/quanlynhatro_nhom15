package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ct07n.hcmact.quanlynhatro_nhom15.adapter.FILE_NAME
import ct07n.hcmact.quanlynhatro_nhom15.adapter.MA_KHU_KEY


import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentThongbaoBinding


class FragmentThongBao:Fragment() {
    private lateinit var binding: FragmentThongbaoBinding

    private var maKhu=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThongbaoBinding.inflate(inflater,container,false)
        val srf=binding.root.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        maKhu=srf.getString(MA_KHU_KEY, "")!!


        binding.rcv.layoutManager= LinearLayoutManager(context)
        return binding.root
    }
}