package jso.kpl.traveller.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.VerticalPostItemBinding;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class VerticalTypePostAdapter extends RecyclerView.Adapter<VerticalTypePostAdapter.VerticalTypePostViewHolder> {

    /*
        Route List - LinearLayout-Vertical 형태의 포스트 리스트의 Adapter
        사진의 크기를 제각각 설정하여 보이게 함
     */
    String TAG = "Trav.VerticalTypePostAdapter.";

    OnVerticalItemClickListener onVerticalItemClickListener;

    public interface OnVerticalItemClickListener{
        void VerticalItemClicked(RePost rePost);
    }

    public void setOnVerticalItemClickListener(OnVerticalItemClickListener onVerticalItemClickListener) {
        this.onVerticalItemClickListener = onVerticalItemClickListener;
    }

    //리사이클러 뷰의 아이템 리스트 - (포스트 데이터 + 이미지)
    MutableLiveData<List<RePost>> rePostList = new MutableLiveData<>();

    public VerticalTypePostAdapter(List<RePost> rePostList) {
        this.rePostList.setValue(rePostList);
    }

    @NonNull
    @Override
    public VerticalTypePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        VerticalPostItemBinding binding = DataBindingUtil.inflate(lif, R.layout.vertical_post_item, parent, false);

        return new VerticalTypePostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalTypePostViewHolder holder, int position) {

        //RePost(Post + imgStr)의 객체
        final RePost rePost = rePostList.getValue().get(position);

        Log.d(TAG, "ver item: " + rePost.getPost().getP_place());
        holder.onBind(rePost);
        holder.binding.executePendingBindings();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVerticalItemClickListener.VerticalItemClicked(rePost);
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
        Log.d(TAG, "Vertical - getItemCount: " + rePostList.getValue().size());
        return rePostList.getValue().size();
    }

    class VerticalTypePostViewHolder extends RecyclerView.ViewHolder {

        VerticalPostItemBinding binding;

        public VerticalTypePostViewHolder(@NonNull VerticalPostItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            binding.setVerticalItemVm(new RouteListViewModel());
            binding.setLifecycleOwner(new RouteList());
        }

        public void onBind(RePost rePost){
            binding.getVerticalItemVm().rePost.setValue(rePost);

        }

    }
}
