package jso.kpl.traveller.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;


public class FavoriteCountry_m {

    private jso.kpl.traveller.model.DBHelper DBHelper;
    private SQLiteDatabase db;
    private String TABLE_NAME = DBHelper.TABLE_NAME;

    public FavoriteCountry_m(Context context) {
        DBHelper = DBHelper.getInstance(context);
        db = DBHelper.getDb();
    }

    public void createTable() {
        DBHelper.onCreate(db);
        if(!isData()) {
            String sql = "insert into " + TABLE_NAME + "(country, country_eng, flag, capital, continent, language, currency, religion) select '일본', 'Japan', 'f_japan', '도쿄', '아시아', '일본어', '엔', '신토' " +
                    "union all select '프랑스', 'France', 'f_france', '파리', '유럽', '불어', '유로', '가톨릭교' union all select '미국', 'USA', 'f_usa', '워싱턴 D.C.', '북아메리카', '영어', '달러', '개신교' " +
                    "union all select '브라질', 'Brazil', 'f_brazil', '브라질리아', '남아메리카', '포르투갈어', '헤알', '천주교' union all select '가나', 'Ghana', 'f_ghana', '아크라', '아프리카', '영어', '세디', '기독교' " +
                    "union all select '호주', 'Australia', 'f_australia', '캔버라', '오세아니아', '영어', '호주 달러', '기독교';";
            db.execSQL(sql);
        }
    }

    public boolean isData(){
        boolean isData = false;
        String sql = "select * from " + TABLE_NAME + ";";
        Cursor result = db.rawQuery(sql, null);
        // result(Cursor)객체가 비어있으면 false 리턴
        if(result.moveToFirst()){
            isData = true;
        }
        result.close();
        return isData;
    }

    public ArrayList<FavoriteCountryVO> getList() {
        ArrayList<FavoriteCountryVO> list = new ArrayList<FavoriteCountryVO>();
        String sql = "select * from " + TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("C_model", "총 리스트 수 : " + cursor.getCount());
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            FavoriteCountryVO FavoriteCountryVO = new FavoriteCountryVO(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
            list.add(FavoriteCountryVO);
            Log.d("C_model", i + "번 쨰 FavoriteCountryVO { 국가명 : " + FavoriteCountryVO.getCountry() + " / 국가 영문명 : " + FavoriteCountryVO.getCountry_eng()
                    + " / 국기 : " + FavoriteCountryVO.getFlag() + " / 수도 : " + FavoriteCountryVO.getCapital() + " / 대륙 : " + FavoriteCountryVO.getContinent()
                    + " / 언어 : " + FavoriteCountryVO.getLanguage()+ " / 통화 : " + FavoriteCountryVO.getCurrency() + " / 종교 : " + FavoriteCountryVO.getReligion() + " }");
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
}
