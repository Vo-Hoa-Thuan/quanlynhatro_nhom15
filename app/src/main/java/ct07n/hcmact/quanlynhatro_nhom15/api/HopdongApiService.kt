package ct07n.hcmact.quanlynhatro_nhom15.api

import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong

import retrofit2.Call
import retrofit2.http.*

interface HopdongApiService {
    @GET("api/hopDong")
    fun getAllHopDong(): Call<List<HopDong>>

    @POST("api/hopDong")
    fun insertHopDong(@Body hopDong: HopDong): Call<Void>

    @GET("api/hopDong/{id}")
    fun getHopDongById(@Path("id") id: String): Call<HopDong>

    @DELETE("api/hopDong/{id}")
    fun deleteHopDong(@Path("id") id: String): Call<Void>

    @PUT("api/hopDong/{id}")
    fun updateHopDong(@Path("id") id: String, @Body hopDong: HopDong): Call<Void>
}