package jso.kpl.traveller.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.FlagImageBinding;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.ui.MyPage;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class FlagRvAdapter extends RecyclerView.Adapter<FlagRvAdapter.FlagRvViewHolder> {

    //Flag 뷰의 클릭 리스너, 국기를 클릭 할 때와 국기 추가를 클릭할 때
    public OnFlagClickListener onFlagClickListener;

    public interface OnFlagClickListener {
        void onFlagClicked();

        void onAddFlagClicked();
    }

    public void setOnFlagClickListener(OnFlagClickListener onFlagClickListener) {
        this.onFlagClickListener = onFlagClickListener;
    }

    String TAG = "Trav.FlagRvAdapter.";

    FlagImageBinding binding;

    //선호 플래그 4개 + 국기 추가 1개
    List<Country> flagList;

    public FlagRvAdapter(List<Country> flagList) {
        this.flagList = flagList;
    }

    @NonNull
    @Override
    public FlagRvAdapter.FlagRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        binding = DataBindingUtil.inflate(lif, R.layout.flag_image, parent, false);

        return new FlagRvAdapter.FlagRvViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FlagRvAdapter.FlagRvViewHolder holder, final int position) {

        Country country = flagList.get(position);

        holder.onBind(country.getCt_flag());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //플래그 4개는 선호 국가 클릭 리스너, 마지막 플러스 이미지는 선호 국가 추가 클릭 리스너
                //플래그 마지막 더하기
                if (position == flagList.size() - 1) {
                    onFlagClickListener.onAddFlagClicked();
                } else {
                    //플래그 클릭
                    onFlagClickListener.onFlagClicked();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //해당 리사이클러 뷰의 최대 갯수를 5개로 제한
        return flagList.size();
    }

    class FlagRvViewHolder extends RecyclerView.ViewHolder {

        FlagImageBinding binding;

        public FlagRvViewHolder(@NonNull FlagImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.setFlagRvVM(new MyPageViewModel());
            binding.setLifecycleOwner(new MyPage());
        }

        public void onBind(String img){
            binding.getFlagRvVM().mpFlagItem.setValue(img);
        }
    }
}
