package jso.kpl.traveller.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.CartlistItemBinding;
import jso.kpl.traveller.model.Cartlist;

public class CartlistItemAdapter extends RecyclerView.Adapter<CartlistItemAdapter.ViewHolder> {

    private Context context;
    private ViewDataBinding binding;
    private OnItemClickListener listener;

    private MutableLiveData<List<Cartlist>> items;

    public CartlistItemAdapter(MutableLiveData<List<Cartlist>> list) {
        this.items = list;
    }

    public MutableLiveData<List<Cartlist>> getItems() {
        return items;
    }

    public void setItems(MutableLiveData<List<Cartlist>> items) {
        this.items = items;
    }

    // onCreateViewHolder() - 아이템뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.cartlist_item, parent, false);
        ViewHolder VH = new ViewHolder((CartlistItemBinding) binding);

        return VH;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시, 아이템 데이터 세팅하는 부분
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cartlist cartlist = items.getValue().get(position);
        holder.binding.setCartlist(cartlist);
        holder.binding.setImgs(App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + cartlist.getSp_imgs());
    }

    // getItemCount() - 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return items.getValue().size();
    }

    // 아이템뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CartlistItemBinding binding;

        ViewHolder(final CartlistItemBinding binding) {
            super(binding.getRoot()) ;
            this.binding = binding;
            // 클릭 이벤트 리스너
            this.binding.relativeParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if(listener != null) {
                            listener.onItemClick(view, position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
