package jso.kpl.traveller.model;

import androidx.databinding.BindingAdapter;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import java.io.Serializable;

public class SearchReq implements Serializable {

    //국가, 최대, 최소 금액
    String sr_country;
    int sr_min_cost;
    int sr_max_cost;

    public SearchReq(String sr_country, int sr_min_cost, int sr_max_cost) {
        this.sr_country = sr_country;
        this.sr_min_cost = sr_min_cost;
        this.sr_max_cost = sr_max_cost;
    }

    public String getSr_country() {
        return sr_country;
    }

    public void setSr_country(String sr_country) {
        this.sr_country = sr_country;
    }

    public int getSr_min_cost() {
        return sr_min_cost;
    }

    public void setSr_min_cost(int sr_min_cost) {
        this.sr_min_cost = sr_min_cost;
    }

    public int getSr_max_cost() {
        return sr_max_cost;
    }

    public void setSr_max_cost(int sr_max_cost) {
        this.sr_max_cost = sr_max_cost;
    }

    @Override
    public String toString() {
        return "SearchReq{" +
                "sr_country='" + sr_country + '\'' +
                ", sr_min_cost=" + sr_min_cost +
                ", sr_max_cost=" + sr_max_cost +
                '}';
    }

    @BindingAdapter("setMAX")
    public static void bindMaxValue(CrystalRangeSeekbar seekbar, int max) {
        seekbar.setMaxValue(max);
    }

    @BindingAdapter("setMIN")
    public static void bindMinValue(CrystalRangeSeekbar seekbar, int min) {
        seekbar.setMinValue(min);
    }
}
