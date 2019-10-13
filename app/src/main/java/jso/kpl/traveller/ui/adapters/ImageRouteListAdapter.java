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
import jso.kpl.traveller.databinding.GridImageBinding;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.MainRouteList;
import jso.kpl.traveller.viewmodel.MainRouteListViewModel;

public class ImageRouteListAdapter extends RecyclerView.Adapter<ImageRouteListAdapter.ImageListViewHolder> {

    String TAG = "Trav.ImageRouteListAdapter.";

    List<RePost> rePostList = new ArrayList<>();
    MainRouteListViewModel rlVm;

    public ImageRouteListAdapter(MainRouteListViewModel rlVm) {

        Log.d(TAG + "Constructor", "생성자");

        this.rlVm = rlVm;
    }

    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG+ "onCreateViewHolder", "onCreateViewHolder: Start");

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        GridImageBinding binding = DataBindingUtil.inflate(lif, R.layout.grid_image, parent, false);

        Log.d(TAG+ "onCreateViewHolder", "onCreateViewHolder: End");

        return new ImageListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListViewHolder holder, int position) {

        Log.d(TAG + "onBindViewHolder", "Start");

            String imgStr = rePostList.get(position).getRp_profile();

        Log.d(TAG+"onBindViewHolder", position+"번째 이미지 주소: " + imgStr);

            holder.binding.setImgVm(rlVm);
            holder.binding.getImgVm().rePost.setValue(new RePost(imgStr, null));
    }

    public void addItems(List<RePost> list){

        Log.d(TAG, "addItems 최초 한번 또는 새로고침할 떄 불리는 함수");

        rePostList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rePostList.size();
    }

    class ImageListViewHolder extends RecyclerView.ViewHolder{

        GridImageBinding binding;

        public ImageListViewHolder(@NonNull GridImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.setLifecycleOwner(new MainRouteList());
            binding.executePendingBindings();
        }
    }
}
