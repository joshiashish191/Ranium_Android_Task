package net.softglobe.raniumandroidtask

import net.softglobe.raniumandroidtask.data.NeoFeed
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/neo/rest/v1/feed")
    suspend fun getUserData(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Response<NeoFeed>
}