package jso.kpl.traveller.model;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

//@Entity(tableName = "country_list")
public class Country implements Serializable {

    String TAG = "Trav.Country.";

    //@PrimaryKey(autoGenerate = true) // autoGenerate는 autoIncrement와 동일한 원리
    @NotNull

    private int ct_no; // index
    private String ct_name; // 나라명
    private String ct_eng_name; // 영문 나라명
    private String ct_capital; // 수도
    private String ct_continent; // 대륙
    private String ct_language; // 언어
    private String ct_currency; // 통화
    private String ct_latlng;

    private String ct_bg;
    private String ct_flag;

    private int is_favorite;

    private String r_name;
    private String r_eng_name;
    private String r_explain;
    private String r_img;

    public MutableLiveData<Boolean> is_favorite_ld = new MutableLiveData<>();

    public Country(String add_flag) {
        this.ct_eng_name = add_flag;
    }

    public int getCt_no() {
        return ct_no;
    }

    public void setCt_no(int ct_no) {
        this.ct_no = ct_no;
    }

    public String getCt_name() {
        return ct_name;
    }

    public void setCt_name(String ct_name) {
        this.ct_name = ct_name;
    }

    public String getCt_eng_name() {
        return ct_eng_name;
    }

    public void setCt_eng_name(String ct_eng_name) {
        this.ct_eng_name = ct_eng_name;
    }

    public String getCt_capital() {
        return ct_capital;
    }

    public void setCt_capital(String ct_capital) {
        this.ct_capital = ct_capital;
    }

    public String getCt_continent() {
        return ct_continent;
    }

    public void setCt_continent(String ct_continent) {
        this.ct_continent = ct_continent;
    }

    public String getCt_language() {
        return ct_language;
    }

    public void setCt_language(String ct_language) {
        this.ct_language = ct_language;
    }

    public String getCt_currency() {
        return ct_currency;
    }

    public void setCt_currency(String ct_currency) {
        this.ct_currency = ct_currency;
    }

    public String getCt_latlng() {
        return ct_latlng;
    }

    public void setCt_latlng(String ct_latlng) {
        this.ct_latlng = ct_latlng;
    }

    public String getCt_bg() {
        return ct_bg;
    }

    public void setCt_bg() {

        String imgStr = "pr_" + getCt_eng_name().toLowerCase();

        if (imgStr.contains(" "))
            imgStr = imgStr.replace(" ", "_");

        this.ct_bg = imgStr;
    }

    public String getCt_flag() {
        return ct_flag;
    }

    //국가 국기 이미지 drawable에 있는 이름과 맞추기
    public void setCt_flag() {

        String imgStr = "f_" + getCt_eng_name().toLowerCase();

        if (imgStr.contains(" "))
            imgStr = imgStr.replace(" ", "_");

        this.ct_flag = imgStr;
    }

    public String getR_img() {
        return r_img;
    }

    public void setR_img() {

        String imgStr = getR_eng_name().toLowerCase();

        if (imgStr.contains(" "))
            imgStr = imgStr.replace(" ", "_");

        this.r_img = imgStr;
    }

    public int getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(int is_favorite) {
        this.is_favorite = is_favorite;
    }

    public MutableLiveData<Boolean> getIs_favorite_ld() {
        return is_favorite_ld;
    }

    public void setIs_favorite_ld() {
        this.is_favorite_ld = new MutableLiveData<>();

        if (getIs_favorite() == 1)
            this.is_favorite_ld.setValue(true);
        if (getIs_favorite() == 0)
            this.is_favorite_ld.setValue(false);
    }

    public String getR_name() {
        return r_name;
    }

    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public String getR_eng_name() {
        return r_eng_name;
    }

    public void setR_eng_name(String r_eng_name) {
        this.r_eng_name = r_eng_name;
    }

    public String getR_explain() {
        return r_explain;
    }

    public void setR_explain(String r_explain) {
        this.r_explain = r_explain;
    }

    @Override
    public String toString() {
        return "Country{" +
                "ct_no=" + ct_no +
                ", ct_name='" + ct_name + '\'' +
                ", ct_eng_name='" + ct_eng_name + '\'' +
                ", ct_capital='" + ct_capital + '\'' +
                ", ct_continent='" + ct_continent + '\'' +
                ", ct_language='" + ct_language + '\'' +
                ", ct_currency='" + ct_currency + '\'' +
                ", ct_latlng='" + ct_latlng + '\'' +
                ", r_name='" + r_name + '\'' +
                ", r_eng_name='" + r_eng_name + '\'' +
                ", r_explain='" + r_explain + '\'' +
                ", is_favorite=" + is_favorite +
                '}';
    }
}
