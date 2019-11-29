package jso.kpl.traveller.network;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.model.ListItem;
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

    //검색을 통해 가져오는 포스트 리스트
    @FormUrlEncoded
    @POST("/search_by_condition")
    Call<ResponseResult<List<ListItem>>> searchByCondition(@Field("sr_country") String sr_country, @Field("sr_max_cost") int sr_max_cost, @Field("sr_min_cost") int sr_min_cost, @Field("p_id") int p_id, @Field("c_no") int c_no);

    //자신이 작성한 포스트 리스트를 가져온다.
    @FormUrlEncoded
    @POST("/search_by_enroll")
    Call<ResponseResult<List<ListItem>>> searchByEnroll(@Field("u_userid") int u_userid, @Field("p_id") int p_id);
}
