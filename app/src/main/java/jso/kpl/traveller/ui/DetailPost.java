package jso.kpl.traveller.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.DetailPostBinding;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.ui.Fragment.MyPage;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.viewmodel.DetailPostViewModel;

public class DetailPost extends AppCompatActivity {

    String TAG = "Trav.RodView.";

    private DetailPostBinding binding;
    private DetailPostViewModel detailPostVm;

    int pId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detailPostVm = new DetailPostViewModel();

        detailPostVm.setPostImgAdapter(new ImageSideVpAdapter(getSupportFragmentManager(), 3));

        binding = DataBindingUtil.setContentView(this, R.layout.detail_post);

        if (getIntent() != null) {
            pId = getIntent().getIntExtra("p_id", 0);
        }

        binding.setRodVm(detailPostVm);
        binding.setLifecycleOwner(this);

        binding.getRodVm().setRouteNodeAdapter(new RouteNodeAdapter(this, binding.routeRel));

        binding.getRodVm().postCall(pId);
        // 포스트의 노드를 클릭 이벤트
        binding.getRodVm().getRouteNodeAdapter().setOnNodeClickListener(new RouteNodeAdapter.OnNodeClickListener() {

            @Override
            public void onNodeClicked(SmallPost sp, int pos) {
                // binding.getRodVm().smallPostCall(sp).enqueue(callback);
                Bundle bundle = new Bundle();
                bundle.putInt("sp_id", sp.getSp_id());

                SmallPostDialog spDialog = new SmallPostDialog();
                spDialog.setArguments(bundle);

                spDialog.show((DetailPost.this).getSupportFragmentManager(), "Dialog_TAG");
            }

        });

        binding.topActionBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {

        Log.d(TAG, "포스트 종료");
        setResult(RESULT_OK);
        binding.getRodVm().onCleared();
        finish();
    }
}
