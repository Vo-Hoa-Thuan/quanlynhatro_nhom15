package ct07n.hcmact.quanlynhatro_nhom15.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ct07n.hcmact.quanlynhatro_nhom15.databinding.DialogDangtinBinding
import ct07n.hcmact.quanlynhatro_nhom15.databinding.FragmentDangtinBinding

class FragmentDangTin:Fragment() {
    private lateinit var binding: FragmentDangtinBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDangtinBinding.inflate(layoutInflater)

        binding.tvDangtin.setOnClickListener {
            val bundle = AlertDialog.Builder(context).create()
            val dialog = DialogDangtinBinding.inflate(LayoutInflater.from(context))

            dialog.btnDangTin.setOnClickListener {
                val tieuDe=dialog.edTieuDe.text.toString()
                val noiDung =dialog.edNoiDung.text.toString()
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