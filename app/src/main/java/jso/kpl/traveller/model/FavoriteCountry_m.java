package jso.kpl.traveller.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class FavoriteCountry_m {

//    private jso.kpl.traveller.model.DBHelper DBHelper;
//    private SQLiteDatabase db;
//    private String TABLE_NAME = DBHelper.TABLE_NAME;
//
//    public FavoriteCountry_m(Context context) {
//        DBHelper = DBHelper.getInstance(context);
//        db = DBHelper.getDb();
//    }
//
//    public void createTable() {
//        DBHelper.onCreate(db);
//        if (!isData()) {
//            String sql = "insert into " + TABLE_NAME + "(country, country_eng, flag, capital, continent, language, currency, religion) select '일본', 'Japan', 'f_japan', '도쿄', '아시아', '일본어', '엔', '신토' " +
//                    "union all select '프랑스', 'France', 'f_france', '파리', '유럽', '불어', '유로', '가톨릭교' union all select '미국', 'USA', 'f_usa', '워싱턴 D.C.', '북아메리카', '영어', '달러', '개신교' " +
//                    "union all select '브라질', 'Brazil', 'f_brazil', '브라질리아', '남아메리카', '포르투갈어', '헤알', '천주교' union all select '가나', 'Ghana', 'f_ghana', '아크라', '아프리카', '영어', '세디', '기독교' " +
//                    "union all select '호주', 'Australia', 'f_australia', '캔버라', '오세아니아', '영어', '호주 달러', '기독교';";
//            db.execSQL(sql);
//        }
//    }
//
//    public boolean isData() {
//        boolean isData = false;
//        String sql = "select * from " + TABLE_NAME + ";";
//        Cursor result = db.rawQuery(sql, null);
//        // result(Cursor)객체가 비어있으면 false 리턴
//        if (result.moveToFirst()) {
//            isData = true;
//        }
//        result.close();
//        return isData;
//    }
//
//    public ArrayList<Country> getInitList(List<Integer> addList) {
//        ArrayList<Country> list = new ArrayList<Country>();
//
//        if (addList != null && addList.size() > 0) {
//
//            String sql = "select * from " + TABLE_NAME + " WHERE ";
//
//            for (int i = 0; i < addList.size(); i++) {
//
//                if (i == (addList.size() - 1)) {
//                    sql += "IDX=" + addList.get(i) + ";";
//                } else {
//                    sql += "IDX=" + addList.get(i) + " OR ";
//                }
//            }
//
//            Log.d("Trav.FavoriteCountry.", "getInitList: " + sql);
//
//            Cursor cursor = db.rawQuery(sql, null);
//            Log.d("C_model", "총 리스트 수 : " + cursor.getCount());
//            cursor.moveToFirst();
//            for (int i = 0; i < cursor.getCount(); i++) {
//                Country Country = new Country(cursor.getString(1), cursor.getString(2), cursor.getString(3),
//                        cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
//                list.add(Country);
//                Log.d("C_model", i + "번 쨰 Country { 국가명 : " + Country.getFc_country() + " / 국가 영문명 : " + Country.getFc_country_eng()
//                        + " / 국기 : " + Country.getFc_flag() + " / 수도 : " + Country.getFc_capital() + " / 대륙 : " + Country.getFc_continent()
//                        + " / 언어 : " + Country.getFc_language() + " / 통화 : " + Country.getFc_currency() + " / 종교 : " + Country.getFc_religion() + " }");
//                cursor.moveToNext();
//            }
//
//            cursor.close();
//        }
//
//        return list;
//    }
//
//    public ArrayList<Country> getList() {
//        ArrayList<Country> list = new ArrayList<Country>();
//        String sql = "select * from " + TABLE_NAME + ";";
//        Cursor cursor = db.rawQuery(sql, null);
//        Log.d("C_model", "총 리스트 수 : " + cursor.getCount());
//        cursor.moveToFirst();
//        for (int i = 0; i < cursor.getCount(); i++) {
//            Country Country = new Country(cursor.getString(1), cursor.getString(2), cursor.getString(3),
//                    cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
//            list.add(Country);
//            Log.d("C_model", i + "번 쨰 Country { 국가명 : " + Country.getFc_country() + " / 국가 영문명 : " + Country.getFc_country_eng()
//                    + " / 국기 : " + Country.getFc_flag() + " / 수도 : " + Country.getFc_capital() + " / 대륙 : " + Country.getFc_continent()
//                    + " / 언어 : " + Country.getFc_language() + " / 통화 : " + Country.getFc_currency() + " / 종교 : " + Country.getFc_religion() + " }");
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return list;
//    }
}
