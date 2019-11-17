package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.ui.adapters.RouteOtherDetailCategoryItemAdapter;

public class RouteOtherDetail_VM extends BaseObservable {

    public MutableLiveData<GridLayoutManager> layoutManager = new MutableLiveData<>();
    public MutableLiveData<List<String>> list = new MutableLiveData<>();
    public RouteOtherDetailCategoryItemAdapter adapter = new RouteOtherDetailCategoryItemAdapter(list);
    public MutableLiveData<Boolean> like = new MutableLiveData<>();

    //private jso.kpl.traveller.model.FavoriteCountry_m FavoriteCountry_m;

    public RouteNodeAdapter routeNodeAdapter;

    public RouteNodeAdapter getRouteNodeAdapter() {
        return routeNodeAdapter;
    }

    public void setRouteNodeAdapter(RouteNodeAdapter routeNodeAdapter) {
        this.routeNodeAdapter = routeNodeAdapter;
    }

    public void init() {
        // 모델 세팅
        //setModel();
        // RecyclerView 세팅
        like.setValue(false);
        setRecyclerView();
    }

    public void setModel() {
        //FavoriteCountry_m = new FavoriteCountry_m(context);
        //FavoriteCountry_m.createTable();
    }

    public void my_post_btnClickEvent() {
        Toast.makeText(App.INSTANCE,"나의 포스트로 이동", Toast.LENGTH_SHORT).show();
    }

    public void post_add_btnClickEvent() {
        Toast.makeText(App.INSTANCE,"포스트 추가", Toast.LENGTH_SHORT).show();
    }

    public void like_btnClickEvent() {
        System.out.println(like.getValue());
        if(like.getValue()) {
            like.setValue(false);
        } else {
            like.setValue(true);
        }
        Toast.makeText(App.INSTANCE,"좋아요 버튼 클릭", Toast.LENGTH_SHORT).show();
    }

    public void setRecyclerView() {
        layoutManager.setValue(new GridLayoutManager(App.INSTANCE, 3));
        // recyclerView에 표시할 데이터 리스트 생성
        ArrayList<String> list = new ArrayList<>();
        // 추후 루트 게시글 리스트로 변경
        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0:
                    list.add("Food");
                    break;
                case 1:
                    list.add("Extream");
                    break;
                case 2:
                    list.add("Tour");
                    break;
                case 3:
                    list.add("Leisure");
                    break;
                case 4:
                    list.add("Resort");
                    break;
                case 5:
                    list.add("Attraction");
                    break;
            }
        }

        // recyclerView에 표시할 데이터 리스트 생성, +++ 리스트 null 값 처리
        this.list.setValue(list);
    }
}
