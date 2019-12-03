package jso.kpl.traveller.model;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

//@Entity(tableName = "favorite_country")
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
    private String ct_religion; // 종교
    private String ct_flag;
    private boolean ct_is_add;

    public MutableLiveData<Boolean> ct_is_add_ld = new MutableLiveData<>();

    public Country(int ct_no, String ct_name, String ct_eng_name, String ct_capital, String ct_continent, String ct_language, String ct_currency, String ct_religion,  boolean ct_is_add) {
        this.ct_no = ct_no;
        this.ct_name = ct_name;
        this.ct_eng_name = ct_eng_name;
        this.ct_capital = ct_capital;
        this.ct_continent = ct_continent;
        this.ct_language = ct_language;
        this.ct_currency = ct_currency;
        this.ct_religion = ct_religion;
        this.ct_is_add_ld.setValue(ct_is_add);
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

    public String getCt_religion() {
        return ct_religion;
    }

    public void setCt_religion(String ct_religion) {
        this.ct_religion = ct_religion;
    }

    public String getCt_flag() {
        return ct_flag;
    }

    //국가 국기 이미지 drawable에 있는 이름과 맞추기
    public void setCt_flag(){

        Log.d(TAG, "최초 국가 영어 이름: " + getCt_eng_name());

        String imgStr = "f_" + getCt_eng_name().toLowerCase();

        Log.d(TAG, "소문자화 국가 영어 이름: " + imgStr);
        if(imgStr.contains(" ")){
            imgStr = imgStr.replace(" ", "_");

            Log.d(TAG, "띄어진 국가 영어 이름: " + imgStr);
        }

        this.ct_flag = "android.resource://jso.kpl.traveller/drawable/" + imgStr;
    }

    @Override
    public String toString() {
        return "Country{" +
                "TAG='" + TAG + '\'' +
                ", ct_no=" + ct_no +
                ", ct_name='" + ct_name + '\'' +
                ", ct_eng_name='" + ct_eng_name + '\'' +
                ", ct_capital='" + ct_capital + '\'' +
                ", ct_continent='" + ct_continent + '\'' +
                ", ct_language='" + ct_language + '\'' +
                ", ct_currency='" + ct_currency + '\'' +
                ", ct_religion='" + ct_religion + '\'' +
                ", ct_flag='" + ct_flag + '\'' +
                '}';
    }
}
