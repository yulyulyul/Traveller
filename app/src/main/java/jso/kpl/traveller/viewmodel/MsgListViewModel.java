package jso.kpl.traveller.viewmodel;

import androidx.lifecycle.ViewModel;

import jso.kpl.traveller.ui.adapters.MsgListAdapter;

public class MsgListViewModel extends ViewModel {

    public MsgListAdapter adapter = new MsgListAdapter();
}
