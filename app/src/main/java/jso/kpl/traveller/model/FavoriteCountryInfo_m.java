package jso.kpl.traveller.model;

import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "favorite_country_info")
public class FavoriteCountryInfo_m implements Serializable {
    private String img;

    public FavoriteCountryInfo_m(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }
}
