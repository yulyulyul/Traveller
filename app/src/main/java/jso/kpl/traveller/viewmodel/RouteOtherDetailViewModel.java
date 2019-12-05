package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.network.PostAPI;
import jso.kpl.traveller.network.RouteOtherDetailAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.Fragment.ImageSideItem;
import jso.kpl.traveller.ui.RouteOtherDetail;
import jso.kpl.traveller.ui.SmallPostDialog;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.ui.adapters.RouteOtherDetailCategoryItemAdapter;
import jso.kpl.traveller.util.CurrencyChange;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteOtherDetailViewModel extends BaseObservable {

    String TAG = "Trav.RouteOtherDetailViewModel.";

    public MutableLiveData<Post> postItem = new MutableLiveData<>();
    public MutableLiveData<SmallPost> smallPostItem = new MutableLiveData<>();
    public MutableLiveData<List<String>> sp_categoryList = new MutableLiveData<>();
    public MutableLiveData<List<String>> sp_imgList = new MutableLiveData<>();
    public MutableLiveData<RouteOtherDetailCategoryItemAdapter> adapter = new MutableLiveData<>();
    public MutableLiveData<RouteOtherDetailCategoryItemAdapter> sp_categoryAdapter = new MutableLiveData<>();
    public MutableLiveData<RouteOtherDetailCategoryItemAdapter> sp_imgAdapter = new MutableLiveData<>();

    public MutableLiveData<Boolean> isLike = new MutableLiveData<>();
    public MutableLiveData<String> period = new MutableLiveData<>();
    public MutableLiveData<String> cost = new MutableLiveData<>();

    //--------------------
    public ImageSideVpAdapter imageSideVpAdapter;

    public ImageSideVpAdapter getImageSideVpAdapter() {
        return imageSideVpAdapter;
    }

    public void setImageSideVpAdapter(ImageSideVpAdapter imageSideVpAdapter) {
        this.imageSideVpAdapter = imageSideVpAdapter;
    }

    //통신------------------------------------------------------------------------------------------
    RouteOtherDetailAPI routeOtherDetailAPI = WebService.INSTANCE.getClient().create(RouteOtherDetailAPI.class);

    public RouteOtherDetailViewModel() {
        isLike.setValue(false);
    }

    //Route Node Layout
    public RouteNodeAdapter routeNodeAdapter;

    public RouteNodeAdapter getRouteNodeAdapter() {
        return routeNodeAdapter;
    }

    public void setRouteNodeAdapter(RouteNodeAdapter routeNodeAdapter) {
        this.routeNodeAdapter = routeNodeAdapter;
    }
    //----------------------------------------------------------------------------------------------

    public void post_add_btnClickEvent() {
        Toast.makeText(App.INSTANCE,"포스트 추가", Toast.LENGTH_SHORT).show();
    }

    public void onLikeClicked() {

        likeCall();

        Toast.makeText(App.INSTANCE,"좋아요 버튼 클릭", Toast.LENGTH_SHORT).show();

    }

    public void loadingPost(Post post) {

        if(postItem == null)
            postItem = new MutableLiveData<>();

        postItem.setValue(post);

        adapter.setValue(new RouteOtherDetailCategoryItemAdapter(postItem.getValue().getP_category(), 1));

        // 기간 텍스트 설정
        isLike.setValue(postItem.getValue().isP_is_like());

        if(postItem.getValue().getP_start_date() != null)
            period.setValue(postItem.getValue().getP_start_date() + " ~ " + postItem.getValue().getP_end_date());

        String expenses = CurrencyChange.moneyFormatToWon(Long.parseLong(postItem.getValue().getP_expenses()));
        postItem.getValue().setP_expenses(expenses);

        post.getP_sp_list();

        for(int i = 0; i < postItem.getValue().getP_sp_list().size(); i++){

            for(int j = 0; j < postItem.getValue().getP_sp_list().get(i).getSp_imgs().size(); j++){

                ImageSideItem item = new ImageSideItem();

                Bundle bundle = new Bundle();
                bundle.putString("img", postItem.getValue().getP_sp_list().get(i).getSp_imgs().get(j));
                item.setArguments(bundle);

                getImageSideVpAdapter().addItem(item);
            }

        }

        getImageSideVpAdapter().notifyDataSetChanged();

        for (int i = 0; i < post.getP_sp_list().size(); i++) {
            SmallPost smallPost = post.getP_sp_list().get(i);
            getRouteNodeAdapter().putItem(
                    new SmallPost(smallPost.getSp_id(), smallPost.getSp_place(), smallPost.getSp_expenses()));
        }

        Log.d(TAG + "Post 내용 : ", postItem.getValue().toString());
    }

    public Call<ResponseResult<List<Post>>> postCall(int pId){
        return routeOtherDetailAPI.loadPost(pId);
    }

    public Call<ResponseResult<List<SmallPost>>> smallPostCall(SmallPost sp) {
        return routeOtherDetailAPI.loadSmallPost(sp.getSp_id());
    }

    public void loadingSmallPost(SmallPost smallPost) {

        smallPostItem.setValue(smallPost);

        sp_categoryList.setValue(new ArrayList<String>());
        // category list에 표시할 데이터 리스트 생성
        if (smallPost.getSp_category().size() != 0) {
            for (int i = 0; i < smallPost.getSp_category().size(); i++) {
                sp_categoryList.getValue().add(smallPost.getSp_category().get(i));
            }
        }

        sp_categoryAdapter.setValue(new RouteOtherDetailCategoryItemAdapter(smallPost.getSp_category(), 1));

        sp_imgList.setValue(new ArrayList<String>());
        // img list에 표시할 데이터 리스트 생성
        if (smallPost.getSp_imgs().size() != 0) {
            String imgPath = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/";

            for (String img : smallPost.getSp_imgs()) {
                sp_imgList.getValue().add(img);
                Log.d(TAG + "이미지인데..", imgPath + img);
            }
        }

        sp_imgAdapter.setValue(new RouteOtherDetailCategoryItemAdapter(smallPost.getSp_imgs(), 0));

        Log.d(TAG + "SmallPost 내용 : ", smallPostItem.getValue().toString());
    }

    public void likeCall(){

        routeOtherDetailAPI.likePost(App.Companion.getUserid(), postItem.getValue().getP_id(), isLike.getValue()).enqueue(new Callback<ResponseResult<Integer>>() {
            @Override
            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {
                if(response.body() != null){

                    ResponseResult<Integer> result = response.body();

                    if(result.getRes_type() == 1){
                        if(isLike.getValue()){
                            postItem.getValue().setP_is_like(!isLike.getValue());
                            isLike.setValue(!isLike.getValue());
                            App.Companion.sendToast("좋아요를 해제했습니다.");
                        } else{
                            postItem.getValue().setP_is_like(!isLike.getValue());
                            isLike.setValue(!isLike.getValue());
                            App.Companion.sendToast("좋아요를 했습니다.");
                        }
                    } else{
                        App.Companion.sendToast("일시적 에러로 인해 실패했습니다.");
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                App.Companion.sendToast("일시적 에러로 인해 실패했습니다.");
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }


}
