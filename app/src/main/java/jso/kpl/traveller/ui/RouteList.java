package jso.kpl.traveller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.MyProfileBinding;
import jso.kpl.traveller.databinding.RouteListBinding;
import jso.kpl.traveller.databinding.SearchResultBinding;
import jso.kpl.traveller.model.ListItem;
import jso.kpl.traveller.model.MyPageItem;
import jso.kpl.traveller.model.SearchReq;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.util.JavaUtil;
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

            addUpperView(item);
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

    public void addUpperView(final MyPageItem item) {

        View addView;

        final int type = item.getType();

        if(type == 0){
            binding.getMainListVm().country.setValue(((SearchReq) item.getO()).getSr_country());
            binding.getMainListVm().minCost.setValue(CurrencyChange.moneyFormatToWon(((SearchReq) item.getO()).getSr_min_cost()));
            binding.getMainListVm().maxCost.setValue(CurrencyChange.moneyFormatToWon(((SearchReq) item.getO()).getSr_max_cost()));

            addView = getLayoutInflater().inflate(R.layout.search_result, binding.addView, false);
            final SearchResultBinding resultBinding = DataBindingUtil.bind(addView);

            resultBinding.setCountry(binding.getMainListVm().country);
            resultBinding.setMinCost(binding.getMainListVm().minCost);
            resultBinding.setMaxCost(binding.getMainListVm().maxCost);
            resultBinding.setIsOpen(binding.getMainListVm().isOpen);

            resultBinding.setLifecycleOwner(RouteList.this);

            resultBinding.setOnOpenClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                    binding.getMainListVm().isOpen.setValue(!binding.getMainListVm().isOpen.getValue());
                }
            });

            resultBinding.setOnReSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String country = binding.getMainListVm().country.getValue();

                    int minCost, maxCost;

                    if (binding.getMainListVm().minCost.getValue().contains("₩"))
                        minCost = Integer.parseInt(binding.getMainListVm().minCost.getValue().replace("₩", "").replace(",", ""));
                    else
                        minCost = Integer.parseInt(binding.getMainListVm().minCost.getValue());

                    if (binding.getMainListVm().maxCost.getValue().contains("₩"))
                        maxCost = Integer.parseInt(binding.getMainListVm().maxCost.getValue().replace("₩", "").replace(",", ""));
                    else
                        maxCost = Integer.parseInt(binding.getMainListVm().maxCost.getValue());

                    Log.d(TAG, "onClick: 재검색: " + country + " 최소: " + minCost + " 최대: " + maxCost);

                    item.setO(new SearchReq(country, minCost, maxCost));

                    binding.getMainListVm().setItem(item);

                    binding.getMainListVm().gridAdapter.removeItems();
                    binding.getMainListVm().verticalAdapter.removeItems();

                    binding.getMainListVm().lastPid = 0;

                    binding.getMainListVm().postList.getValue().clear();

                    binding.getMainListVm().searchByCondition(item);

                    JavaUtil.downKeyboard(RouteList.this);

                    binding.getMainListVm().isOpen.setValue(false);
                }
            });

            binding.addView.addView(addView);

        } else if(type == 2 || type == 3 || type == 4) {
            addView = getLayoutInflater().inflate(R.layout.my_profile, binding.addView, false);
            final MyProfileBinding profileBinding = DataBindingUtil.bind(addView);

            binding.getMainListVm().userInfoCall();

            binding.getMainListVm().cnts.observe(RouteList.this, new Observer<List<Integer>>() {
                @Override
                public void onChanged(List<Integer> integers) {
                    profileBinding.setCnts(integers);
                }
            });

            binding.getMainListVm().profileImg.setValue(App.Companion.getUser().getU_profile_img());

            profileBinding.setImg(binding.getMainListVm().profileImg.getValue());

            profileBinding.setNickName(App.Companion.getUser().getU_nick_name());

            binding.getMainListVm().profileImg.observe(RouteList.this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    profileBinding.setImg(s);
                }
            });

            profileBinding.setOnReviseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RouteList.this, ProfileManagement.class);
                    startActivityForResult(intent, 11);
                }
            });

            binding.addView.addView(addView);
        }
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

        if (requestCode == 11) {
            Log.d(TAG, "업데이트 이미지: " + App.Companion.getUser().getU_profile_img());

            binding.getMainListVm().profileImg.setValue(App.Companion.getUser().getU_profile_img());
        }

        if (requestCode == 44) {
            Log.d(TAG, "디테일 포스트 -> 루트 리스트: ");
            binding.getMainListVm().gridAdapter.removeItems();
            binding.getMainListVm().verticalAdapter.removeItems();

            binding.getMainListVm().lastPid = 0;

            binding.getMainListVm().postList.getValue().clear();

            binding.getMainListVm().searchByCondition(item);

            binding.getMainListVm().userInfoCall();
        }
    }
}
