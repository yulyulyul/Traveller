package jso.kpl.traveller.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;

import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteSearchBinding;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.viewmodel.RouteSearchViewModel;

public class RouteSearch extends AppCompatActivity {

    String TAG = "Trav.RouteSearch.";

    RouteSearchBinding binding;
    RouteSearchViewModel routeSearchVm;

    Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        act = this;
        routeSearchVm = new RouteSearchViewModel();

        //route search 바인딩 작업
        binding = DataBindingUtil.setContentView(this, R.layout.route_search);
        binding.setSearchVm(routeSearchVm);
        binding.setLifecycleOwner(this);

        JavaUtil.onTouchDownKey(binding.searchToolbar, binding.searchPlace, binding.searchExpenses);

        //max min 노드가 움직이는 값을 가져오는 이벤트 함수
        binding.crystalRangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                Log.d(TAG, "valueChanged: " + maxValue);

                binding.getSearchVm().costRange.setValue(
                        CurrencyChange.moneyFormatToWon(Integer.parseInt(minValue.toString()))
                                + " ~ "
                                + CurrencyChange.moneyFormatToWon(Integer.parseInt(maxValue.toString()))
                );

            }
        });

        binding.getSearchVm().onBackClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
            }
        };

        binding.getSearchVm().onSearchClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.getSearchVm().onSearchClicked() == 1){
                    onClear();
                }
            }
        };

        binding.executePendingBindings();

        //max min 노드의 현재 위치 값을 가져오는 이벤트 함수
        binding.crystalRangeSeekBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                binding.getSearchVm().srLD.getValue().setSr_min_cost(Integer.parseInt(minValue.toString()));
                binding.getSearchVm().srLD.getValue().setSr_max_cost(Integer.parseInt(maxValue.toString()));

                Log.d(TAG, "현재 미니멈 코스트: " + binding.getSearchVm().srLD.getValue().getSr_min_cost());
                Log.d(TAG, "현재 맥스 코스트: " + binding.getSearchVm().srLD.getValue().getSr_max_cost());
            }
        });

        binding.getSearchVm().inputMaxCost.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "비용 입력 값: " + s);

                if (!s.isEmpty()) {

                    int maxCost;

                    if(s.contains("₩")){
                        maxCost = Integer.parseInt(s.replace("₩", "").replace(",", ""));
                    }else{
                        maxCost = Integer.parseInt(s);
                    }

                    //입력값이 만원 이하이면 max값을 10000원으로 고정
                    if (maxCost > 100000){
                        //시크바의 맥스 코스트에 입력
                        binding.crystalRangeSeekBar.setMaxValue(maxCost);

                        //시크바의 맥스트 코스트 텍스트
                        binding.getSearchVm().seekBarMax.setValue(CurrencyChange.moneyFormatToWon(maxCost));

                        binding.getSearchVm().costRange.setValue(
                                CurrencyChange.moneyFormatToWon(0)
                                        + " ~ "
                                        + CurrencyChange.moneyFormatToWon(maxCost)
                        );

                    }else {
                        //시크바의 맥스 코스트에 입력
                        binding.crystalRangeSeekBar.setMaxValue(100000);

                        //시크바의 맥스트 코스트 텍스트
                        binding.getSearchVm().seekBarMax.setValue(CurrencyChange.moneyFormatToWon(100000));

                        binding.getSearchVm().costRange.setValue(
                                CurrencyChange.moneyFormatToWon(0)
                                        + " ~ "
                                        + CurrencyChange.moneyFormatToWon(100000)
                        );
                    }

                } else{
                    binding.getSearchVm().costRange.setValue("초기화");
                }

            }
        });

        binding.getSearchVm().onBackgroundClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JavaUtil.downKeyboard(act);
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        onClear();
    }

    public void onClear(){
        binding.getSearchVm().onCleared();
        finish();
    }
}
