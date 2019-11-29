package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
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
import jso.kpl.traveller.util.JavaUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteListViewModel extends ViewModel implements Callback, GridTypePostAdapter.OnGridItemClickListener, VerticalTypePostAdapter.OnVerticalItemClickListener {

    RouteListViewModel routeListVm = this;
    /*
        Route List - Route Search나 선호 국가를 눌렀을 때 해당 조건에 해당하는 포스트들의 리스트를 뿌려주는 화면
        현재 구성 TabLayout + FrameLayout(Fragment * 2)
         */
    String TAG = "Trav.MainRouteViewModel.";

    String imageUri = "android.resource://jso.kpl.toyroutesearch/drawable/i_blank_person_icon";

    public MutableLiveData<List<ListItem>> postList = new MutableLiveData<>();

    public FragmentManager fm;

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    //탭레이아웃 리스너
    TabLayout.OnTabSelectedListener listener;

    public GridTypePost gt_post;
    public VerticalTypePost vt_post;

    //검색 결과 리스트
    public MutableLiveData<ListItem> postLD = new MutableLiveData<>();

    //각 adapter - 해당 뷰페이저의 Adapter, 각 뷰페이저의 프래그먼트들의 Adapter
    public GridTypePostAdapter gridAdapter;
    public VerticalTypePostAdapter verticalAdapter;

    //StaggeredGridLayout의 한줄 당 아이템 갯수와 간격을 설정하는 객체
    public GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, 10, true);

    //통신 관련 -------------------------------------------------------------------------------------
    PostAPI postAPI = WebService.INSTANCE.getClient().create(PostAPI.class);
    int lastPid = 0;
    int categoryNo = 0;

    public SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.d(TAG, "onRefresh: 된다아아");
        }
    };

    //Grid View의 스크롤 상태
    public RecyclerView.OnScrollListener onGridScrollListener;

    //vertical view 스크롤 상태
    public RecyclerView.OnScrollListener onVerticalScrollListener;

    //----------------------------------------------------------------------------------------------
    public RouteListViewModel() {

        postList.setValue(new ArrayList<ListItem>());

        gridAdapter = new GridTypePostAdapter(postList.getValue());
        verticalAdapter = new VerticalTypePostAdapter(postList.getValue());

        gridAdapter.setOnGridItemClickListener(this);
        verticalAdapter.setOnVerticalItemClickListener(this);

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
        gt_post = new GridTypePost(routeListVm);
        fm.beginTransaction().add(container.getId(), gt_post).commit();

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
                            gt_post = new GridTypePost(routeListVm);
                            fm.beginTransaction().add(container.getId(), gt_post).commit();
                        } else {
                            fm.beginTransaction().show(gt_post).commit();
                        }

                        if (vt_post != null) fm.beginTransaction().hide(vt_post).commit();

                        gridAdapter.notifyDataSetChanged();
                        break;
                    case 1:

                        tab.getIcon().setTint(App.INSTANCE.getResources().getColor(R.color.clicked));

                        if (vt_post == null) {
                            vt_post = new VerticalTypePost(routeListVm);
                            fm.beginTransaction().add(container.getId(), vt_post).commit();
                        } else {
                            fm.beginTransaction().show(vt_post).commit();
                        }

                        if (gt_post != null) fm.beginTransaction().hide(gt_post).commit();

                        verticalAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

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

                        gt_post = new GridTypePost(routeListVm);

                        fm.beginTransaction().add(container.getId(), gt_post).commit();
                        break;
                    case 1:
                        fm.beginTransaction().remove(vt_post).commit();

                        vt_post = new VerticalTypePost(routeListVm);

                        fm.beginTransaction().add(container.getId(), vt_post).commit();

                        break;
                }

            }
        };

        return listener;
    }

    public void searchByCondition(MyPageItem item) {

        Call<ResponseResult<List<ListItem>>> call;

        if (item != null) {

            final int no = item.getType();

            switch (no) {
                case 0:
                    Log.d(TAG, "searchByCondition-Search");

                    SearchReq searchReq = (SearchReq) item.getO();

                    call = postAPI.searchByCondition(searchReq.getSr_country(), searchReq.getSr_max_cost(), searchReq.getSr_min_cost(), lastPid, categoryNo);
                    break;
                case 3:
                    Log.d(TAG, "searchByCondition-Enroll");
                    call = postAPI.searchByEnroll((int) item.getO(), lastPid);
                    break;
                default:
                    call = null;
                    break;
            }

            call.enqueue(this);
        } else {

            Log.d(TAG, "searchByCondition: Null");
        }


    }

    /*
    각 프래그먼트의 리사이클러 뷰 아이템을 클릭하는 이벤트
     */
    @Override
    public void GridItemClicked(int p_id) {

        if (App.INSTANCE != null) {

            Intent intent = new Intent(App.INSTANCE, RouteOtherDetail.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("p_id", p_id);
            App.INSTANCE.startActivity(intent);
        }
    }

    @Override
    public void VerticalItemClicked(int p_id) {

        if (App.INSTANCE != null) {

            Intent intent = new Intent(App.INSTANCE, RouteOtherDetail.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("p_id", p_id);
            App.INSTANCE.startActivity(intent);
        }
    }

    //-----------------------------------------------------------------------------------------------

    //태그를 추가할 때 TextView(태그값)를 동적으로 생성하는 함수
    public LinearLayout addCategoryItem(Context context, String tag) {

        //버튼 있는 버전 - 현재 실패
        LinearLayout categoryItem = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(JavaUtil.dpToPx(5), JavaUtil.dpToPx(5), JavaUtil.dpToPx(5), JavaUtil.dpToPx(5));
        params.gravity = Gravity.CENTER_VERTICAL;

        categoryItem.setLayoutParams(params);
        categoryItem.setPadding(10, 10, 10, 10);
        categoryItem.setBackgroundResource(R.drawable.s_border_round_square);
        categoryItem.setOrientation(LinearLayout.HORIZONTAL);
        categoryItem.setVerticalGravity(Gravity.CENTER_VERTICAL);

        TextView tv = new TextView(context);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(5, 0, 5, 0);
        tv.setTextColor(context.getColor(R.color.colorWhite));
        tv.setText(tag);

        params.leftMargin = 15;
        params.rightMargin = 10;

        categoryItem.addView(tv);

        return categoryItem;

    }

    //-----------------------------------------------------------------------------------------------

    //카테고리 레이아웃 추가 함수
    public void addCategoryLayout(final Context context, final LinearLayout layout) {

        List<String> categoryList = new ArrayList<>();
        categoryList.add("전체");

        String[] categories = App.INSTANCE.getResources().getStringArray(R.array.category);

        for (int k = 0; k < categories.length; k++) {
            categoryList.add(categories[k]);
        }

        //선택한 카테고리의 수에 따라 텍스트 뷰 생성
        for (int i = 0; i < categoryList.size(); i++) {

            final LinearLayout tv = addCategoryItem(context, categoryList.get(i));

            layout.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "태그: " + ((TextView) tv.getChildAt(0)).getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //통신 결과-----------------------------------------------------------------------------------

    @Override
    public void onResponse(Call call, Response response) {

        Log.d(TAG, "2. 데이터 통신 - response");

        if (response.body() != null) {

            ResponseResult<List<ListItem>> result = (ResponseResult<List<ListItem>>) response.body();

            if (result.getRes_type() == 1)
                postList.getValue().addAll(result.getRes_obj());
            else
                Log.d(TAG, "onResponse: " + result.getRes_msg());

            if(postList.getValue() != null && postList.getValue().size() > 0)
                 lastPid = postList.getValue().get(postList.getValue().size() - 1).getP_id();

            gridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {

        Log.d(TAG, "3. 데이터 통신 - fail");

        Toast.makeText(App.INSTANCE, "에러로 인해 포스트를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Post init load onFailure: ", t);
    }


    //----------------------------------------------------------------------------------------------

    @BindingAdapter("onAddScrollListener")
    public static void bindAddScrollListener(RecyclerView rv, RecyclerView.OnScrollListener listener) {
        if (rv != null) {
            rv.addOnScrollListener(listener);
        }
    }

    @BindingAdapter("onRefreshListener")
    public static void bindRefreshListener(SwipeRefreshLayout swipeRefreshLayout, SwipeRefreshLayout.OnRefreshListener listener) {

        swipeRefreshLayout.setOnRefreshListener(listener);
        swipeRefreshLayout.setRefreshing(false);

    }

}
