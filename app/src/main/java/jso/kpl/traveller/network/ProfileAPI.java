package jso.kpl.traveller.network;

import java.util.List;

import jso.kpl.traveller.model.ResponseResult;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ProfileAPI {

    @Multipart
    @POST("/update_profileImg")
    Call<ResponseResult<String>> updateProfileImg(@Part("u_userid") int u_userid,
                                                  @Part MultipartBody.Part imgBody);

    @FormUrlEncoded
    @POST("/update_pwd")
    Call<ResponseResult<Integer>> updateUserPwd(@Field("u_userid") int u_userid,
                                            @Field("update_pwd") String u_pwd);

    @FormUrlEncoded
    @POST("/update_info")
    Call<ResponseResult<Integer>> updateUserInfo(@Field("u_userid") int u_userid,
                                             @Field("update_nick_name") String u_nick);

    @FormUrlEncoded
    @POST("/user_info_count")
    Call<ResponseResult<List<Integer>>> loadUserInfo(@Field("u_userid") int u_userid);


}
