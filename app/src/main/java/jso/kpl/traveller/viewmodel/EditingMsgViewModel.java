package jso.kpl.traveller.viewmodel;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.MsgAPI;
import jso.kpl.traveller.network.MsgWebService;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditingMsgViewModel extends ViewModel {

    public MutableLiveData<String> receiverLD = new MutableLiveData<>();
    public MutableLiveData<String> contentLD = new MutableLiveData<>();
    public MutableLiveData<String> limitCharLD = new MutableLiveData<>();

    public MutableLiveData<Boolean> isSendLD = new MutableLiveData<>();

    public MutableLiveData<Boolean> isClickLD = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSuccessLD = new MutableLiveData<>();

    public String cardImg = "dummy_travel_img1";

    // 포스트 하위 모든 이미지들을 보여주는 객체
    public ImageSideVpAdapter msgBgAdapter;

    public ImageSideVpAdapter getMsgBgAdapter() {
        return msgBgAdapter;
    }

    public void setMsgBgAdapter(ImageSideVpAdapter msgBgAdapter) {
        this.msgBgAdapter = msgBgAdapter;
    }

    public EditingMsgViewModel() {
        isSuccessLD.setValue(true);
        isClickLD.setValue(true);
        isSendLD.setValue(false);
        limitCharLD.setValue("0/300");
    }

    public View.OnClickListener onSendClickListener;
//    public void onSendClicked() {
//        MsgWebService.INSTANCE.getClient().create(MsgAPI.class)
//                .sendMsg(App.Companion.getUser().getU_userid(), App.Companion.getUser().getU_nick_name(), receiverLD.getValue(), contentLD.getValue(), cardImg)
//                .enqueue(new Callback<ResponseResult<Integer>>() {
//                    @Override
//                    public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {
//                        if (response.body() != null) {
//
//                            Log.d("Trav", "성공 완료");
//                            isSuccessLD.setValue(true);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
//                        isSuccessLD.setValue(false);
//                    }
//                });
//    }
}
