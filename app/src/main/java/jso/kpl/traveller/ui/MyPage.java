package jso.kpl.traveller.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyPageBinding;
import jso.kpl.traveller.viewmodel.MyPageViewModel;

public class MyPage extends AppCompatActivity {

    String TAG = "Trav.MyPage.";

    //MyPage의 ViewModel
    MyPageViewModel myPageVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPageVM = new MyPageViewModel();

        MyPageBinding pageBinding = DataBindingUtil.setContentView(this, R.layout.my_page);
        pageBinding.setMyPageVm(myPageVM);
        pageBinding.setLifecycleOwner(this);
        pageBinding.executePendingBindings();

        pageBinding.getMyPageVm().setContext(this);

    }
}
