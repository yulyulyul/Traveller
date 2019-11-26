package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.network.PostAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.Fragment.GridTypePost;
import jso.kpl.traveller.ui.Fragment.VerticalTypePost;
import jso.kpl.traveller.ui.RouteOtherDetail;
import jso.kpl.traveller.ui.adapters.GridTypePostAdapter;
import jso.kpl.traveller.ui.adapters.VerticalTypePostAdapter;
import jso.kpl.traveller.util.GridSpacingItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteListViewModel extends ViewModel implements GridTypePostAdapter.OnGridItemClickListener, VerticalTypePostAdapter.OnVerticalItemClickListener {

    /*
    Route List - Route Search나 선호 국가를 눌렀을 때 해당 조건에 해당하는 포스트들의 리스트를 뿌려주는 화면
    현재 구성 TabLayout + FrameLayout(Fragment * 2)
     */
    String TAG = "Trav.MainRouteViewModel.";

    String imageUri = "android.resource://jso.kpl.toyroutesearch/drawable/i_blank_person_icon";

    public SearchReq searchReq;

    public SearchReq getSearchReq() {
        return searchReq;
    }

    public void setSearchReq(SearchReq searchReq) {
        this.searchReq = searchReq;
    }

    public FragmentManager fm;

    TabLayout.OnTabSelectedListener listener;

    public GridTypePost gt_post;
    public VerticalTypePost vt_post;

    //검색 결과 리스트
    public MutableLiveData<RePost> rePost = new MutableLiveData<>();

    //각 adapter - 해당 뷰페이저의 Adapter, 각 뷰페이저의 프래그먼트들의 Adapter
    public GridTypePostAdapter gridAdapter;
    public VerticalTypePostAdapter verticalAdapter;

    //StaggeredGridLayout의 한줄 당 아이템 갯수와 간격을 설정하는 객체
    public GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, 10, true);

    public MutableLiveData<List<RePost>> rePostList = new MutableLiveData<>();

    //통신 관련 -------------------------------------------------------------------------------------
    PostAPI postAPI = WebService.INSTANCE.getClient().create(PostAPI.class);;

    //----------------------------------------------------------------------------------------------
    public RouteListViewModel() {

        gridAdapter = new GridTypePostAdapter(getPostList());
        verticalAdapter = new VerticalTypePostAdapter(getPostList());

        gridAdapter.setOnGridItemClickListener(this);
        verticalAdapter.setOnVerticalItemClickListener(this);
        getPostList();
    }


    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    //탭레이아웃의 탭 추가
    public List<TabLayout.Tab> getTabs(TabLayout tabLayout) {

        List<TabLayout.Tab> tabs = new ArrayList<>();

        tabs.add(tabLayout.newTab().setIcon(R.drawable.i_grid_icon));

        tabs.add(tabLayout.newTab().setIcon(R.drawable.i_vertical_icon));

        tabs.get(0).getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.clicked));
        tabs.get(1).getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.non_clicked));
        return tabs;
    }

    //탭레이아웃의 콜백 리스너
    public TabLayout.OnTabSelectedListener onTestClicked(final FrameLayout container) {

        /*첫 번째 탭의 [gt_post:fragment]의 생성자
            + [gt_post:fragment]를 frameLayout에 올린다.
         */
        gt_post = new GridTypePost();
        fm.beginTransaction().add(container.getId(), gt_post).commit();

        searchByCondition();
        Log.d(TAG, "onTabSelected: 1 첫번쨰 로딩");

        listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //클릭하는 탭의 번호
                final int CLICK_NO = tab.getPosition();

                switch (CLICK_NO) {
                    case 0:

                        tab.getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.clicked));
                        /*
                        클릭한 탭에 해당하는 프래그먼트가 null이라면 객체화 후, [container:FrameLayout]에 올린다.
                        만약 null이 아니라면 보여지는 프래그먼트는 hide 처리를 하고 해당 탭의 프래그먼트를 show 처리를 해준다.
                         */
                        if (gt_post == null) {
                            gt_post = new GridTypePost();
                            fm.beginTransaction().add(container.getId(), gt_post).commit();
                            searchByCondition();
                            Log.d(TAG, "onTabSelected: 1 첫번쨰 로딩");
                        } else {
                            fm.beginTransaction().show(gt_post).commit();
                            Log.d(TAG, "onTabSelected: 1 이건 아닌데..");
                        }

                        if (vt_post != null) fm.beginTransaction().hide(vt_post).commit();

                        break;
                    case 1:

                        tab.getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.clicked));

                        if (vt_post == null) {
                            vt_post = new VerticalTypePost();
                            fm.beginTransaction().add(container.getId(), vt_post).commit();
                            Log.d(TAG, "onTabSelected: 2 첫번쨰 로딩");
                            searchByCondition();
                        } else {
                            fm.beginTransaction().show(vt_post).commit();
                            Log.d(TAG, "onTabSelected: 2 이건 아닌데..");
                        }

                        if (gt_post != null) fm.beginTransaction().hide(gt_post).commit();

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                final int NONE_CLICK_NO = tab.getPosition();
                tab.getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.non_clicked));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                /*
                현재 보여지는 프래그먼트에 해당하는 탭을 다시 누를 경우,
                프래그먼트를 삭제하고 다시 [container:FrameLayout]에 붙인다.
                 */


                final int RESELECT_NO = tab.getPosition();

                switch (RESELECT_NO) {
                    case 0:
                        fm.beginTransaction().remove(gt_post).commit();

                        gt_post = new GridTypePost();

                        fm.beginTransaction().add(container.getId(), gt_post).commit();
                        break;
                    case 1:
                        fm.beginTransaction().remove(vt_post).commit();

                        vt_post = new VerticalTypePost();

                        fm.beginTransaction().add(container.getId(), vt_post).commit();
                        break;
                }

            }
        };

        return listener;
    }

    public void searchByCondition(){
        if(getSearchReq() != null){
            Call<List<ResponseResult<Post>>> call = postAPI.searchByCondition(getSearchReq());

            call.enqueue(new Callback<List<ResponseResult<Post>>>() {
                @Override
                public void onResponse(Call<List<ResponseResult<Post>>> call, Response<List<ResponseResult<Post>>> response) {

                    List<Post> postList = new ArrayList<>();

                    if(response.body() != null){
                        for(ResponseResult<Post> pr_post : response.body()){
                            if(pr_post.getRes_type() == 1){
                                postList.add((Post)pr_post.getRes_obj());
                            }else{
                                Log.d(TAG, "onResponse: " + pr_post.getRes_msg());
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<List<ResponseResult<Post>>> call, Throwable t) {
                    sendToast(App.INSTANCE, "에러로 인해 포스트를 불러오는데 실패했습니다.");

                    Log.e(TAG, "Post init load onFailure: ", t);
                }
            });
        }

    }

    //초기 값 - 더미
    public List<RePost> getPostList() {
        //통신 처리

        rePostList.setValue(new ArrayList<RePost>());

        Post post = new Post("테스트01", "asle1000", true);
        post.setP_expenses(1000000 + "");

        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));
        rePostList.getValue().add(new RePost(imageUri, post));

        return rePostList.getValue();
    }

    //토스트 메시지 메서드화
    private void sendToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /*
    각 프래그먼트의 리사이클러 뷰 아이템을 클릭하는 이벤트
     */
    @Override
    public void GridItemClicked(RePost rePost) {
        Log.d(TAG + "GridClicked", "그리드 클릭 : " + rePost.getPost().toString());

        if(App.INSTANCE != null){
            Toast.makeText(App.INSTANCE, "나라: " + rePost.getPost().getP_place(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(App.INSTANCE, RouteOtherDetail.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.INSTANCE.startActivity(intent);
        }
    }

    @Override
    public void VerticalItemClicked(RePost rePost) {
        Log.d(TAG + "VertiClicked", "버티컬: : " +rePost.getPost().toString());

        if(App.INSTANCE != null){
            Toast.makeText(App.INSTANCE, "나라: " + rePost.getPost().getP_place(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(App.INSTANCE, RouteOtherDetail.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.INSTANCE.startActivity(intent);
        }
    }
}
