package jso.kpl.traveller.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.CountryItemBinding;
import jso.kpl.traveller.model.Country;

public class CountryItemAdapter extends RecyclerView.Adapter<CountryItemAdapter.ItemViewHolder> {

    String TAG = "Trav.FcAdapter.";

    private ViewDataBinding binding;

    //인터페이스 - 클릭 리스너
    private OnCountryClickListener onCountryClickListener;

    public interface OnCountryClickListener {
        void onBtnClicked(int position, int type);

        void onDetailClicked(int ct_no);
    }

    public void setOnItemClickListener(OnCountryClickListener onCountryClickListener) {
        this.onCountryClickListener = onCountryClickListener;
    }

    //----------------------------------------------------------------------------------------------
    private MutableLiveData<List<Country>> items;

    public CountryItemAdapter() {

        items = new MutableLiveData<>();
        items.setValue(new ArrayList<Country>());

    }

    // onCreateViewHolder() - 아이템뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        binding = DataBindingUtil.inflate(inflater, R.layout.country_item, parent, false);

        return new ItemViewHolder((CountryItemBinding) binding);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시, 아이템 데이터 세팅하는 부분
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        final Country country = items.getValue().get(position);
        country.setCt_flag();

        holder.binding.setCountryItem(country);

        holder.addRemoveCountry(position);

    }

    // getItemCount() - 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return items.getValue().size();
    }

    public void updateItem(Country country){
        items.getValue().add(country);
        notifyItemInserted(items.getValue().size() - 1);
    }

    public void removeItem() {
        items.getValue().clear();
        notifyDataSetChanged();
    }

    // 아이템뷰를 저장하는 뷰홀더 클래스
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private CountryItemBinding binding;

        public ItemViewHolder(final CountryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void addRemoveCountry(final int position){

            // 국가 디테일 보기
            binding.relativeParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (position != RecyclerView.NO_POSITION) {
                        if (onCountryClickListener != null) {
                            onCountryClickListener.onDetailClicked(binding.getCountryItem().getCt_no());
                        }
                    }

                }
            });

            //국가 추가 또는 제거
            binding.removeCountryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (position != RecyclerView.NO_POSITION) {
                        if (onCountryClickListener != null) {

                            Log.d(TAG, "추가 삭제: " + binding.getCountryItem().is_favorite_ld.getValue());
                            //삭제
                            onCountryClickListener.onBtnClicked(position, 0);
                            Log.d(TAG, "국가 삭제");

                            notifyItemChanged(position);
                        }
                    }


                }
            });

            //국가 추가 또는 제거
            binding.addCountryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (position != RecyclerView.NO_POSITION) {
                        if (onCountryClickListener != null) {

                            Log.d(TAG, "추가 삭제: " + binding.getCountryItem().is_favorite_ld.getValue());
                            //추가
                            onCountryClickListener.onBtnClicked(position, 1);
                            Log.d(TAG, "국가 추가");

                            notifyItemChanged(position);
                        }
                    }
                }
            });

        }
    }


}
