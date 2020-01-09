package jso.kpl.traveller.network;

import jso.kpl.traveller.model.LoginUser;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MsgAPI {

    @FormUrlEncoded
    @POST("/send_msg")
    Call<ResponseResult<Integer>> sendMsg(@Field("m_sender_id") int m_sender_id, @Field("m_sender_nick") String m_sender_nick,
                                          @Field("m_receiver_nick") String m_receiver_nick, @Field("m_msg") String m_msg, @Field("m_card_img") String m_card_img);
}
