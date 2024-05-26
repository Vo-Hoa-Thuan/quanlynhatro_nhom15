package ct07n.hcmact.quanlynhatro_nhom15.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ct07n.hcmact.quanlynhatro_nhom15.R
import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung
import ct07n.hcmact.quanlynhatro_nhom15.model.Phong

class NguoiThueSpinnerAdapter(reContext: Context, val list: List<NguoiDung>): ArrayAdapter<Phong>(reContext,R.layout.layout_item_spinner_nguoi_thue){
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

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View{
        val rowView : View = LayoutInflater.from(context).inflate(R.layout.layout_item_spinner_nguoi_thue,parent,false)
        val tvTenPhong = rowView.findViewById<TextView>(R.id.tvTenNguoiThueSpinner)
        tvTenPhong.text = list[position].ho_ten_nguoi_dung
        return rowView
    }
}
