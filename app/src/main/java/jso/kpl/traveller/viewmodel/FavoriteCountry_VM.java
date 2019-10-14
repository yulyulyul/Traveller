package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import jso.kpl.traveller.model.FavoriteCountryVO;
import jso.kpl.traveller.model.FavoriteCountry_m;
import jso.kpl.traveller.ui.FavoriteCountryInfo;
import jso.kpl.traveller.ui.adapters.FavoriteCountryItemAdapter;

public class FavoriteCountry_VM extends BaseObservable {

    private Context context;

    public MutableLiveData<List<FavoriteCountryVO>> list = new MutableLiveData<>();
    public FavoriteCountryItemAdapter adapter = new FavoriteCountryItemAdapter(list);

    private jso.kpl.traveller.model.FavoriteCountry_m FavoriteCountry_m;

    public FavoriteCountry_VM(Context context) {
        this.context = context;
    }

    public void init() {
        // 모델 세팅
        setModel();
        // RecyclerView 세팅
        setRecyclerView();
    }

    public void setModel() {
        FavoriteCountry_m = new FavoriteCountry_m(context);
        FavoriteCountry_m.createTable();
    }

    public void setRecyclerView() {
        // recyclerView에 표시할 데이터 리스트 생성, +++ 리스트 null 값 처리
        list.setValue(FavoriteCountry_m.getList());

        // 클릭 이벤트 지정
        adapter.setOnItemClickListener(new FavoriteCountryItemAdapter.OnItemClickListener() {
            // 선호 국가 추가
            @Override
            public void onItemClick(int position){
                String country = list.getValue().get(position).getCountry();
                Toast.makeText(context,"선호 국가(" + country + ") 추가되었습니다.",Toast.LENGTH_SHORT).show();
            }

            // 상세 정보 페이지 전환
            @Override
            public void onBtnClick(int position) {
                FavoriteCountryVO selectCountry = list.getValue().get(position);
                Intent intent = new Intent(context, FavoriteCountryInfo.class);
                // 선택된 국가 정보 전달
                intent.putExtra("selectFavoriteCountryVO", selectCountry);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ContextCompat.startActivity(context, intent, null);
            }
        });
    }
}
