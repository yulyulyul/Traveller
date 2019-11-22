package jso.kpl.traveller.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.network.RouteOtherDetailAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.SmallPostDialog;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.ui.adapters.RouteOtherDetailCategoryItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteOtherDetail_VM extends BaseObservable implements Callback {

    String TAG = "Trav.RouteOtherDetailViewModel.";

    private Context context;

    public MutableLiveData<Post> postItem = new MutableLiveData<>();
    public MutableLiveData<SmallPost> smallPostItem = new MutableLiveData<>();
    public MutableLiveData<List<String>> categoryList = new MutableLiveData<>();
    public MutableLiveData<List<String>> sp_categoryList = new MutableLiveData<>();
    public MutableLiveData<List<String>> sp_imgList = new MutableLiveData<>();
    public MutableLiveData<RouteOtherDetailCategoryItemAdapter> adapter = new MutableLiveData<>();
    public MutableLiveData<RouteOtherDetailCategoryItemAdapter> sp_categoryAdapter = new MutableLiveData<>();
    public MutableLiveData<RouteOtherDetailCategoryItemAdapter> sp_imgAdapter = new MutableLiveData<>();
    public RouteNodeAdapter routeNodeAdapter;

    public MutableLiveData<Boolean> like = new MutableLiveData<>();
    public MutableLiveData<String> period = new MutableLiveData<>();
    public MutableLiveData<String> cost = new MutableLiveData<>();

    //레트로핏
    RouteOtherDetailAPI routeOtherDetailAPI = WebService.INSTANCE.getClient().create(RouteOtherDetailAPI.class);

    public RouteOtherDetail_VM(Intent intent, Context context) {
        this.context = context;
        int p_id = intent.getExtras().getInt("p_id");
        Log.d(TAG + "Post", "Post: " + p_id);
        routeOtherDetailAPI.loadPost(p_id).enqueue(this);
    }

    public void init() {
        like.setValue(false);
    }

    public RouteNodeAdapter getRouteNodeAdapter() {
        return routeNodeAdapter;
    }

    public void setRouteNodeAdapter(RouteNodeAdapter routeNodeAdapter) {
        this.routeNodeAdapter = routeNodeAdapter;
    }

    public void my_post_btnClickEvent() {
        Toast.makeText(App.INSTANCE,"나의 포스트로 이동", Toast.LENGTH_SHORT).show();
    }

    public void post_add_btnClickEvent() {
        Toast.makeText(App.INSTANCE,"포스트 추가", Toast.LENGTH_SHORT).show();
    }

    public void like_btnClickEvent() {
        if(like.getValue()) {
            like.setValue(false);
        } else {
            like.setValue(true);
        }
        Toast.makeText(App.INSTANCE,"좋아요 버튼 클릭", Toast.LENGTH_SHORT).show();
    }

    public void resPost(Response response) {
        Post post = ((ResponseResult<List<Post>>) response.body()).getRes_obj().get(0);
        postItem.setValue(post);
        categoryList.setValue(new ArrayList<String>());
        // category list에 표시할 데이터 리스트 생성
        if (post.getP_category().size() != 0) {
            for (int i = 0; i < post.getP_category().size(); i++) {
                categoryList.getValue().add(post.getP_category().get(i));
            }
        }

        adapter.setValue(new RouteOtherDetailCategoryItemAdapter(categoryList, 1));

        // 기간 텍스트 설정
        if (post.getP_start_date() == null && post.getP_end_date() == null) {
            period.setValue("");
        } else if (post.getP_start_date() == null) {
            period.setValue("~ " + post.getP_end_date());
        } else if (post.getP_end_date() == null) {
            period.setValue(post.getP_start_date() + " ~");
        }

        getRouteNodeAdapter().setOnNodeClickListener(new RouteNodeAdapter.OnNodeClickListener() {
            @Override
            public void onNodeClicked(SmallPost sp, int pos) {
                Log.d(TAG, pos + "번 노드 : " + sp.toString());
                callSmallPost(sp);
            }

            @Override
            public void onNodeLongClicked() {
                Log.d(TAG, "onNodeLongClicked: ");
            }
        });

        post.getP_sp_list();
        for (int i = 0; i < post.getP_sp_list().size(); i++) {
            SmallPost smallPost = post.getP_sp_list().get(i);
            getRouteNodeAdapter().putItem(
                    new SmallPost(smallPost.getSp_id(), smallPost.getSp_place(), smallPost.getSp_expenses()));
        }

        Log.d(TAG + "Post 내용 : ", postItem.getValue().toString());
    }

    public void callSmallPost(SmallPost sp) {
        routeOtherDetailAPI.loadSmallPost(sp.getSp_id()).enqueue(this);
    }

    public void resSmallPost(Response response) {
        SmallPost smallPost = ((ResponseResult<List<SmallPost>>) response.body()).getRes_obj().get(0);
        smallPostItem.setValue(smallPost);

        sp_categoryList.setValue(new ArrayList<String>());
        // category list에 표시할 데이터 리스트 생성
        if (smallPost.getSp_category().size() != 0) {
            for (int i = 0; i < smallPost.getSp_category().size(); i++) {
                sp_categoryList.getValue().add(smallPost.getSp_category().get(i));
            }
        }

        sp_categoryAdapter.setValue(new RouteOtherDetailCategoryItemAdapter(sp_categoryList, 1));

        sp_imgList.setValue(new ArrayList<String>());
        // img list에 표시할 데이터 리스트 생성
        if (smallPost.getSp_imgs().size() != 0) {
            String imgPath = App.INSTANCE.getResources().getString(R.string.server_ip_port) + "uploads/";
            for (String img : smallPost.getSp_imgs().get(0).split(", ")) {
                sp_imgList.getValue().add(imgPath + img);
                Log.d(TAG + "이미지인데..", imgPath + img);
            }
        }

        sp_imgAdapter.setValue(new RouteOtherDetailCategoryItemAdapter(sp_imgList, 0));

        SmallPostDialog smallPostDialog = new SmallPostDialog(context);
        smallPostDialog.call(this);
        Log.d(TAG + "SmallPost 내용 : ", smallPostItem.getValue().toString());
    }

    @Override
    public void onResponse(Call call, Response response) {
        Log.d(TAG + "통신 성공","성공적으로 전송");
        boolean type = ((ResponseResult<List<?>>) response.body()).getRes_obj().get(0) instanceof Post;
        if (type) {
            resPost(response);
        } else {
            resSmallPost(response);
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Toast.makeText(App.INSTANCE, "통신 불량" + t.getMessage(), Toast.LENGTH_SHORT).show();
        Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
        t.printStackTrace();
    }
}
