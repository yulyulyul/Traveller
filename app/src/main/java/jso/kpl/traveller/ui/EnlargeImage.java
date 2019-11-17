package jso.kpl.traveller.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.EnlargeImageBinding;
import jso.kpl.traveller.viewmodel.EnlargeImageViewModel;

public class EnlargeImage extends AppCompatActivity {

    EnlargeImageViewModel enlargeImageVm = new EnlargeImageViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EnlargeImageBinding binding = DataBindingUtil.setContentView(this, R.layout.enlarge_image);
        binding.setEnlargeVm(enlargeImageVm);
        binding.setLifecycleOwner(this);

    }
}
