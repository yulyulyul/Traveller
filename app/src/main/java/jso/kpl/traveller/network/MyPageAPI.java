package jso.kpl.traveller.network;

import java.util.List;

import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.ResponseResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MyPageAPI {

    @FormUrlEncoded
    @POST("/my_page_enroll")
    Call<ResponseResult<List<ListItem>>> myPageEnroll(@Field("u_userid") int u_userid);
}
