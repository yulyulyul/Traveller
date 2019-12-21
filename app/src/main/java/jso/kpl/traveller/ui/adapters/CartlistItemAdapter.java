package jso.kpl.traveller.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.CartlistItemBinding;
import jso.kpl.traveller.model.CartListItem;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.util.JavaUtil;

public class CartlistItemAdapter extends RecyclerView.Adapter<CartlistItemAdapter.ViewHolder> {

    private Context context;
    private ViewDataBinding binding;
    private OnItemClickListener listener;

    private MutableLiveData<List<CartListItem>> items;

    public CartlistItemAdapter(MutableLiveData<List<CartListItem>> list) {
        this.items = list;
    }

    public MutableLiveData<List<CartListItem>> getItems() {
        return items;
    }

    public void setItems(MutableLiveData<List<CartListItem>> items) {
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
        CartListItem cartListItem = items.getValue().get(position);

        holder.onBind(cartListItem);
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
            super(binding.getRoot());
            this.binding = binding;
            // 클릭 이벤트 리스너

        }

        public void onBind(final CartListItem item){

            String tmp = item.p_period.replace(" ", "");
            String[] tmps = tmp.split("~");

            item.setP_period(JavaUtil.travelPeriod(tmps[0], tmps[1]));

            if (!item.getP_expenses().contains("₩")) {
                String currency = CurrencyChange.moneyFormatToWon(Long.parseLong(item.getP_expenses()));
                item.setP_expenses(currency);
            }

            this.binding.cartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(view, position);

                            binding.largeImg.setBackground(null);
                            binding.largeImgLayout.setVisibility(View.GONE);
                        }
                    }
                }
            });

            this.binding.setOnImgClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.presentImg_1:
                            showLargeImg(binding, item.getSp_imgs());
                            break;
                        case R.id.presentImg_2:
                            showLargeImg(binding, item.getSp_imgs());
                            break;
                        case R.id.presentImg_3:
                            showLargeImg(binding, item.getSp_imgs());
                            break;
                    }
                    Log.d("Trav.cartAdapter", "onClick: ");
                }
            });

            binding.setItem(item);
        }

        public void showLargeImg(final CartlistItemBinding binding, String str) {
            binding.largeImgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.largeImgLayout.setVisibility(View.GONE);
                    binding.largeImg.setBackground(null);
                }
            });

            binding.largeImgLayout.setVisibility(View.VISIBLE);

            RequestOptions options
                    = RequestOptions.bitmapTransform(new FitCenter()).error(R.drawable.i_empty_image_icon);

            String path =  App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/" + str;

            Glide.with(binding.largeImg.getContext())
                    .load(path)
                    .centerCrop()
                    .apply(options)
                    .into(binding.largeImg);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
