package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteOtherDetailBinding;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.ui.Fragment.ImageSideItem;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.viewmodel.RouteOtherDetailViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RouteOtherDetail extends AppCompatActivity implements Callback {

    String TAG = "Trav.RodView.";

    private RouteOtherDetailBinding binding;
    private RouteOtherDetailViewModel routeOtherDetailVm;

    int pId;

    Callback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callback = this;
        routeOtherDetailVm = new RouteOtherDetailViewModel();
        routeOtherDetailVm.setImageSideVpAdapter(new ImageSideVpAdapter(getSupportFragmentManager(), 3));

        binding = DataBindingUtil.setContentView(this, R.layout.route_other_detail);

        if(getIntent() != null){
            pId = getIntent().getIntExtra("p_id", 0);
        }



        binding.setRodVm(routeOtherDetailVm);
        binding.setLifecycleOwner(this);

        binding.getRodVm().setRouteNodeAdapter(new RouteNodeAdapter(this, binding.routeRel));

        // 포스트 상세 데이터 통신
        binding.getRodVm().postCall(pId).enqueue(callback);

        // 포스트의 노드를 클릭 이벤트
        binding.getRodVm().getRouteNodeAdapter().setOnNodeClickListener(new RouteNodeAdapter.OnNodeClickListener() {
            @Override
            public void onNodeClicked(SmallPost sp, int pos) {
                binding.getRodVm().smallPostCall(sp).enqueue(callback);
            }

            @Override
            public void onNodeLongClicked() {
                Log.d(TAG, "onNodeLongClicked: ");
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) {
        Log.d(TAG + "통신 성공","성공적으로 전송");

        if(response.body() != null){

            boolean type = ((ResponseResult<List<?>>) response.body()).getRes_obj().get(0) instanceof Post;

            if(type){
                //포스트 상세 내용
                if(((ResponseResult<List<?>>) response.body()).getRes_type() == 1){
                    App.Companion.sendToast("포스트 상세 보기");
                    binding.getRodVm().loadingPost(((ResponseResult<List<Post>>) response.body()).getRes_obj().get(0));
                } else {
                    App.Companion.sendToast("포스트 상세 보기 실패");
                }
            } else{
                // 스몰 포스트 상세 내용
                if(((ResponseResult<List<?>>) response.body()).getRes_type() == 1){
                    App.Companion.sendToast("스몰포스트 상세 보기");
                    binding.getRodVm().loadingSmallPost(((ResponseResult<List<SmallPost>>) response.body()).getRes_obj().get(0));
                } else {
                    App.Companion.sendToast("스몰포스트 상세 보기 실패");
                }
            }
        } else{

        }

    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Toast.makeText(App.INSTANCE, "통신 불량" + t.getMessage(), Toast.LENGTH_SHORT).show();
        Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
        t.printStackTrace();
    }

}
