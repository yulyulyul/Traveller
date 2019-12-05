package jso.kpl.traveller.ui.adapters;

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
import jso.kpl.traveller.databinding.VerticalPostItemBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class VerticalTypePostAdapter extends RecyclerView.Adapter<VerticalTypePostAdapter.VerticalTypePostViewHolder> {

    /*
        Route List - LinearLayout-Vertical 형태의 포스트 리스트의 Adapter
        사진의 크기를 제각각 설정하여 보이게 함
     */
    String TAG = "Trav.VerticalTypePostAdapter.";

    OnVerticalItemClickListener onVerticalItemClickListener;

    public interface OnVerticalItemClickListener{
        void VerticalItemClicked(int p_id);
    }

    public void setOnVerticalItemClickListener(OnVerticalItemClickListener onVerticalItemClickListener) {
        this.onVerticalItemClickListener = onVerticalItemClickListener;
    }

    //리사이클러 뷰의 아이템 리스트
    List<ListItem> postList = new ArrayList<>();

    public VerticalTypePostAdapter() {

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
        final ListItem item = postList.get(position);

        holder.onBind(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVerticalItemClickListener.VerticalItemClicked(item.getP_id());
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

    class VerticalTypePostViewHolder extends RecyclerView.ViewHolder {

        VerticalPostItemBinding binding;

        public VerticalTypePostViewHolder(@NonNull VerticalPostItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            binding.setVerticalItemVm(new RouteListViewModel());
            binding.setLifecycleOwner(new RouteList());
        }

        public void onBind(ListItem item){

            if(!item.getU_profile_img().contains("http://")){
                String path = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + item.getU_profile_img();
                item.setU_profile_img(item.getU_profile_img());
            }

            if(!item.getP_expenses().contains("₩")){
                Long expensesLng = Long.parseLong(item.getP_expenses());
                item.setP_expenses(CurrencyChange.moneyFormatToWon(expensesLng));
            }

            binding.getVerticalItemVm().postLD.setValue(item);

        }

    }
}
