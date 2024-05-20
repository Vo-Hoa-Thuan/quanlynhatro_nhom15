package ct07n.hcmact.quanlynhatro_nhom15.activity

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import ct07n.hcmact.quanlynhatro_nhom15.R

class Loading(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setTitle(null)

        val view: View = LayoutInflater.from(context).inflate(R.layout.animotion_loading, null)
        setContentView(view)
    }
}
