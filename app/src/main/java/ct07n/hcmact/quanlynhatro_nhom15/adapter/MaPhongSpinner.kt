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
import ct07n.hcmact.quanlynhatro_nhom15.api.PhongApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MaPhongSpinner(private val recontext: Context, private val apiService: PhongApiService) : ArrayAdapter<Phong>(recontext, R.layout.layout_item_spinner_ma_phong) {

    private var listPhong: List<Phong> = emptyList()

    init {
        // Gọi hàm để lấy dữ liệu từ API khi khởi tạo
        fetchDataFromApi()
    }

    override fun getCount(): Int {
        return listPhong.size
    }

    override fun getItem(position: Int): Phong? {
        return listPhong.getOrNull(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    @SuppressLint("ViewHolder", "MissingInflatedId")
    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = LayoutInflater.from(context).inflate(R.layout.layout_item_spinner_ma_phong, parent, false)
        val tvTenPhong = rowView.findViewById<TextView>(R.id.tvTenPhongSpinner)
        tvTenPhong.text = listPhong[position].ten_phong
        return rowView
    }

    private fun fetchDataFromApi() {
        apiService.getAllPhong().enqueue(object : Callback<List<Phong>> {
            override fun onResponse(call: Call<List<Phong>>, response: Response<List<Phong>>) {
                if (response.isSuccessful) {
                    listPhong = response.body() ?: emptyList()
                    notifyDataSetChanged()
                } else {
                    // Xử lý khi không thành công
                }
            }
            override fun onFailure(call: Call<List<Phong>>, t: Throwable) {
                // Xử lý khi gặp lỗi
            }
        })
    }
}
