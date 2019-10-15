package jso.kpl.traveller.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.ImageRouteItemBinding;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.MainRouteList;
import jso.kpl.traveller.viewmodel.MainRouteListViewModel;

public class ImageRouteListAdapter extends RecyclerView.Adapter<ImageRouteListAdapter.ImageRouteViewHolder> {

    /*
        Route List - StaggeredGridLayout 형태의 포스트 리스트의 Adapter
        사진의 크기를 제각각 설정하여 보이게 함
     */
    String TAG = "Trav.ImageRouteListAdapter.";

    //해당 아이템의 클릭 리스너 - 이미지 클릭 시 해당 포스트의 상세 보기 화면이 뜬다. - Route Other Detail.class
    //(예정) 포스트 데이터를 파라미터로 할 것
    OnImagePostClickListener onImagePostClickListener;

    public interface OnImagePostClickListener {
        void imagePostClicked(RePost rePost);
    }

    public void setOnImagePostClickListener(OnImagePostClickListener onImagePostClickListener) {
        this.onImagePostClickListener = onImagePostClickListener;
    }

    //리사이클러 뷰의 아이템 리스트 - (포스트 데이터 + 이미지)
    List<RePost> rePostList = new ArrayList<>();

    //뷰페이저가 존재하는 뷰모델 - 여기서 값을 처리한다.
    MainRouteListViewModel rlVm;

    //생성자
    public ImageRouteListAdapter(MainRouteListViewModel rlVm) {
        this.rlVm = rlVm;
    }

    @NonNull
    @Override
    public ImageRouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG + "onCreateViewHolder", "Start");

        LayoutInflater lif = LayoutInflater.from(parent.getContext());

        ImageRouteItemBinding binding = DataBindingUtil.inflate(lif, R.layout.image_route_item, parent, false);

        return new ImageRouteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRouteViewHolder holder, final int position) {

        Log.d(TAG + "onBindViewHolder", "Start");

        //RePost(Post + imgStr)의 객체
        final RePost rePost = rePostList.get(position);

        holder.binding.setImgVm(rlVm);
        holder.binding.getImgVm().rePost.setValue(new RePost(rePost.getRp_profile(), null));

        //아이템을 클릭 할 때 RePost를 넘긴다.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImagePostClickListener.imagePostClicked(rePost);
            }
        });

    }

    public void addItems(List<RePost> list) {

        Log.d(TAG, "addItems 최초 한번 또는 새로고침할 떄 불리는 함수");

        rePostList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rePostList.size();
    }

    class ImageRouteViewHolder extends RecyclerView.ViewHolder {

        ImageRouteItemBinding binding;

        public ImageRouteViewHolder(@NonNull ImageRouteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.setLifecycleOwner(new MainRouteList());
            binding.executePendingBindings();
        }
    }
}
