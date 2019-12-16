package jso.kpl.traveller.model;

import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "detail_country_info")
public class FavoriteCountryInfoVO implements Serializable {
    private String img;

    public FavoriteCountryInfoVO(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }
}
