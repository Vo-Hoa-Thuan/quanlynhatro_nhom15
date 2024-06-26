package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.activity.*
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogDangtinBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentDangtinBinding

class FragmentDangTin : Fragment() {
    private lateinit var binding: FragmentDangtinBinding
    private var maKhu = "";

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDangtinBinding.inflate(layoutInflater)

        // Lấy tên chủ trọ từ SharedPreferences
        val sharedPreferences = context?.getSharedPreferences("", Context.MODE_PRIVATE)
        val tenChuTro = sharedPreferences?.getString("ten_chu_tro", "Tên Chủ Trọ")

        // Cập nhật TextView với tên chủ trọ
        binding.tvTenChuTro.text = tenChuTro

        binding.tvDangtin.setOnClickListener {
            val bundle = AlertDialog.Builder(context).create()
            val dialog = DialogDangtinBinding.inflate(LayoutInflater.from(context))

            dialog.btnDangTin.setOnClickListener {
                val tieuDe = dialog.edTieuDe.text.toString()
                val noiDung = dialog.edNoiDung.text.toString()
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, """
                        $tieuDe
                        $noiDung
                    """.trimIndent())

                }
                startActivity(reportIntent)
                bundle.dismiss()

            }
            dialog.imgCloseDangTin.setOnClickListener {
                bundle.cancel()
            }
            bundle.setView(dialog.root)
            bundle.show()

        }
        return binding.root
    }
}
