package jso.kpl.traveller.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MsgListBinding;
import jso.kpl.traveller.interfaces.DialogYNInterface;
import jso.kpl.traveller.model.Message;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.MsgAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.adapters.MsgListAdapter;
import jso.kpl.traveller.viewmodel.MsgListViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MsgList extends AppCompatActivity implements MsgListAdapter.OnDeleteClickListener {

    String TAG = "Trav.MsgList";

    MsgListBinding binding;
    MsgListViewModel msgListVm;

    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        msgListVm = new MsgListViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.msg_list);
        binding.setMsgListVm(msgListVm);
        binding.setLifecycleOwner(this);

        binding.rv.setItemAnimator(null);
        binding.topActionBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.getMsgListVm().onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.getMsgListVm().adapter.removeItems();
                binding.getMsgListVm().msgCall();
            }
        };

        binding.getMsgListVm().adapter.setOnDeleteClickListener(this);

        if (broadcastReceiver != null) return;

        final IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.limky.broadcastreceiver.gogo");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                Message m = (Message) intent.getSerializableExtra("msg");
                binding.getMsgListVm().adapter.addItem(m);
                binding.getMsgListVm().adapter.notifyItemInserted(0);

                binding.rv.smoothScrollToPosition(0);
            }
        };

        registerReceiver(receiver, new IntentFilter("com.example.limky.broadcastreceiver.gogo"));
    }

    @Override
    public void onDeleteClicked(final int pos, final int no) {
        Log.d(TAG, "onDeleteClicked: " + pos + ", " + no);

        Bundle bundle = new Bundle();
        bundle.putString("title", "해당 유저와의 대화를 전부 삭제하시겠습니까?");

        final CustomDialog customDialog = new CustomDialog();

        customDialog.setArguments(bundle);

        customDialog.show(((AppCompatActivity) this).getSupportFragmentManager(), "Dialog_TAG");

        customDialog.setDialogYNInterface(new DialogYNInterface() {
            @Override
            public void positiveBtn() {

                WebService.INSTANCE.getClient().create(MsgAPI.class).deleteMsg(no)
                        .enqueue(new Callback<ResponseResult<Integer>>() {
                            @Override
                            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                                if (response.body() != null) {

                                    if (response.body().getRes_type() == 1) {
                                        msgListVm.msgList.remove(pos);
                                        msgListVm.adapter.removeItem(pos);
                                        msgListVm.adapter.notifyItemRemoved(pos);
                                        customDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                                Log.d(TAG, t.getMessage());
                            }
                        });
            }

            @Override
            public void negativeBtn() {
                customDialog.dismiss();
            }
        });

    }
}
