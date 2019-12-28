package jso.kpl.traveller.viewmodel;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.network.UserAPI;
import jso.kpl.traveller.network.WebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOptionViewViewModel extends ViewModel {

    UserAPI userAPI;

    public View.OnClickListener onBackListener;

    public MutableLiveData<Integer> ADD_VIEW = new MutableLiveData<>();
    public MutableLiveData<Boolean> isCart = new MutableLiveData<>();

    public AddOptionViewViewModel() {
        userAPI = WebService.INSTANCE.getClient().create(UserAPI.class);

        if(App.Companion.getUser().getU_is_cart() == 1)
            isCart.setValue(true);
        else
            isCart.setValue(false);
    }

    public void onAddClicked(){

        userAPI.updateCartListView(App.Companion.getUser().getU_userid(), App.Companion.getUser().getU_is_cart())
                .enqueue(new Callback<ResponseResult<Integer>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                        if(response.body() != null){

                            if(response.body().getRes_type() == 1){

                                if(App.Companion.getUser().getU_is_cart() == 1) {
                                    ADD_VIEW.setValue(-1);
                                    App.Companion.getUser().setU_is_cart(0);
                                } else {
                                    ADD_VIEW.setValue(1);
                                    App.Companion.getUser().setU_is_cart(1);
                                }

                            } else {
                                App.Companion.sendToast("카트 리스트 추가에 실패하셨습니다.");
                            }

                        } else {
                            App.Companion.sendToast("카트 리스트 추가에 실패하셨습니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                        App.Companion.sendToast("카트 리스트 추가에 실패하셨습니다.");
                        Log.e("Trav.AddView", "onFailure: ", t);
                    }
                });



    }
}
