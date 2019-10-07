package jso.kpl.traveller.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageHorizonBinding;
import jso.kpl.traveller.databinding.MyPagePostBinding;
import jso.kpl.traveller.databinding.MyPageProfileBinding;
import jso.kpl.traveller.databinding.MyPageSearchBinding;
import jso.kpl.traveller.databinding.MyPageSubtitleBinding;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.MyPageProfile;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.MyPage;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class MyPageAdapter extends RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder> {

    interface OnMyPageClickListener{
        void onProfileClicked();
        void onSearchClicked();
        void onMoreClicked();
        void onPoseClicked();
        void onHorizonClicked();
    }

    private OnMyPageClickListener onMyPageClickListener;

    String TAG = "TAG.MyPageAdapter.";

    final int HEAD_PROFILE = 0;
    final int ROUTE_SEARCH = 1;
    final int SUBTITLE_MORE = 2;
    final int USER_POST = 3;
    final int HORIZON_ITEM = 4;
    final int ERROR_REFRESH = 5;


    MutableLiveData<List<MyPageItem>> itemList;
    MyPageViewModel myPageVM;
    ViewDataBinding binding;

    public MyPageAdapter(MyPageViewModel myPageVM, MutableLiveData<List<MyPageItem>> list) {

        Log.d(TAG + "init", "Init");

        this.myPageVM = myPageVM;
        this.itemList = list;
    }

    @NonNull
    @Override
    public MyPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG + "onCreateVH", "onCreateViewHolder-Start");

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HEAD_PROFILE:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_profile, parent, false);
                return new MyPageViewHolder((MyPageProfileBinding) binding, myPageVM);
            case ROUTE_SEARCH:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_search, parent, false);
                return new MyPageViewHolder((MyPageSearchBinding) binding, myPageVM);
            case SUBTITLE_MORE:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_subtitle, parent, false);
                return new MyPageViewHolder((MyPageSubtitleBinding) binding, myPageVM);
            case HORIZON_ITEM:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_horizon, parent, false);
                return new MyPageViewHolder((MyPageHorizonBinding) binding, myPageVM);
            case USER_POST:
                binding = DataBindingUtil.inflate(lif, R.layout.my_page_post, parent, false);
                return new MyPageViewHolder((MyPagePostBinding) binding, myPageVM);
            default:
                return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MyPageViewHolder holder, int position) {

        int type = itemList.getValue().get(position).getType();

        Log.d(TAG + "VH", "순서: " + position + ", 타입: " + type);

        onMyPageClickListener = (OnMyPageClickListener) this.onMyPageClickListener;

        switch (type) {
            case HEAD_PROFILE:
                Log.d(TAG + "VH", "Head_PROFILE");
                MyPageProfile myPageProfile = (MyPageProfile) itemList.getValue().get(position).getO();
                holder.profileBinding.setProfileVm(myPageVM);
                holder.profileBinding.getProfileVm().mp_profileLD.setValue(myPageProfile);
                break;
            case ROUTE_SEARCH:
                Log.d(TAG + "VH", "ROUTE_SEARCH");
                break;
            case SUBTITLE_MORE:
                Log.d(TAG + "VH", "SUBTITLE");
                String subtitle = itemList.getValue().get(position).getO().toString();

                holder.subtitleBinding.setSubtitleVM(myPageVM);
                holder.subtitleBinding.getSubtitleVM().subtitleLD.setValue(subtitle);
                break;
            case HORIZON_ITEM:
                Log.d(TAG + "VH", "HORIZON_ITEM");

                List<String> horizonList = (List<String>) itemList.getValue().get(position).getO();

                holder.horizonBinding.setHorizonVM(myPageVM);
                holder.horizonBinding.getHorizonVM().horizonLD.setValue(horizonList);

                break;
            case USER_POST:
                Log.d(TAG + "VH", "USER_POST");
                RePost rePost = (RePost) itemList.getValue().get(position).getO();
                holder.postBinding.setPostVM(myPageVM);
                holder.postBinding.getPostVM().postLD.setValue(rePost);
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
            case HORIZON_ITEM:
                return HORIZON_ITEM;
            default:
                return -1;
        }
    }


    class MyPageViewHolder extends RecyclerView.ViewHolder {

        MyPageProfileBinding profileBinding;
        MyPageSearchBinding searchBinding;
        MyPageSubtitleBinding subtitleBinding;
        MyPageHorizonBinding horizonBinding;
        MyPagePostBinding postBinding;

        MyPageViewModel vm;

        public MyPageViewHolder(@NonNull MyPageProfileBinding binding, MyPageViewModel vm) {
            super(binding.getRoot());

            this.profileBinding = binding;
            this.vm = vm;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }

        public MyPageViewHolder(@NonNull MyPageSearchBinding binding, MyPageViewModel vm) {
            super(binding.getRoot());

            this.searchBinding = binding;
            this.vm = vm;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }

        public MyPageViewHolder(@NonNull MyPageSubtitleBinding binding, MyPageViewModel vm) {
            super(binding.getRoot());

            this.subtitleBinding = binding;
            this.vm = vm;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }

        public MyPageViewHolder(@NonNull MyPagePostBinding binding, MyPageViewModel vm) {
            super(binding.getRoot());

            this.postBinding = binding;
            this.vm = vm;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }

        public MyPageViewHolder(@NonNull MyPageHorizonBinding binding, MyPageViewModel vm) {
            super(binding.getRoot());

            this.horizonBinding = binding;
            this.vm = vm;

            binding.setLifecycleOwner(new MyPage());
            binding.executePendingBindings();
        }
    }

}
