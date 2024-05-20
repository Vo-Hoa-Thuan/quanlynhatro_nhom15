package ct07n.hcmact.quanlynhatro_nhom15.api

import ct07n.hcmact.quanlynhatro_nhom15.model.Admin
import ct07n.hcmact.quanlynhatro_nhom15.model.LoginResponse

import retrofit2.Call
import retrofit2.http.*

interface AdminApiService {
    @POST("admin")
    fun insertAdmin(@Body admin: Admin): Call<Void>

    @PUT("admin")
    fun updateAdmin(@Body admin: Admin): Call<Void>

    @GET("admins")
    fun getAllAdmins(): Call<List<Admin>>


    @POST("admin/login")
    fun checkLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>


    @GET("admin/{username}")
    fun getAdmin(@Path("username") username: String): Call<Admin>

    data class LoginRequest(val username: String, val password: String)
}
