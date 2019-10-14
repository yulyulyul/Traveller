package jso.kpl.traveller.model;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

//@Entity(tableName = "favorite_country")
public class FavoriteCountryVO implements Serializable {
    //@PrimaryKey(autoGenerate = true) // autoGenerate는 autoIncrement와 동일한 원리
    @NotNull
    // @ColumnInfo(name="") 사용하면 원하는 칼럼명으로 변경 가능
    // @Embedded 필드로 object를 갖는 경우 사용
    private int idx; // index
    private String country; // 나라명
    private String country_eng; // 영문 나라명
    private String flag; // 국기
    private String capital; // 수도
    private String continent; // 대륙
    private String language; // 언어
    private String currency; // 통화
    private String religion; // 종교

    public FavoriteCountryVO(String country, String country_eng, String flag, String capital, String continent, String language, String currency, String religion) {
        this.country = country;
        this.country_eng = country_eng;
        this.flag = flag;
        this.capital = capital;
        this.continent = continent;
        this.language = language;
        this.currency = currency;
        this.religion = religion;
    }

    @NotNull
    public int getIdx() {
        return idx;
    }

    public void setIdx(@NotNull int idx) {
        this.idx = idx;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry_eng() {
        return country_eng;
    }

    public void setCountry_eng(String country_eng) {
        this.country_eng = country_eng;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    @BindingAdapter({"bind:loadImage"})
    public static void loadImage(ImageView imageView, int imageUrl) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
    }
}
