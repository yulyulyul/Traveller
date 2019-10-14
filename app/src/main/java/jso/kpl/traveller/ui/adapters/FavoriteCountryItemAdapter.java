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

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.FavoriteCountryItemBinding;
import jso.kpl.traveller.model.FavoriteCountryVO;
import jso.kpl.traveller.util.JavaUtil;

public class FavoriteCountryItemAdapter extends RecyclerView.Adapter<FavoriteCountryItemAdapter.ViewHolder> {

    private Context context;
    private ViewDataBinding binding;
    private OnItemClickListener listener;

    private MutableLiveData<List<FavoriteCountryVO>> items;

    public FavoriteCountryItemAdapter(MutableLiveData<List<FavoriteCountryVO>> list) {
        this.items = list;
    }

    // onCreateViewHolder() - 아이템뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.favorite_country_item, parent, false);
        ViewHolder VH = new ViewHolder((FavoriteCountryItemBinding) binding);

        return VH;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시, 아이템 데이터 세팅하는 부분
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FavoriteCountryVO favoriteCountryVO = items.getValue().get(position);
        holder.binding.setFC(favoriteCountryVO);
        int drawableResourceId = JavaUtil.getImage(holder.itemView.getContext(), favoriteCountryVO.getFlag());
        holder.binding.setImageUrl(drawableResourceId);
    }

    // getItemCount() - 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {

        return items.getValue().size();
    }

    // 아이템뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {

        private FavoriteCountryItemBinding binding;

        ViewHolder(FavoriteCountryItemBinding binding) {
            super(binding.getRoot()) ;
            this.binding = binding;
            // 클릭 이벤트 리스너
            this.binding.relativeParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if(listener != null) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            this.binding.countryInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onBtnClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onBtnClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
