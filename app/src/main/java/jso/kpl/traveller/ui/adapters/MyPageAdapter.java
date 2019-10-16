package jso.kpl.traveller.ui.adapters;

import android.content.Intent;
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
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.MyPageProfile;
import jso.kpl.traveller.model.MyPageSubtitle;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.FavoriteCountryInfo;
import jso.kpl.traveller.ui.MyPage;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class MyPageAdapter extends RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder> implements FlagRvAdapter.OnFlagClickListener {

    String TAG = "Trav.MyPageAdapter.";

    OnMyPageClickListener myPageClickListener;

    //My Page의 각 뷰에 대한 클릭 이벤트
    public interface OnMyPageClickListener {
        void onProfileClicked(String email);

        void onSearchClicked();

        void onPostClicked(RePost rePost);

        void onMoreClicked(int type);
    }

    //선호 플래그를 클릭할 시 루트 리스트 화면으로 넘어가는 클릭 이벤트
    @Override
    public void onFlagClicked() {
        Log.d(TAG, "onFlagClicked");
    }

    //새로운 선호 국가를 추가할 수 있는 화면으로 넘어가는 클릭 이벤트
    @Override
    public void onAddFlagClicked() {

        Log.d(TAG, "onAddFlagClicked: ");
        //Intent intent = new Intent(context, RouteOtherDetail.class);
    }

    public void setMyPageClickListener(OnMyPageClickListener myPageClickListener) {
        this.myPageClickListener = myPageClickListener;
    }

    //My Page의 갹 뷰타입을 지정한 인덱스
    final int HEAD_PROFILE = 0;
    final int ROUTE_SEARCH = 1;
    final int SUBTITLE_MORE = 2;
    final int USER_POST = 3;
    final int FLAG_ITEM = 4;
    final int ERROR_REFRESH = 5;

    //My Page의 모든 데이터를 가지고 있는 리스트
    MutableLiveData<List<MyPageItem>> itemList;

    ViewDataBinding binding;

    public MyPageAdapter(MutableLiveData<List<MyPageItem>> list) {
        this.itemList = list;
    }

    @NonNull
    @Override
    public MyPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        //각 뷰타입에 따라 데이터 바인딩 - 데이터 바인딩의 방향은 각 xml과 MyPageViewModel이다.
        switch (viewType) {
            case HEAD_PROFILE:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_profile, parent, false);
                return new MyPageViewHolder((MyPageProfileBinding) binding);
            case ROUTE_SEARCH:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_search, parent, false);
                return new MyPageViewHolder((MyPageSearchBinding) binding);
            case SUBTITLE_MORE:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_subtitle, parent, false);
                return new MyPageViewHolder((MyPageSubtitleBinding) binding);
            case FLAG_ITEM:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_flag, parent, false);
                return new MyPageViewHolder((MyPageFlagBinding) binding);
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
            //MyPage의 첫번째 뷰타입으로 유저의 이미지와 이메일을 보여준다.
            case HEAD_PROFILE:

                final MyPageProfile myPageProfile = (MyPageProfile) itemList.getValue().get(position).getO();

                holder.profileBinding.setProfileVm(new MyPageViewModel());
                holder.profileBinding.getProfileVm().mp_profile.setValue(myPageProfile);

                holder.profileBinding.setEmail(holder.profileBinding.getProfileVm().mp_profile.getValue().getEmail());
//                프로필 이미지 클릭 시 이메일 전송
                holder.profileBinding.mpProfileIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myPageClickListener.onProfileClicked(holder.profileBinding.getProfileVm().mp_profile.getValue().getEmail());
                    }
                });

                break;

            //MyPage의 두번째 뷰타입으로 누르면 Route Search로 넘어간다.
            case ROUTE_SEARCH:

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myPageClickListener.onSearchClicked();
                    }
                });

                break;

            //각 파트의 소제목과 더보기 버튼의 뷰타입
            case SUBTITLE_MORE:

                final MyPageSubtitle subtitle = (MyPageSubtitle) itemList.getValue().get(position).getO();

                holder.subtitleBinding.setSubtitleVM(new MyPageViewModel());
                holder.subtitleBinding.getSubtitleVM().mp_subtitle.setValue(subtitle);

                //각 파트에 해당하는 인덱스를 보낸다.
                holder.subtitleBinding.mpMoreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final int type = holder.subtitleBinding.getSubtitleVM().mp_subtitle.getValue().getType();
                        myPageClickListener.onMoreClicked(type);
                    }
                });
                break;

             //선호 국가 국기 0~4개와 선호 국가를 추가할 수 있는 버튼 1개로 구성된 뷰타입
            case FLAG_ITEM:

                List<String> flagList = (List<String>) itemList.getValue().get(position).getO();

                holder.flagBinding.setFlagVM(new MyPageViewModel());
                holder.flagBinding.getFlagVM().mp_flag.setValue(flagList);

                holder.flagBinding.getFlagVM().flagRvAdapter = new FlagRvAdapter(holder.flagBinding.getFlagVM().mp_flag.getValue());

                holder.flagBinding.getFlagVM().flagRvAdapter.setOnFlagClickListener(this);

                break;

            //포스트의 뷰타입
            case USER_POST:

                final RePost rePost = (RePost) itemList.getValue().get(position).getO();
                holder.postBinding.setPostVM(new MyPageViewModel());
                holder.postBinding.getPostVM().mp_post.setValue(rePost);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myPageClickListener.onPostClicked(rePost);
                        Log.d(TAG, "adapter - 포스트 클릭: " + rePost.toString());
                    }
                });
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return itemList.getValue().size();
    }

    @Override
    public int getItemViewType(int position) {

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

    class MyPageViewHolder extends RecyclerView.ViewHolder {

        MyPageProfileBinding profileBinding;
        MyPageSearchBinding searchBinding;
        MyPageSubtitleBinding subtitleBinding;
        MyPageFlagBinding flagBinding;
        MyPagePostBinding postBinding;

        public MyPageViewHolder(@NonNull MyPageProfileBinding binding) {
            super(binding.getRoot());

            this.profileBinding = binding;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();

        }

        public MyPageViewHolder(@NonNull MyPageSearchBinding binding) {
            super(binding.getRoot());

            this.searchBinding = binding;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }

        public MyPageViewHolder(@NonNull MyPageSubtitleBinding binding) {
            super(binding.getRoot());

            this.subtitleBinding = binding;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }

        public MyPageViewHolder(@NonNull MyPagePostBinding binding) {
            super(binding.getRoot());

            this.postBinding = binding;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }

        public MyPageViewHolder(@NonNull MyPageFlagBinding binding) {
            super(binding.getRoot());

            this.flagBinding = binding;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }
    }

}
