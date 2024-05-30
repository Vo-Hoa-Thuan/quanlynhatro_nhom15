package ct07n.hcmact.quanlynhatro_nhom15.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong

class MaPhongSpinnerAdapter(context: Context, val list: List<Phong>)
    : ArrayAdapter<Phong>(context, R.layout.layout_item_spinner_ma_phong, list) {

    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    @SuppressLint("MissingInflatedId")
    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.layout_item_spinner_ma_phong, parent, false)
        val tvTenPhong = view.findViewById<TextView>(R.id.tvTenPhongSpinner)
        val phong = list[position]
        tvTenPhong.text = phong.ten_phong
        return view
    }
}
