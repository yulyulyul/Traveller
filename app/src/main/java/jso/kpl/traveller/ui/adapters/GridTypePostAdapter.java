package jso.kpl.traveller.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.GridPostItemBinding;
import jso.kpl.traveller.model.ListItem;
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

    public interface OnGridItemClickListener {
        void GridItemClicked(int p_id);
    }

    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
        this.onGridItemClickListener = onGridItemClickListener;
    }

    //리사이클러 뷰의 아이템 리스트
    List<ListItem> postList = new ArrayList<>();

    //생성자
    public GridTypePostAdapter() {

    }

    @NonNull
    @Override
    public GridTypePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        GridPostItemBinding binding = DataBindingUtil.inflate(lif, R.layout.grid_post_item, parent, false);

        return new GridTypePostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GridTypePostViewHolder holder, final int position) {

        //RePost(Post + imgStr)의 객체
        final ListItem item = postList.get(position);

        holder.onBind(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGridItemClickListener.GridItemClicked(item.getP_id());
            }
        });

    }

    public void addItem(ListItem item) {

        if(postList == null)
            postList = new ArrayList<>();

        postList.add(item);

        notifyItemInserted(postList.size() - 1);
    }

    public void addItems(List<ListItem> items) {

        if(postList == null)
            postList = new ArrayList<>();

        postList.addAll(items);

        notifyDataSetChanged();
    }

    public void removeItems() {

        postList = new ArrayList<>();

        notifyDataSetChanged();
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
        return postList.size();
    }

    class GridTypePostViewHolder extends RecyclerView.ViewHolder {

        GridPostItemBinding binding;

        public GridTypePostViewHolder(@NonNull GridPostItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            binding.setGridItemVm(new RouteListViewModel());
            binding.setLifecycleOwner(new RouteList());
        }

        public void onBind(ListItem item) {

            String path = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + item.getSp_imgs();
            item.setSp_imgs(item.getSp_imgs());

            binding.getGridItemVm().postLD.setValue(item);
        }
    }
}
