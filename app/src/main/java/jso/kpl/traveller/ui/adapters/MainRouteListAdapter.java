package jso.kpl.traveller.ui.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import jso.kpl.traveller.ui.Fragment.ImageRouteList;
import jso.kpl.traveller.ui.Fragment.PostRouteList;


public class MainRouteListAdapter extends FragmentStatePagerAdapter {

    String TAG = "Trav.MainRouteListAdapter .";
    int numOfPage;

    public MainRouteListAdapter(@NonNull FragmentManager fm, int numOfPage) {
        super(fm, numOfPage);
        this.numOfPage = numOfPage;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ImageRouteList imageRouteList = new ImageRouteList();

                Log.d(TAG + "1st", "ImageRouteList");
                return imageRouteList;
            case 1:
                PostRouteList postRouteList = new PostRouteList();

                Log.d(TAG + "2nd", "PostRouteList");
                return postRouteList;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numOfPage;
    }
}
