package jso.kpl.traveller.network;

import jso.kpl.traveller.model.LoginUser;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserAPI {

    //로그인
    @POST("/login")
    Call<ResponseResult<User>> goLogin(@Body LoginUser lu_obj);

    //회원가입
    @FormUrlEncoded
    @POST("/check_value")
    Call<ResponseResult<Integer>> authEmail(@Field("u_email") String u_email);

    @Multipart
    @POST("/signup")
    Call<ResponseResult<User>> goSignUp(@Part("User") User user,
                                        @Part MultipartBody.Part imgBody);
}
