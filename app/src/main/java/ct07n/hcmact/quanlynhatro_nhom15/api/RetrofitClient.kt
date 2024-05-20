package ct07n.hcmact.quanlynhatro_nhom15.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Trong RetrofitClient.kt
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.120:3000"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
