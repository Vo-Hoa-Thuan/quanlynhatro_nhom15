package ct07n.hcmact.quanlynhatro_nhom15.model

import java.io.Serializable

data class KhuTro(
    val ma_khu_tro: String,
    val ten_khu_tro: String,
    val dia_chi: String,
    val so_luong_phong: Int,
    val ten_dang_nhap: String
) : Serializable