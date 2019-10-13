package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.RePost;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.ui.adapters.ImageRouteListAdapter;
import jso.kpl.traveller.ui.adapters.MainRouteListAdapter;
import jso.kpl.traveller.ui.adapters.PostRouteListAdapter;
import jso.kpl.traveller.util.GridSpacingItemDecoration;

public class MainRouteListViewModel extends BaseObservable {

    String TAG = "Trav.MainRouteViewModel.";
    String imageUri = "android.resource://jso.kpl.toyroutesearch/drawable/i_blank_person_icon";

    Context context;
    FragmentManager fm;

    //검색 결과 리스트
    public MutableLiveData<RePost> rePost = new MutableLiveData<jso.kpl.traveller.model.RePost>();

    //각 adapter
    public MainRouteListAdapter mainAdapter;
    public ImageRouteListAdapter imgAdapter;
    public PostRouteListAdapter postAdapter;

    public GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, 10, true);

    List<RePost> rePostList = new ArrayList<>();

    public SearchReq searchReq;

    public MainRouteListViewModel(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;

        mainAdapter = new MainRouteListAdapter(fm, 2);
        imgAdapter = new ImageRouteListAdapter(this);
        postAdapter = new PostRouteListAdapter(this);

        getPostList();
    }

    public SearchReq getSearchReq() {
        return searchReq;
    }

    public void setSearchReq(SearchReq searchReq) {
        this.searchReq = searchReq;

        Log.d(TAG + "setSearchReq", "결과값(나라): " +searchReq.getSr_country());
    }

    public void getPostList(){
        //통신 처리

        //rePostList = response.body();

        rePostList.add(new RePost(imageUri, new Post(0, "나에게로 떠나는 여행", "asle1000", "10000", "korea")));
        rePostList.add(new RePost(imageUri, new Post(1, "비망록", "asle1000", "10000", "korea")));
        rePostList.add(new RePost(imageUri, new Post(2, "가시", "asle1000", "10000", "korea")));

        imgAdapter.addItems(rePostList);
        imgAdapter.notifyDataSetChanged();

        postAdapter.addItems(rePostList);
        postAdapter.notifyDataSetChanged();

    }


}
