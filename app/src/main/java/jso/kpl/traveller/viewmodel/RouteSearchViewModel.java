package jso.kpl.traveller.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.util.CurrencyChange;

public class RouteSearchViewModel extends ViewModel {

    String TAG = "Trav.RouteSearchViewModel.";

    Context context;

    //국가, 선택(최소, 최대)금액
    public MutableLiveData<SearchReq> srLD = new MutableLiveData<>();

    //입력한 최대 금액(EditText)
    public MutableLiveData<String> inputMaxCost = new MutableLiveData<>();

    //seek bar 최대 수치 Text
    public MutableLiveData<String> seekBarMax = new MutableLiveData<>();

    //seek bar 최소 수치 Text
    public MutableLiveData<String> seekBarMin = new MutableLiveData<>();

    //현재 seek bar 최대, 최소 노드의 범위 Text
    public MutableLiveData<String> costRange = new MutableLiveData<>();

    public RouteSearchViewModel(Context context) {
        this.context = context;

        //국가와 seek bar의 초기값
        srLD.setValue(new SearchReq("", 10000, 10001));

        //각 max min Text 입력
        seekBarMax.setValue(CurrencyChange.moneyFormatToWon(srLD.getValue().getSr_max_cost()));
        seekBarMin.setValue(CurrencyChange.moneyFormatToWon(srLD.getValue().getSr_min_cost()));

        Log.d(TAG + "Constructor", ": RouteSearchViewModel");
    }

    //최종 검색 조건 이벤트
    public void onSearchClicked(){

        if(srLD.getValue().getSr_country() != null){
            if(srLD.getValue().getSr_country().length() > 0){
                Log.d(TAG + "onSearchClicked", srLD.getValue().toString());
                Toast.makeText(context, "국가: " + srLD.getValue().getSr_country()
                        + "\n가격 범위: " + costRange.getValue() ,Toast.LENGTH_LONG).show();

                //최종 검색 조건을 가지고 이동
                Intent goToResult = new Intent(context, RouteList.class);

                goToResult.putExtra("req", new MyPageItem(srLD.getValue(), 0));
                context.startActivity(goToResult);

            }else{
                Toast.makeText(context, "국가란이 비어있습니다." ,Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(context, "국가란이 비어있습니다." ,Toast.LENGTH_LONG).show();
        }
    }
}
