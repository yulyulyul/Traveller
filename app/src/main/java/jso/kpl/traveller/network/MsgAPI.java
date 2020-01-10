package jso.kpl.traveller.network;

import java.util.List;

import jso.kpl.traveller.model.Message;
import jso.kpl.traveller.model.ResponseResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MsgAPI {

    @FormUrlEncoded
    @POST("/send_msg")
    Call<ResponseResult<Integer>> sendMsg(@Field("m_sender_id") int m_sender_id, @Field("m_sender_nick") String m_sender_nick, @Field("m_receiver_nick") String m_receiver_nick,
                                          @Field("m_msg") String m_msg, @Field("m_card_img") String m_card_img);

    @FormUrlEncoded
    @POST("/load_msg_list")
    Call<ResponseResult<List<Message>>> loadMsgList(@Field("u_userid") int u_userid);

    @FormUrlEncoded
    @POST("/update_receive")
    Call<ResponseResult<Integer>> updateReply(@Field("m_no") int m_no);

    @FormUrlEncoded
    @POST("/delete_msg")
    Call<ResponseResult<Integer>> deleteMsg(@Field("m_no") int m_no);

    @FormUrlEncoded
    @POST("/send_back_push")
    Call<ResponseResult<Integer>> sendBackPush(@Field("u_userid") int u_userid);
}
