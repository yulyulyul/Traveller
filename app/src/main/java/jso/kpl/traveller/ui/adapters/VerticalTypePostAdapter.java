package jso.kpl.traveller.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.SimplePostBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.util.CurrencyChange;

public class VerticalTypePostAdapter extends RecyclerView.Adapter<VerticalTypePostAdapter.VerticalTypePostViewHolder> {

    /*
        Route List - LinearLayout-Vertical 형태의 포스트 리스트의 Adapter
        사진의 크기를 제각각 설정하여 보이게 함
     */
    String TAG = "Trav.VerticalTypePostAdapter.";

    SimplePostBinding binding;

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

       binding = DataBindingUtil.inflate(lif, R.layout.simple_post, parent, false);

        return new VerticalTypePostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalTypePostViewHolder holder, int position) {

        //RePost(Post + imgStr)의 객체
        final ListItem item = postList.get(position);

        holder.onBind(item);

        holder.binding.getItem().onPostClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVerticalItemClickListener.VerticalItemClicked(item.getP_id());
            }
        };


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

        SimplePostBinding binding;

        public VerticalTypePostViewHolder(@NonNull SimplePostBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

        public void onBind(ListItem item){

            if(!item.getP_expenses().contains("₩"))
                item.setP_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(item.getP_expenses())));

            binding.setItem(item);
        }

    }
}
