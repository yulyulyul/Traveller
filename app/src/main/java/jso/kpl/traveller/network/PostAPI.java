package jso.kpl.traveller.network;

import java.util.ArrayList;

import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostAPI {

    @Multipart
    @POST("/editing_post")
    Call<ResponseResult<Integer>> editingPost(@Part("Post") Post post, @Part ArrayList<MultipartBody.Part> imgs);

//    @POST("/editing_post")
//    Call<ResponseResult<Integer>> editingPost(@Body Post post);

}
