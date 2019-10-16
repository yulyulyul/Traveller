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
import jso.kpl.traveller.databinding.FavoriteCountryInfoItemBinding;
import jso.kpl.traveller.model.FavoriteCountryInfoVO;
import jso.kpl.traveller.util.JavaUtil;

public class FavoriteCountryInfoItemAdapter extends RecyclerView.Adapter<FavoriteCountryInfoItemAdapter.ViewHolder> {

    private Context context;
    private ViewDataBinding binding;
    private OnItemClickListener listener;

    public MutableLiveData<List<FavoriteCountryInfoVO>> items;

    public FavoriteCountryInfoItemAdapter(MutableLiveData<List<FavoriteCountryInfoVO>> list) {
        items = list;
    }

    // onCreateViewHolder() - 아이템뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.favorite_country_info_item, parent, false);
        ViewHolder VH = new ViewHolder((FavoriteCountryInfoItemBinding) binding);

        return VH;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FavoriteCountryInfoVO favoriteCountryInfo_VO = items.getValue().get(position);
        int drawableResourceId = JavaUtil.getImage(holder.itemView.getContext(), favoriteCountryInfo_VO.getImg());
        holder.binding.setImageUrl(drawableResourceId);
    }

    // getItemCount() - 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return items.getValue().size();
    }

    // 아이템뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {

        private FavoriteCountryInfoItemBinding binding;

        ViewHolder(FavoriteCountryInfoItemBinding binding) {
            super(binding.getRoot()) ;
            this.binding = binding;
            // 클릭 이벤트 리스너
            this.binding.recentVisitor.setOnClickListener(new View.OnClickListener() {
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
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
