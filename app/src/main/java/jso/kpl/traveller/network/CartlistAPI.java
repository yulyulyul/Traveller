package jso.kpl.traveller.network;

import java.util.List;

import jso.kpl.traveller.model.CartListItem;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.Timeline;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CartlistAPI {

    @FormUrlEncoded
    @POST("/add_cart_list")
    Call<ResponseResult<Integer>> addCartList(@Field("u_userid") int u_userid, @Field("p_id") int p_id);

    @FormUrlEncoded
    @POST("/delete_cart_list")
    Call<ResponseResult<Integer>> removeCartList(@Field("u_userid") int u_userid, @Field("p_id") int p_id);

    @FormUrlEncoded
    @POST("/load_cartlist")
    Call<ResponseResult<List<CartListItem>>> cartlist(@Field("u_userid") int u_userid);

    @FormUrlEncoded
    @POST("/load_cartlist_timeline")
    Call<ResponseResult<List<Timeline>>> cartlist_timeline(@Field("p_id") int p_id);
}
