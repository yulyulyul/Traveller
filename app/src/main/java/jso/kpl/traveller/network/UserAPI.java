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

    //로그아웃
    @FormUrlEncoded
    @POST("/logout")
    Call<ResponseResult<Integer>> goLogout(@Field("u_userid") int u_userid);

    @FormUrlEncoded
    @POST("/check_value")
    Call<ResponseResult<Integer>> authEmail(@Field("u_email") String u_email);

    //회원가입
    @Multipart
    @POST("/signup")
    Call<ResponseResult<User>> goSignUp(@Part("User") User user,
                                        @Part MultipartBody.Part imgBody);

    //mypage 카트리스트 뷰 추가 및 삭제
    @FormUrlEncoded
    @POST("/update_user_cart_list")
    Call<ResponseResult<Integer>> updateCartListView(@Field("u_userid") int u_userid, @Field("u_is_cart") int u_is_cart);

    //회원 탈퇴
    @FormUrlEncoded
    @POST("/delete_user")
    Call<ResponseResult<Integer>> deleteUser(@Field("u_userid") int u_userid);

    //임시 비밀번호 수정
    @FormUrlEncoded
    @POST("/update_temp_pwd")
    Call<ResponseResult<Integer>> updateUserTempPwd(@Field("u_email") String u_email,
                                                    @Field("update_pwd") String u_pwd);

    @FormUrlEncoded
    @POST("/upload_token")
    Call<ResponseResult<Integer>> uploadToken(@Field("u_userid") int u_userid, @Field("u_token") String u_token);
}
