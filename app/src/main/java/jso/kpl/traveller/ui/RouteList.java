package jso.kpl.traveller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.RouteListBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.viewmodel.RouteListViewModel;

public class RouteList extends AppCompatActivity {

    String TAG = "Trav.RouteList.";

    MyPageItem item;

    RouteListBinding binding;
    RouteListViewModel routeListVm = new RouteListViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeListVm.setFm(getSupportFragmentManager());

        binding = DataBindingUtil.setContentView(this, R.layout.route_list);
        binding.setMainListVm(routeListVm);
        binding.setLifecycleOwner(this);

        if (getIntent() != null) {
            item = (MyPageItem) getIntent().getSerializableExtra("req");
            binding.getMainListVm().setItem(item);
            binding.getMainListVm().searchByCondition(item);
        }

        if (((MyPageItem) getIntent().getSerializableExtra("req")).getType() == 0) {
            binding.categoryHorizontalLayout.setVisibility(View.VISIBLE);
            binding.getMainListVm().addCategoryLayout(this, binding.categoryLayout);
        } else {
            binding.categoryHorizontalLayout.setVisibility(View.GONE);
        }

        binding.getMainListVm().onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.d(TAG, "Route List - onRefresh");

                binding.getMainListVm().gridAdapter.removeItems();
                binding.getMainListVm().verticalAdapter.removeItems();

                binding.getMainListVm().lastPid = 0;

                binding.getMainListVm().postList.setValue(new ArrayList<ListItem>());

                binding.getMainListVm().searchByCondition(item);

                binding.getMainListVm().isRefresh.setValue(false);
            }
        };

        binding.getMainListVm().onGridScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (binding.getMainListVm().gt_post.isVisible()) {

                    int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                    if (itemTotalCount > 16) {
                        if (lastVisibleItemPosition == itemTotalCount) {
                            Log.d(TAG, "마지막 그리드: ");
                            binding.getMainListVm().searchByCondition(item);
                        }
                    }
                }
            }
        };

        binding.getMainListVm().onVerticalScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (binding.getMainListVm().vt_post != null && binding.getMainListVm().vt_post.isVisible()) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                    Log.d(TAG, "2. 최상단 보이는 노드의 인덱스: " +
                            ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());

                    if (itemTotalCount > 16) {
                        if (lastVisibleItemPosition == itemTotalCount) {
                            Log.d(TAG, "마지막 버티컬: ");
                            binding.getMainListVm().searchByCondition(item);
                        }
                    }
                }
            }
        };

        binding.getMainListVm().POST_ID.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d(TAG, "루트 리스트 포스트 아이디: " + integer);
                Intent intent = new Intent(App.INSTANCE, DetailPost.class);
                intent.putExtra("p_id", integer);
                startActivityForResult(intent, 44);
            }
        });

        // TopActionBarBinding barBinding = DataBindingUtil.findBinding(binding.topActionBar.textView);
        binding.topActionBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        binding.getMainListVm().onCleared();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 44) {
            Log.d(TAG, "디테일 포스트 -> 루트 리스트: ");
            binding.getMainListVm().gridAdapter.removeItems();
            binding.getMainListVm().verticalAdapter.removeItems();

            binding.getMainListVm().lastPid = 0;

            binding.getMainListVm().postList.getValue().clear();

            binding.getMainListVm().searchByCondition(item);
        }
    }
}
