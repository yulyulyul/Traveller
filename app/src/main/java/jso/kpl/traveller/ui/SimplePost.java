package jso.kpl.traveller.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPagePostBinding;
import jso.kpl.traveller.model.ListItem;

public class SimplePost extends LinearLayout {

    MyPagePostBinding binding;

    public SimplePost(final Context context, final ListItem item) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        binding = DataBindingUtil.inflate(inflater, R.layout.my_page_post, this, true);

        binding.setItem(item);

        binding.setLifecycleOwner(new MyPage());

        binding.getItem().onPostClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Trav.Item", "Post: " + binding.getItem().toString());

                Intent intent = new Intent(context, RouteOtherDetail.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("p_id", item.getP_id());
                context.startActivity(intent);
            }
        };
    }

}
