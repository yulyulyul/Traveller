package jso.kpl.traveller.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageFlagBinding;
import jso.kpl.traveller.databinding.MyPagePostBinding;
import jso.kpl.traveller.databinding.MyPageProfileBinding;
import jso.kpl.traveller.databinding.MyPageSearchBinding;
import jso.kpl.traveller.databinding.MyPageSubtitleBinding;
import jso.kpl.traveller.model.Country;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.MyPageProfile;
import jso.kpl.traveller.model.MyPageSubtitle;
import jso.kpl.traveller.ui.MyPage;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class MyPageAdapter extends RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder>{

    String TAG = "Trav.MyPageAdapter.";

    // 클릭 리스너 인터페이스--------------------------------------------------------------------------
    OnMyPageClickListener myPageClickListener;

    //My Page의 각 뷰에 대한 클릭 이벤트
    public interface OnMyPageClickListener {

        void onSearchClicked();

        void onPostClicked(ListItem listItem);

        void onMoreClicked(int type);
    }

    public void setMyPageClickListener(OnMyPageClickListener myPageClickListener) {
        this.myPageClickListener = myPageClickListener;
    }

    //---------------------------------------------------------------------------------------------

    //My Page의 갹 뷰타입을 지정한 인덱스
    final int HEAD_PROFILE = 0;
    final int ROUTE_SEARCH = 1;
    final int SUBTITLE_MORE = 2;
    final int USER_POST = 3;
    final int FLAG_ITEM = 4;
    final int ERROR_REFRESH = 5;

    //---------------------------------------------------------------------------------------------

    //My Page의 모든 데이터를 가지고 있는 리스트
    public MutableLiveData<List<MyPageItem>> itemList;

    ViewDataBinding binding;
    MyPageViewModel myPageVm;

    public MyPageAdapter(MutableLiveData<List<MyPageItem>> list, MyPageViewModel myPageVm) {
        this.itemList = list;
        this.myPageVm = myPageVm;
    }

    @NonNull
    @Override
    public MyPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        //각 뷰타입에 따라 데이터 바인딩 - 데이터 바인딩의 방향은 각 xml과 MyPageViewModel이다.
        switch (viewType) {
            case SUBTITLE_MORE:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_subtitle, parent, false);
                return new MyPageViewHolder((MyPageSubtitleBinding) binding);
            case USER_POST:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_post, parent, false);
                return new MyPageViewHolder((MyPagePostBinding) binding);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPageViewHolder holder, int position) {

        int type = itemList.getValue().get(position).getType();

        switch (type) {

            //각 파트의 소제목과 더보기 버튼의 뷰타입
            case SUBTITLE_MORE:

                final MyPageSubtitle subtitle = (MyPageSubtitle) itemList.getValue().get(position).getO();

                holder.subtitleBinding.getSubtitleVM().mpSubtitleLD.setValue(subtitle);


                //각 파트에 해당하는 인덱스를 보낸다.
                holder.subtitleBinding.mpMoreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       // final int type = holder.subtitleBinding.getSubtitleVM().mp_subtitle.getValue().getType();
                      //  myPageClickListener.onMoreClicked(type);
                    }
                });
                break;

            //포스트의 뷰타입
            case USER_POST:

//                //final RePost rePost = (RePost) itemList.getValue().get(position).getO();
//                final ListItem listItem = (ListItem) itemList.getValue().get(position).getO();
//                holder.postBinding.setPostVM(new MyPageViewModel());
//
//                if (listItem != null) {
//                    if (!listItem.getP_expenses().contains("₩")) {
//                        String expenses = listItem.getP_expenses();
//                        listItem.setP_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(expenses)));
//                    }
//
//                    holder.postBinding.getPostVM().mp_post.setValue(listItem);
//
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            myPageClickListener.onPostClicked(listItem);
//                            Log.d(TAG, "adapter - 포스트 클릭: " + listItem.toString());
//                        }
//                    });
//
//
//                }

                break;
            default:
        }
    }

    public void updateItem(int pos, MyPageItem item) {
        itemList.getValue().set(pos, item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.getValue().size();
    }

    @Override
    public int getItemViewType(int position) {

        Log.d(TAG, "넘버: " + position);
        int type = itemList.getValue().get(position).getType();

        switch (type) {
            case HEAD_PROFILE:
                return HEAD_PROFILE;
            case ROUTE_SEARCH:
                return ROUTE_SEARCH;
            case SUBTITLE_MORE:
                return SUBTITLE_MORE;
            case USER_POST:
                return USER_POST;
            case FLAG_ITEM:
                return FLAG_ITEM;
            default:
                return -1;
        }
    }

//    //선호 플래그를 클릭할 시 루트 리스트 화면으로 넘어가는 클릭 이벤트
//    @Override
//    public void onFlagClicked(String country) {
//        myPageClickListener.onFlagClicked(country);
//    }
//
//    //새로운 선호 국가를 추가할 수 있는 화면으로 넘어가는 클릭 이벤트
//    @Override
//    public void onAddFlagClicked() {
//        myPageClickListener.onAddFlagClicked();
//
//    }

    class MyPageViewHolder extends RecyclerView.ViewHolder {

        MyPageProfileBinding profileBinding;
        MyPageSearchBinding searchBinding;
        MyPageSubtitleBinding subtitleBinding;
        MyPageFlagBinding flagBinding;
        MyPagePostBinding postBinding;

        public MyPageViewHolder(@NonNull MyPageProfileBinding binding) {
            super(binding.getRoot());

            this.profileBinding = binding;
            this.profileBinding.setProfileVm(myPageVm);

            binding.setLifecycleOwner(new MyPage());
        }

        public MyPageViewHolder(@NonNull MyPageSearchBinding binding) {
            super(binding.getRoot());

            this.searchBinding = binding;
            this.searchBinding.setSearchVM(myPageVm);

            binding.setLifecycleOwner(new MyPage());
        }

        public MyPageViewHolder(@NonNull MyPageSubtitleBinding binding) {
            super(binding.getRoot());

            this.subtitleBinding = binding;
            this.subtitleBinding.setSubtitleVM(myPageVm);

            binding.setLifecycleOwner(new MyPage());
        }

        public MyPageViewHolder(@NonNull MyPagePostBinding binding) {
            super(binding.getRoot());

            this.postBinding = binding;
      //      this.postBinding.setPostVM(myPageVm);

            binding.setLifecycleOwner(new MyPage());
        }

        public MyPageViewHolder(@NonNull MyPageFlagBinding binding) {
            super(binding.getRoot());

            this.flagBinding = binding;
            this.flagBinding.setFlagVM(myPageVm);

            binding.setLifecycleOwner(new MyPage());
        }
    }

}
