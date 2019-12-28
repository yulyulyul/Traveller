package jso.kpl.traveller.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.AddOptionViewBinding;
import jso.kpl.traveller.viewmodel.AddOptionViewViewModel;

public class AddOptionView extends AppCompatActivity {

    AddOptionViewViewModel addOptionViewVm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addOptionViewVm = new AddOptionViewViewModel();

        AddOptionViewBinding binding = DataBindingUtil.setContentView(this, R.layout.add_option_view);

        binding.setAddOptionViewVm(addOptionViewVm);

        binding.setLifecycleOwner(this);

        binding.getAddOptionViewVm().onBackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Trav.TEST", "onClick: ");
                onBackPressed();
            }
        };

        binding.getAddOptionViewVm().ADD_VIEW.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if(integer == 1 || integer == -1){

                    Intent intent = new Intent();
                    intent.putExtra("add_view", integer);

                    setResult(RESULT_OK, intent);

                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(0, 0);
    }
}
