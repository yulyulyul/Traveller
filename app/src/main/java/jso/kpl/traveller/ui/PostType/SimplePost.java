package jso.kpl.traveller.ui.PostType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.SimplePostBinding;
import jso.kpl.traveller.interfaces.PostInterface;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.ui.Fragment.MyPage;
import jso.kpl.traveller.ui.DetailPost;

public class SimplePost extends LinearLayout {

    SimplePostBinding binding;

    LifecycleOwner lifecycleOwner;

    public SimplePost(final Activity activity, final ListItem item) {
        super(activity);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);

        binding = DataBindingUtil.inflate(inflater, R.layout.simple_post, this, true);

        binding.setItem(item);

//        lifecycleOwner = (LifecycleOwner) activity;
//        binding.setLifecycleOwner(lifecycleOwner);

        binding.getItem().onPostClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Trav.Item", "Post: " + binding.getItem().toString());

                Intent intent = new Intent(activity, DetailPost.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("p_id", item.getP_id());

                activity.startActivityForResult(intent, 88);
            }
        };
    }
}
