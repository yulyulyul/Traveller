package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.ui.adapters.ImageRouteListAdapter;
import jso.kpl.traveller.ui.adapters.MainRouteListAdapter;
import jso.kpl.traveller.ui.adapters.PostRouteListAdapter;
import jso.kpl.traveller.util.GridSpacingItemDecoration;

public class MainRouteListViewModel extends ViewModel implements ImageRouteListAdapter.OnImagePostClickListener, PostRouteListAdapter.OnPostClickListener {

    /*
    Route List - Route Search나 선호 국가를 눌렀을 때 해당 조건에 해당하는 포스트들의 리스트를 뿌려주는 화면
    현재 구성 TabLayout + ViewPager
     -> 문제점: viewpager는 앞뒤의 화면도 동시에 생성되기에 데이터를 많이 부르는 화면에는 부적합하기에
                추후 FrameLayout을 이용해서 두개의 프래그먼트를 Attach Detech 변경할 예정
     */
    String TAG = "Trav.MainRouteViewModel.";
    String imageUri = "android.resource://jso.kpl.toyroutesearch/drawable/i_blank_person_icon";

    Context context;
    FragmentManager fm;

    //검색 결과 리스트
    public MutableLiveData<RePost> rePost = new MutableLiveData<>();

    //각 adapter - 해당 뷰페이저의 Adapter, 각 뷰페이저의 프래그먼트들의 Adapter
    public MainRouteListAdapter mainAdapter;
    public ImageRouteListAdapter imgAdapter;
    public PostRouteListAdapter postAdapter;

    //StaggeredGridLayout의 한줄 당 아이템 갯수와 간격을 설정하는 객체
    public GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, 10, true);

    List<RePost> rePostList = new ArrayList<>();

    public MutableLiveData<MyPageItem> myPageItem = new MutableLiveData<>();

    public MainRouteListViewModel(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;

        mainAdapter = new MainRouteListAdapter(fm, 2);
        imgAdapter = new ImageRouteListAdapter(this);
        postAdapter = new PostRouteListAdapter(this);

        imgAdapter.setOnImagePostClickListener(this);
        postAdapter.setOnPostClickListener(this);
        getPostList();
    }

    public void getPostList(){
        //통신 처리

        if (myPageItem.getValue() != null) {

            final int type = myPageItem.getValue().getType();

            switch (type){

                //Route Search의 검색 결과
                case 0:
                    Log.d(TAG + "getPostList", "Route Search: ");
                    break;
                //Preferred Country More
                case 1:
                    Log.d(TAG + "getPostList", "Preferred Country");
                    break;
                //Favorites Post More
                case 2:
                    Log.d(TAG + "getPostList", "Favorites Post");
                    break;
                //Enrolled Post More
                case 3:
                    Log.d(TAG + "getPostList", "Enrolled Post");
                    break;
            }
            
        }else{
            Log.d(TAG, "getPostList: 값이 또 없데");
        }
        
        //rePostList = response.body();

        rePostList.add(new RePost(imageUri, new Post(0, "나에게로 떠나는 여행", "asle1000", "10000", "korea")));
        rePostList.add(new RePost(imageUri, new Post(1, "비망록", "asle1000", "10000", "korea")));
        rePostList.add(new RePost(imageUri, new Post(2, "가시", "asle1000", "10000", "korea")));

        imgAdapter.addItems(rePostList);
        imgAdapter.notifyDataSetChanged();

        postAdapter.addItems(rePostList);
        postAdapter.notifyDataSetChanged();

    }


    @Override
    public void imagePostClicked(RePost rePost) {

    }

    @Override
    public void postClicked(RePost rePost) {

    }
}
