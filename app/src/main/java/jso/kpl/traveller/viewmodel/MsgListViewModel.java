package jso.kpl.traveller.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.Message;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.MsgAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.adapters.MsgListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MsgListViewModel extends ViewModel {

    public List<Message> msgList = new ArrayList<>();
    public MsgListAdapter adapter = new MsgListAdapter();

    public SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    public MutableLiveData<Boolean> isRefreshLD = new MutableLiveData<>();

    public MsgListViewModel() {

        adapter.setHasStableIds(true);
        isRefreshLD.setValue(false);

        msgCall();
    }

    public void msgCall() {
        isRefreshLD.setValue(true);
        WebService.INSTANCE.getClient().create(MsgAPI.class).loadMsgList(App.Companion.getUser().getU_userid())
                .enqueue(new Callback<ResponseResult<List<Message>>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<List<Message>>> call, Response<ResponseResult<List<Message>>> response) {

                        isRefreshLD.setValue(false);

                        if (response.body() != null) {
                            if (response.body().getRes_type() == 1) {
                                Log.d("Trav.msgVm", "onResponse: ");
                                msgList = response.body().getRes_obj();
                                adapter.addItems(msgList);
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseResult<List<Message>>> call, Throwable t) {
                        t.printStackTrace();

                        isRefreshLD.setValue(false);
                    }
                });
    }
}
