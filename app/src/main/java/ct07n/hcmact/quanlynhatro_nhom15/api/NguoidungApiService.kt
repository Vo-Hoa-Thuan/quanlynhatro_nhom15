package ct07n.hcmact.quanlynhatro_nhom15.api

import ct07n.hcmact.quanlynhatro_nhom15.model.NguoiDung

import retrofit2.Call
import retrofit2.http.*

interface NguoidungApiService {
    @GET("nguoidung")
    fun getAll(): Call<List<NguoiDung>>

    @POST("nguoidung")
    fun insert(@Body nguoiDung: NguoiDung): Call<Void>

    @GET("nguoidung/{ma_nguoi_dung}")
    fun getById(@Path("ma_nguoi_dung") maNguoiDung: String): Call<NguoiDung>

    @DELETE("nguoidung/{ma_nguoi_dung}")
    fun delete(@Path("ma_nguoi_dung") maNguoiDung: String): Call<Void>

    @PUT("nguoidung/{ma_nguoi_dung}")
    fun update(@Path("ma_nguoi_dung") maNguoiDung: String, @Body nguoiDung: NguoiDung): Call<Void>

    @GET("nguoidung/tenphong")
    fun getAllByTenPhong(@Query("tenPhong") tenPhong: String): Call<List<NguoiDung>>

    @GET("nguoidung/makhu")
    fun getAllByMaKhu(@Query("maKhu") maKhu: String): Call<List<NguoiDung>>

    @GET("nguoidung/maphong")
    fun getNguoiDungByMaPhong(@Query("maPhong") maPhong: String): Call<List<NguoiDung>>

    @GET("nguoidung/{maPhong}/tennguoidung1")
    fun getTenNguoiDungByMaPhong1(@Path("maPhong") maPhong: String): Call<NguoiDung>

    @GET("nguoidung/trangthaichodat")
    fun getNguoiDungByTrangThai(@Query("trangThai") trangThai: Int, @Query("maPhong") maPhong: String): Call<String>

    @GET("nguoidung/{maPhong}/tennguoidung")
    fun getTenNguoiDungByMaPhong(@Path("maPhong") maPhong: String): Call<String>

    @GET("nguoidung/{maPhong}/songuoidung")
    fun getSoNguoiDungByMaPhong(@Path("maPhong") maPhong: String): Call<Int>

    @GET("nguoidung/{maPhong}/songuoio")
    fun getSoNguoiOByMaPhong(@Path("maPhong") maPhong: String): Call<Int>

    @GET("nguoidung/{maPhong}/listnguoidung")
    fun getListNguoiDungByMaPhong(@Path("maPhong") maPhong: String): Call<List<NguoiDung>>

    @GET("nguoidung/{maPhong}/listtrangthaihddung")
    fun getListTrangThaiHDDungByMaPhong(@Path("maPhong") maPhong: String): Call<List<NguoiDung>>

    @GET("nguoidung/dang-o/{maKhu}")
    fun getAllInNguoiDangOByMaKhu(@Path("maKhu") maKhu: String): Call<List<NguoiDung>>

    @GET("nguoidung/da-o/{maKhu}")
    fun getAllInNguoiDaOByMaKhu(@Path("maKhu") maKhu: String): Call<List<NguoiDung>>

    @GET("nguoidung/{maPhong}/tennguoidango")
    fun getTenNguoiDangOByMaPhong(@Path("maPhong") maPhong: String): Call<String>

    @GET("nguoidung/{maPhong}/tennguoidao")
    fun getTenNguoiDaOByMaPhong(@Path("maPhong") maPhong: String): Call<String>

    @GET("nguoidung/{maPhong}/manguoidango")
    fun getMaNguoiDangOByMaPhong(@Path("maPhong") maPhong: String): Call<String>

    @PUT("nguoidung/{maPhong}/updatetrangthaidao")
    fun updateTrangThaiNguoiDungThanhDaO(@Path("maPhong") maPhong: String): Call<Void>
}