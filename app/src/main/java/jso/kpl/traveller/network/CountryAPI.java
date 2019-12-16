package jso.kpl.traveller.network;

import java.util.List;

import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.ResponseResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CountryAPI {

    @FormUrlEncoded
    @POST("/load_country_list")
    Call<ResponseResult<List<Country>>> loadCountryList(@Field("u_userid") int u_userid, @Field("ct_no") int ct_no);

    @FormUrlEncoded
    @POST("/load_favorite_country_list")
    Call<ResponseResult<List<Country>>> loadFavoriteCountry(@Field("u_userid") int u_userid, @Field("is_mypage") int is_mypage);

    @FormUrlEncoded
    @POST("/add_favorite_country")
    Call<ResponseResult<Integer>> addFlag(@Field("u_userid") int u_userid, @Field("ct_no") int ct_no);

    @FormUrlEncoded
    @POST("/delete_favorite_country")
    Call<ResponseResult<Integer>> deleteFavoriteCountry(@Field("u_userid") int u_userid, @Field("ct_no") int ct_no);

    @FormUrlEncoded
    @POST("load_country_info")
    Call<ResponseResult<Country>> loadCountryInfo(@Field("u_userid") int u_userid, @Field("ct_no") int ct_no);
}
