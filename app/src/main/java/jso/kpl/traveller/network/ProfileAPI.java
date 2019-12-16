package jso.kpl.traveller.network;

import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
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
    Call<ResponseResult<Integer>> updatePwd(@Field("u_userid") int u_userid,
                                                   @Field ("current_pwd") String c_pwd,
                                                    @Field ("update_pwd") String u_pwd);
}
