package jso.kpl.traveller.ui.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MsgItemBinding;
import jso.kpl.traveller.model.Message;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.MsgAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.EditingMsg;
import jso.kpl.traveller.ui.MsgList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.MsgListViewHolder> {

    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClicked(int pos, int no);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    List<Message> msgList = new ArrayList<>();

    @NonNull
    @Override
    public MsgListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        MsgItemBinding binding = DataBindingUtil.inflate(lif, R.layout.msg_item, parent, false);

        return new MsgListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MsgListViewHolder holder, final int position) {

        final Message msg = msgList.get(position);

        holder.onBind(msg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.onChangeView(position);
            }
        });

        holder.msgItemBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClicked(position, msg.getM_no());
            }
        });


        holder.msgItemBinding.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Trav.adapter", "답장입니다.");

                Intent intent = new Intent(App.INSTANCE, EditingMsg.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("p_author", msg.getM_sender());
                App.INSTANCE.startActivity(intent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void addItem(Message message) {

        if (msgList == null)
            msgList = new ArrayList<>();

        msgList.add(message);

        notifyDataSetChanged();
    }

    public void removeItem(int pos) {

        if (msgList != null)
            msgList.remove(pos);

        notifyItemRemoved(pos);
    }

    public void removeItems() {

        if (msgList != null)
            msgList.clear();
        else
            msgList = new ArrayList<>();

        notifyDataSetChanged();
    }

    class MsgListViewHolder extends RecyclerView.ViewHolder {

        public MutableLiveData<Boolean> isCheck = new MutableLiveData<>();
        public MsgItemBinding msgItemBinding;

        public MsgListViewHolder(@NonNull MsgItemBinding binding) {
            super(binding.getRoot());

            this.msgItemBinding = binding;
            this.msgItemBinding.setLifecycleOwner(new MsgList());

            isCheck.setValue(true);
        }

        public void onChangeView(final int pos) {
            isCheck.setValue(!isCheck.getValue());

            msgItemBinding.cardFrontView.setVisibility(isCheck.getValue() ? View.VISIBLE : View.GONE);
            msgItemBinding.cardBackView.setVisibility(isCheck.getValue() ? View.GONE : View.VISIBLE);

            if (!msgItemBinding.getItem().isM_is_receive()) {

                WebService.INSTANCE.getClient().create(MsgAPI.class)
                        .updateReply(msgItemBinding.getItem().getM_no())
                        .enqueue(new Callback<ResponseResult<Integer>>() {
                            @Override
                            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                                if (response.body() != null) {

                                    if (response.body().getRes_type() == 1) {
                                        msgList.get(pos).setM_is_receive(true);
                                        msgItemBinding.isReceive.setBackgroundResource(R.drawable.open_mail);
                                    } else
                                        msgList.get(pos).setM_is_receive(false);
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                                msgList.get(pos).setM_is_receive(false);
                            }
                        });

            }
        }

        public void onBind(Message message) {
            msgItemBinding.setItem(message);
        }

    }
}
