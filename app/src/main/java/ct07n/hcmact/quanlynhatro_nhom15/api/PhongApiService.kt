package ct07n.hcmact.quanlynhatro_nhom15.api

import ct07n.hcmact.quanlynhatro_nhom15.model.Phong
import retrofit2.Call
import retrofit2.http.*

interface PhongApiService {
    @POST("phong")
    fun insertPhong(@Body phong: Phong): Call<Void>

    @DELETE("phong/{maPhong}")
    fun xoaPhongById(@Path("maPhong") maPhong: String): Call<Void>

    @PUT("phong/{maPhong}")
    fun updatePhong(@Path("maPhong") maPhong: String, @Body phong: Phong): Call<Void>

    @PUT("phong/{maPhong}/dang-o")
    fun updateTrangThaiPhongThanhDangO(@Path("maPhong") maPhong: String): Call<Void>

    @PUT("phong/{maPhong}/da-o")
    fun updateTrangThaiPhongThanhDaO(@Path("maPhong") maPhong: String): Call<Void>

    @GET("phong/ma-khu/{maKhu}")
    fun getAllInPhongByMaKhu(@Path("maKhu") maKhu: String): Call<List<Phong>>

    @GET("phong/ten-khu-tro/{tenKhuTro}")
    fun getAllInPhongByTenKhuTro(@Path("tenKhuTro") tenKhuTro: String): Call<List<Phong>>

    @GET("phong/{id}")
    fun getPhongById(@Path("id") id: String): Call<Phong>

    @GET("phong/{id}/ten")
    fun getTenPhongById(@Path("id") id: String): Call<String>

    @GET("phong/chua-co-hop-dong/{maKhu}")
    fun getPhongChuaCoHopDong(@Path("maKhu") maKhu: String): Call<List<Phong>>

    @GET("phong/da-o/{maKhu}")
    fun getAllInPhongDaOMaKhu(@Path("maKhu") maKhu: String): Call<List<Phong>>

    @GET("phong/dem/{maKhu}")
    fun demSoPhong(@Path("maKhu") maKhu: String): Call<Int>

    @GET("phong")
    fun getAllPhong(): Call<List<Phong>>
    companion object {
        fun getInstance(): PhongApiService {
            return RetrofitClient.instance.create(PhongApiService::class.java)
        }
    }
}