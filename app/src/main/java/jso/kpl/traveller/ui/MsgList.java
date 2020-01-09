package jso.kpl.traveller.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MsgListBinding;
import jso.kpl.traveller.viewmodel.MsgListViewModel;

public class MsgList extends AppCompatActivity {

    MsgListBinding binding;
    MsgListViewModel msgListVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        msgListVm = new MsgListViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.msg_list);
        binding.setMsgListVm(msgListVm);
        binding.setLifecycleOwner(this);
    }
}
