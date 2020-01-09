package jso.kpl.traveller.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MsgItemBinding;
import jso.kpl.traveller.model.Message;


public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.MsgListViewHolder> {

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



    }


    @Override
    public int getItemCount() {
        return 5;
    }

    class MsgListViewHolder extends RecyclerView.ViewHolder {

        public MsgListViewHolder(@NonNull ViewDataBinding binding) {
            super(binding.getRoot());


        }

        public void onChangeView() {
        }

        public void onBind() {

        }

    }
}
