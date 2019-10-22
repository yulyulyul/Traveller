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
import jso.kpl.traveller.databinding.GridPostItemBinding;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class GridTypePostAdapter extends RecyclerView.Adapter<GridTypePostAdapter.GridTypePostViewHolder> {

    /*
        Route List - StaggeredGridLayout 형태의 포스트 리스트의 Adapter
        사진의 크기를 제각각 설정하여 보이게 함
     */
    String TAG = "Trav.GridTypePostAdapter.";

    /*
    [RouteList - Grid Type]의 아이템 클릭 리스너
     */
    OnGridItemClickListener onGridItemClickListener;

    public interface OnGridItemClickListener{
        void GridItemClicked(RePost rePost);
    }

    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
        this.onGridItemClickListener = onGridItemClickListener;
    }

    //리사이클러 뷰의 아이템 리스트 - (포스트 데이터 + 이미지)
    MutableLiveData<List<RePost>> rePostList = new MutableLiveData<>();

    //생성자
    public GridTypePostAdapter(List<RePost> rePostList) {
        this.rePostList.setValue(rePostList);
    }

    @NonNull
    @Override
    public GridTypePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG + "onCreateViewHolder", "Start");

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        GridPostItemBinding binding = DataBindingUtil.inflate(lif, R.layout.grid_post_item, parent, false);

        return new GridTypePostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GridTypePostViewHolder holder, final int position) {

        Log.d(TAG + "onBindViewHolder", "Start");

        //RePost(Post + imgStr)의 객체
        final RePost rePost = rePostList.getValue().get(position);

        holder.onBind(rePost);
        Log.d(TAG, "Grid Item: " + rePost.getPost().getP_country());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGridItemClickListener.GridItemClicked(rePost);
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
        Log.d(TAG, "Grid - getItemCount: ");
        return rePostList.getValue().size();
    }

    class GridTypePostViewHolder extends RecyclerView.ViewHolder {

        GridPostItemBinding binding;

        public GridTypePostViewHolder(@NonNull GridPostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setGridItemVm(new RouteListViewModel());
            binding.setLifecycleOwner(new RouteList());
        }

        public void onBind(RePost rePost){

            binding.getGridItemVm().rePost.setValue(rePost);
            binding.executePendingBindings();
        }
    }
}
