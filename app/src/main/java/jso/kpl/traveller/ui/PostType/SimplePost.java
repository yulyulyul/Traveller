package jso.kpl.traveller.ui.PostType;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.SimplePostBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.ui.DetailPost;

public class SimplePost extends LinearLayout {

    public SimplePostBinding binding;

    public SimplePost(final Context context, final ListItem item) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        binding = DataBindingUtil.inflate(inflater, R.layout.simple_post, this, true);

        binding.setItem(item);
    }
}
