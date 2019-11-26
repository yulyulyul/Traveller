package jso.kpl.traveller.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.ui.RouteList;
import jso.kpl.traveller.ui.RouteSearch;
import jso.kpl.traveller.util.CurrencyChange;

public class RouteSearchViewModel extends ViewModel {

    String TAG = "Trav.RouteSearchViewModel.";

    Application app;

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

    public View.OnClickListener onSearchClickListener;
    //뒤로가기 클릭리스너
    public View.OnClickListener onBackClickListener;

    public RouteSearchViewModel() {

        this.app = (Application) App.INSTANCE;

        //국가와 seek bar의 초기값
        srLD.setValue(new SearchReq("", 10000, 10001));

        //각 max min Text 입력
        seekBarMax.setValue(CurrencyChange.moneyFormatToWon(srLD.getValue().getSr_max_cost()));
        seekBarMin.setValue(CurrencyChange.moneyFormatToWon(srLD.getValue().getSr_min_cost()));

        costRange.setValue("초기화");
        Log.d(TAG + "Constructor", ": RouteSearchViewModel");
    }

    public void onResetClicked(){
        srLD.setValue(new SearchReq("", 10000, 10001));
        inputMaxCost.setValue("");
        seekBarMax.setValue(CurrencyChange.moneyFormatToWon(10001));
        seekBarMin.setValue(CurrencyChange.moneyFormatToWon(10000));
    }

    //최종 검색 조건 이벤트
    public int onSearchClicked(){

        int minCost = 10000;
        int maxCost = 10001;

        if(srLD.getValue().getSr_country() != null){
            if(srLD.getValue().getSr_country().length() > 0){

                if(!costRange.getValue().equals("초기화")){

                    String[] str = costRange.getValue().replace("₩", "").replace(",", "").trim().split(" ~ ");

                    minCost = Integer.parseInt(str[0]);
                    maxCost = Integer.parseInt(str[1]);
                }

                srLD.getValue().setSr_max_cost(maxCost);
                srLD.getValue().setSr_min_cost(minCost);

                Log.d(TAG, "onSearchClicked: " + srLD.getValue().toString());
                //최종 검색 조건을 가지고 이동
                Intent goToResult = new Intent(app, RouteList.class);

                goToResult.putExtra("req", new MyPageItem(srLD.getValue(), 0));

                app.startActivity(goToResult);

                return 1;
            }else{
                Toast.makeText(app, "국가란이 비어있습니다." ,Toast.LENGTH_LONG).show();
                return 0;
            }
        }
        else{
            Toast.makeText(app, "국가란이 비어있습니다." ,Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    @Override
    public void onCleared() {
        super.onCleared();
    }
}
