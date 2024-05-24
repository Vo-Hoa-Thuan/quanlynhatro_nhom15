package ct07n.hcmact.quanlynhatro_nhom15.api

import ct07n.hcmact.quanlynhatro_nhom15.model.HopDong

import retrofit2.Call
import retrofit2.http.*

interface HopdongApiService {
    @GET("hopDong")
    fun getAllHopDong(): Call<List<HopDong>>

    @POST("hopDong")
    fun insertHopDong(@Body hopDong: HopDong): Call<Void>

    @GET("hopDong/{id}")
    fun getHopDongById(@Path("id") id: String): Call<HopDong>

    @DELETE("hopDong/{id}")
    fun deleteHopDong(@Path("id") id: String): Call<Void>

<<<<<<< HEAD
    @PUT("hopDong/{id}")
    fun updateHopDong(@Path("id") id: HopDong, @Body hopDong: HopDong): Call<Void>
}
=======
    @PUT("api/hopDong/{id}")
    fun updateHopDong(@Path("id") id: String, @Body hopDong: HopDong): Call<Void>
       @GET("api/hopDong/{id}/tenNguoiDung")
    fun getTenNguoiDungByIDHopDong(@Path("id") id: String): Call<String>
}
>>>>>>> 45b52b1561c61809932b76fac5b500e060bc0121
