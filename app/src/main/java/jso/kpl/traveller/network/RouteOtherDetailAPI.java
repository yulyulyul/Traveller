package jso.kpl.traveller.network;

import java.util.List;

import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SmallPost;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface RouteOtherDetailAPI {

    @FormUrlEncoded
    @POST("/load_post")
    Call<ResponseResult<List<Post>>> loadPost(@Field("u_userid") int u_userid,@Field("p_id") int p_id);

    @FormUrlEncoded
    @POST("/load_small_post")
    Call<ResponseResult<List<SmallPost>>> loadSmallPost(@Field("sp_id") int sp_id);

    @FormUrlEncoded
    @POST("/update_recent_post")
    Call<ResponseResult<Integer>> updateRecentPost(@Field("u_userid") int u_userid, @Field("p_id") int p_id);

    @FormUrlEncoded
    @POST("/like_post")
    Call<ResponseResult<Integer>> likePost(@Field("u_userid") int u_userid, @Field("p_id") int p_id, @Field("p_is_like") boolean p_is_like);
}
