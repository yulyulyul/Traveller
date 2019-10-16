package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.model.FavoriteCountryInfoVO;
import jso.kpl.traveller.model.FavoriteCountryVO;
import jso.kpl.traveller.ui.adapters.FavoriteCountryInfoItemAdapter;
import jso.kpl.traveller.util.JavaUtil;

public class FavoriteCountryInfo_VM extends BaseObservable {

    private Context context;
    private Intent intent;

    public MutableLiveData<GridLayoutManager> layoutManager = new MutableLiveData<>();
    public MutableLiveData<List<FavoriteCountryInfoVO>> list = new MutableLiveData<>();
    public MutableLiveData<FavoriteCountryVO> favoriteCountryVO = new MutableLiveData<>();
    public FavoriteCountryInfoItemAdapter adapter = new FavoriteCountryInfoItemAdapter(list);

    public int imageUrl;

    public FavoriteCountryInfo_VM(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        // favoriteCountryVO 초기화
        favoriteCountryVO.setValue(getIntentExtra());
        Log.d("CountryInfoAct", "선택된 국가 : " + favoriteCountryVO.getValue().getCountry());
        imageUrl = JavaUtil.getImage(context, favoriteCountryVO.getValue().getFlag());
    }

    public void init() {
        // RecyclerView 세팅
        setRecyclerView();
    }

    // intent에서 선택된 FavoriteCountryVO 객체 반환
    public FavoriteCountryVO getIntentExtra() {
        FavoriteCountryVO favoriteCountryVO = (FavoriteCountryVO) intent.getSerializableExtra("selectFavoriteCountryVO");

        return favoriteCountryVO;
    }

    public void country_warningClickEvent() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.0404.go.kr/dev/country.mofa?group_idx=&stext=" + favoriteCountryVO.getValue().getCountry()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextCompat.startActivity(context, intent, null);
    }

    public void country_add_btnClickEvent() {
        String country = favoriteCountryVO.getValue().getCountry();
        Toast.makeText(context,"선호 국가(" + country + ") 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void setRecyclerView() {
        layoutManager.setValue(new GridLayoutManager(context, 3));
        // recyclerView에 표시할 데이터 리스트 생성
        ArrayList<FavoriteCountryInfoVO> list = new ArrayList<>();
        // 추후 루트 게시글 리스트로 변경
        for(int i = 0; i < 6; i++){
            switch(i) {
                case 0:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img1"));
                    break;
                case 1:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img2"));
                    break;
                case 2:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img3"));
                    break;
                case 3:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img4"));
                    break;
                case 4:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img5"));
                    break;
                case 5:
                    list.add(new FavoriteCountryInfoVO("dummy_travel_img6"));
                    break;
            }
        }
        this.list.setValue(list);
        // 클릭 이벤트 지정
        adapter.setOnItemClickListener(new FavoriteCountryInfoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position){
                Toast.makeText(context,"해당 게시글로 이동.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
