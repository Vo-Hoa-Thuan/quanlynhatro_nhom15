package ct07n.hcmact.quanlynhatro_nhom15.api

import ct07n.hcmact.quanlynhatro_nhom15.model.KhuTro

import retrofit2.Call
import retrofit2.http.*

interface KhuTroApiService {
    @POST("api/khutro")
    fun insertKhuTro(@Body khuTro: KhuTro): Call<Void>

    @PUT("api/khutro/{id}")
    fun updateKhuTro(@Path("id") id: String, @Body khuTro: KhuTro): Call<Void>

    @DELETE("api/khutro/{id}")
    fun deleteKhuTro(@Path("id") id: String): Call<Void>

    @GET("api/khutro")
    fun getAllKhuTro(): Call<List<KhuTro>>

    @GET("api/khutro/admin/{username}")
    fun getAllInKhuTroByAdmin(@Path("username") username: String): Call<List<KhuTro>>

    @GET("api/khutro/names")
    fun getTenKhuTro(): Call<List<String>>
}