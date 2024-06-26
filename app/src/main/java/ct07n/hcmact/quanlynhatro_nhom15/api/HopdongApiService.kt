package ct07n.hcmact.quanlynhatro_nhom15.api

import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong

import retrofit2.Call
import retrofit2.http.*

interface HopdongApiService {
    @GET("hopDong/{maKhu}")
    fun getAllHopDongByMaKhu(@Path("maKhu") maKhu: String): Call<List<HopDong>>

    @POST("hopDong")
    fun insertHopDong(@Body hopDong: HopDong): Call<Void>

    @GET("hopDong/{id}")
    fun getHopDongById(@Path("id") id: String): Call<HopDong>

    @DELETE("hopDong/{id}")
    fun deleteHopDong(@Path("id") id: String): Call<Void>


    @PUT("hopDong/{id}")
    fun updateHopDong(@Path("id") id: String, @Body hopDong: HopDong): Call<Void>


    @GET("hopDong/{id}/tenNguoiDung")
    fun getTenNguoiDungByIDHopDong(@Path("id") id: String): Call<TenNguoiResponse>

    data class TenNguoiResponse(
        val ho_ten_nguoi_dung: String
    )

    @GET("hopDong/hanCon/{maKhu}")
    fun getHopDongConHanByMaKhu(@Path("maKhu") maKhu: String): Call<List<HopDong>>;

    @GET("hopDong/hanHet/{maKhu}")
    fun getHopDongHetHanByMaKhu(@Path("maKhu") maKhu: String): Call<List<HopDong>>;
}
