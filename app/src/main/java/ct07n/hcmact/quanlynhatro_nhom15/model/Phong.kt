package ct07n.hcmact.quanlynhatro_nhom15.model

import java.io.Serializable

data class Phong(
    val ten_phong: String,
    val dien_tich: Int,
    val gia_thue: Long,
    val so_nguoi_o: Int,
    val trang_thai_phong: Int,
    val ma_khu: Int
): Serializable