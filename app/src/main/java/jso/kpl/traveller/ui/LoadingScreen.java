package jso.kpl.traveller.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.LoadingScreenBinding;

public class LoadingScreen extends AppCompatActivity {

    LoadingScreenBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadingScreenBinding binding
                = DataBindingUtil.setContentView(this, R.layout.loading_screen);

        binding.setLifecycleOwner(this);

        if (binding.loader.getAnimation() == null) {
            Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotation);
            binding.loader.startAnimation(rotateAnimation);
            binding.loader.setAnimation(rotateAnimation);
        }

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }
}
