package jso.kpl.traveller.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SignupAPI {

    //1. 데이터 클래스(받는 이름:UserSignUp) + 이미지(받는 이름:us_profile_img)
/*
    @Multipart
    @POST("/upload")
    Call<Void> goSignUp(@Part("UserSignUp") UserSignup userSignup, @Part MultipartBody.Part imgBody);
*/
    //2. 각 변수 + 이미지(받는 이름:us_profile_img)

    @Multipart
    @POST("/signup")
    Call<Void> goSignUp(@Part("us_email") RequestBody us_email,
                        @Part("us_pwd") RequestBody us_pwd,
                        @Part("us_device") RequestBody us_device,
                        @Part("us_nick_name") RequestBody us_nick_name,
                        @Part MultipartBody.Part imgBody);

/*    @Multipart
    @POST("/upload")
    Call<Void> goSignUp(@Part MultipartBody.Part body, @Part("upload") String name);*/
}
