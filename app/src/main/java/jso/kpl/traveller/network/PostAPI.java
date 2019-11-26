package jso.kpl.traveller.network;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.model.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostAPI {

    @Multipart
    @POST("/editing_post")
    Call<ResponseResult<Integer>> editingPost(@Part("Post") Post post, @Part ArrayList<MultipartBody.Part> imgs);

    @POST("/search_by_condition")
    Call<List<ResponseResult<Post>>> searchByCondition(@Body SearchReq searchReq);
}
