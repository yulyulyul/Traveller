package jso.kpl.traveller.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RodCategoryItemBinding;
import jso.kpl.traveller.databinding.RodImgItemBinding;

public class RouteOtherDetailCategoryItemAdapter extends RecyclerView.Adapter<RouteOtherDetailCategoryItemAdapter.ViewHolder> {

    private Context context;
    private ViewDataBinding binding;
    private int type;

    public MutableLiveData<List<String>> items;

    public RouteOtherDetailCategoryItemAdapter(MutableLiveData<List<String>> list, int type) {
        items = list;
        this.type = type;
    }

    // onCreateViewHolder() - 아이템뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder VH;
        if (type == 1) {
            binding = DataBindingUtil.inflate(inflater, R.layout.rod_category_item, parent, false);
            VH = new ViewHolder((RodCategoryItemBinding) binding);
        } else {
            binding = DataBindingUtil.inflate(inflater, R.layout.rod_img_item, parent, false);
            VH = new ViewHolder((RodImgItemBinding) binding);
        }

        return VH;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (type == 1) {
            String category = items.getValue().get(position);
            ((RodCategoryItemBinding) holder.binding).setCategory(category);
        } else {
            String img = items.getValue().get(position);
            ((RodImgItemBinding) holder.binding).setImgs(img);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return items.getValue().size();
    }

    // 아이템뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        ViewHolder(RodCategoryItemBinding binding) {
            super(binding.getRoot()) ;
            this.binding = binding;
        }

        ViewHolder(RodImgItemBinding binding) {
            super(binding.getRoot()) ;
            this.binding = binding;
        }
    }
}
