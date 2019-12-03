package jso.kpl.traveller.network;

import java.util.List;

import jso.kpl.traveller.model.Cartlist;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.Timeline;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CartlistAPI {

    @FormUrlEncoded
    @POST("/cartlist")
    Call<ResponseResult<List<Cartlist>>> cartlist(@Field("u_userid") int u_userid);

    @FormUrlEncoded
    @POST("/cartlist_timeline")
    Call<ResponseResult<List<Timeline>>> cartlist_timeline(@Field("p_id") int p_id);
}
