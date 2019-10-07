package jso.kpl.traveller.network

import jso.kpl.traveller.model.LoginUser
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 로그인이 할때.
 */
interface LoginAPI
{
    @POST("login")
    fun LOGIN(@Body lu_obj : LoginUser): Call<LoginUser>
}