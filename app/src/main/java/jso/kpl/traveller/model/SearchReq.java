package jso.kpl.traveller.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.util.JavaUtil;

public class SearchReq implements Serializable {

    //국가, 최대, 최소 금액
    String sr_country;
    int sr_min_cost;
    int sr_max_cost;

    String sr_tag;

    //날짜
    String sr_date;

    public SearchReq(String sr_country, int sr_min_cost, int sr_max_cost) {
        this.sr_country = sr_country;
        this.sr_min_cost = sr_min_cost;
        this.sr_max_cost = sr_max_cost;

        sr_date = JavaUtil.currentTime();
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

    public String getSr_tag() {
        return sr_tag;
    }

    public void setSr_tag(String sr_tag) {
        this.sr_tag = sr_tag;
    }

    @Override
    public String toString() {
        return "SearchReq{" +
                "sr_country='" + sr_country + '\'' +
                ", sr_min_cost=" + sr_min_cost +
                ", sr_max_cost=" + sr_max_cost +
                ", sr_tag='" + sr_tag + '\'' +
                ", sr_date='" + sr_date + '\'' +
                '}';
    }
}
