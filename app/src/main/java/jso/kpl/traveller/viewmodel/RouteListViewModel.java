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
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.Fragment.GridTypePost;
import jso.kpl.traveller.ui.Fragment.VerticalTypePost;
import jso.kpl.traveller.ui.RouteOtherDetail;
import jso.kpl.traveller.ui.adapters.GridTypePostAdapter;
import jso.kpl.traveller.ui.adapters.VerticalTypePostAdapter;
import jso.kpl.traveller.util.GridSpacingItemDecoration;

public class RouteListViewModel extends ViewModel implements GridTypePostAdapter.OnGridItemClickListener, VerticalTypePostAdapter.OnVerticalItemClickListener {

    /*
    Route List - Route Search나 선호 국가를 눌렀을 때 해당 조건에 해당하는 포스트들의 리스트를 뿌려주는 화면
    현재 구성 TabLayout + FrameLayout(Fragment * 2)
     */
    String TAG = "Trav.MainRouteViewModel.";
    String imageUri = "android.resource://jso.kpl.toyroutesearch/drawable/i_blank_person_icon";

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

        tabs.add(tabLayout.newTab().setText("1st"));
        tabs.add(tabLayout.newTab().setText("2nd"));

        return tabs;
    }

    //탭레이아웃의 콜백 리스너
    public TabLayout.OnTabSelectedListener onTestClicked(final FrameLayout container) {

        /*첫 번째 탭의 [gt_post:fragment]의 생성자
            + [gt_post:fragment]를 frameLayout에 올린다.
         */
        gt_post = new GridTypePost();
        fm.beginTransaction().add(container.getId(), gt_post).commit();

        listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //클릭하는 탭의 번호
                final int CLICK_NO = tab.getPosition();

                switch (CLICK_NO) {
                    case 0:

                        /*
                        클릭한 탭에 해당하는 프래그먼트가 null이라면 객체화 후, [container:FrameLayout]에 올린다.
                        만약 null이 아니라면 보여지는 프래그먼트는 hide 처리를 하고 해당 탭의 프래그먼트를 show 처리를 해준다.
                         */
                        if (gt_post == null) {
                            gt_post = new GridTypePost();
                            fm.beginTransaction().add(container.getId(), gt_post).commit();
                        } else {
                            fm.beginTransaction().show(gt_post).commit();
                        }

                        if (vt_post != null) fm.beginTransaction().hide(vt_post).commit();

                        break;
                    case 1:
                        if (vt_post == null) {
                            vt_post = new VerticalTypePost();
                            fm.beginTransaction().add(container.getId(), vt_post).commit();
                        } else {
                            fm.beginTransaction().show(vt_post).commit();
                        }

                        if (gt_post != null) fm.beginTransaction().hide(gt_post).commit();

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                final int NONE_CLICK_NO = tab.getPosition();

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

    //초기 값 - 더미
    public List<RePost> getPostList() {
        //통신 처리

        rePostList.setValue(new ArrayList<RePost>());

        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트01", "asle1000", "10000", "테스트01")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트02", "asle1000", "10000", "테스트02")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트03", "asle1000", "10000", "테스트03")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트04", "asle1000", "10000", "테스트04")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트05", "asle1000", "10000", "테스트05")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트06", "asle1000", "10000", "테스트06")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트07", "asle1000", "10000", "테스트07")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트08", "asle1000", "10000", "테스트08")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트09", "asle1000", "10000", "테스트09")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트10", "asle1000", "10000", "테스트10")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트11", "asle1000", "10000", "테스트11")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트12", "asle1000", "10000", "테스트12")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트13", "asle1000", "10000", "테스트13")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트14", "asle1000", "10000", "테스트14")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트15", "asle1000", "10000", "테스트15")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트16", "asle1000", "10000", "테스트16")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트17", "asle1000", "10000", "테스트17")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트18", "asle1000", "10000", "테스트18")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트19", "asle1000", "10000", "테스트19")));
        rePostList.getValue().add(new RePost(imageUri, new Post(0, "테스트20", "asle1000", "10000", "테스트20")));

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
            Toast.makeText(App.INSTANCE, "나라: " + rePost.getPost().getP_country(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(App.INSTANCE, RouteOtherDetail.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.INSTANCE.startActivity(intent);
        }
    }

    @Override
    public void VerticalItemClicked(RePost rePost) {
        Log.d(TAG + "VertiClicked", "버티컬: : " +rePost.getPost().toString());

        if(App.INSTANCE != null){
            Toast.makeText(App.INSTANCE, "나라: " + rePost.getPost().getP_country(), Toast.LENGTH_SHORT).show();
            App.INSTANCE.startActivity(new Intent(App.INSTANCE, RouteOtherDetail.class));
        }
    }
}
