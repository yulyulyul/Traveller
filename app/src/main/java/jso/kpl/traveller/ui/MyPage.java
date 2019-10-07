package jso.kpl.traveller.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageBinding;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class MyPage extends AppCompatActivity {

    String TAG = "TAG,MyPage.";

    MyPageViewModel myPageVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPageVM = new MyPageViewModel(this);

        MyPageBinding pageBinding = DataBindingUtil.setContentView(this, R.layout.my_page);
        pageBinding.setMyPageVm(myPageVM);
        pageBinding.setLifecycleOwner(this);
        pageBinding.executePendingBindings();

        pageBinding.getMyPageVm().init();
    }
}
