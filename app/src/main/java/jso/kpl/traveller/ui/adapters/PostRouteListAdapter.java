package jso.kpl.traveller.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.VerticalPostBinding;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.MainRouteList;
import jso.kpl.traveller.viewmodel.MainRouteListViewModel;

public class PostRouteListAdapter extends RecyclerView.Adapter<PostRouteListAdapter.PostListViewHolder> {

    String TAG = "Trav.PostRouteListAdapter.";

    List<RePost> rePostList = new ArrayList<>();
    MainRouteListViewModel rlVm;

    public PostRouteListAdapter(MainRouteListViewModel rlVm) {

        Log.d(TAG + "Constructor", "생성자");

        this.rlVm = rlVm;
    }

    @NonNull
    @Override
    public PostListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG + "onCreateViewHolder", "onCreateViewHolder: Start");

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        VerticalPostBinding binding = DataBindingUtil.inflate(lif, R.layout.vertical_post, parent, false);

        Log.d(TAG + "onCreateViewHolder", "onCreateViewHolder: End");

        return new PostListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListViewHolder holder, int position) {

        Log.d(TAG + "onBindViewHolder", "Start");

        RePost rePost = rePostList.get(position);

        holder.binding.setVpVm(rlVm);

        holder.binding.getVpVm().rePost.setValue(rePost);
    }

    public void addItems(List<RePost> list) {

        Log.d(TAG, "addItems 최초 한번 또는 새로고침할 떄 불리는 함수");

        rePostList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rePostList.size();
    }

    class PostListViewHolder extends RecyclerView.ViewHolder {

        VerticalPostBinding binding;

        public PostListViewHolder(@NonNull VerticalPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.setLifecycleOwner(new MainRouteList());
            binding.executePendingBindings();
        }
    }
}
