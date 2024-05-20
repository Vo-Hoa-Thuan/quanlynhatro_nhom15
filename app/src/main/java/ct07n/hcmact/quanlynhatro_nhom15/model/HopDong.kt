package ct07n.hcmact.quanlynhatro_nhom15.model

data class HopDong(
    var ma_hop_dong:String,
    var thoi_han: Int,
    var ngay_o:String,
    var ngay_hop_dong:String,
    var ngay_lap_hop_dong:String,
    var anh_hop_dong:String,
    var tien_coc:Int,
    var trang_thai_hop_dong: Int,
    var hieu_luc_hop_dong: Int,
    var ma_phong:String,
    var ma_nguoi_dung:String) : java.io.Serializable{
    companion object{
        const val TB_NAME="hop_dong"
        const val CLM_MA_HOP_DONG="ma_hop_dong"
        const val CLM_THOI_HAN="thoi_han"
        const val CLM_NGAY_O="ngay_o"
        const val CLM_NGAY_LAP_HOP_DONG="ngay_lap_hop_dong"
        const val CLM_NGAY_HOP_DONG="ngay_hop_dong"
        const val CLM_ANH_HOP_DONG="anh_hop_dong"
        const val CLM_TIEN_COC="tien_coc"
        const val CLM_TRANG_THAI_HOP_DONG="trang_thai_hop_dong"
        const val CLM_HIEU_LUC_HOP_DONG="hieu_luc_hop_dong"
        const val CLM_MA_PHONG="ma_phong"
        const val CLM_MA_NGUOI_DUNG="ma_nguoi_dung"
    }
}
