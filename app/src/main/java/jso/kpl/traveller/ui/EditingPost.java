package jso.kpl.traveller.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.yongbeom.aircalendar.AirCalendarDatePickerActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.EditingPostBinding;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.model.Timeline;
import jso.kpl.traveller.network.PostAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.Fragment.WritePostType;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.viewmodel.EditingPostViewModel;
import me.jerryhanks.timelineview.IndicatorAdapter;
import me.jerryhanks.timelineview.model.Status;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditingPost extends AppCompatActivity implements WritePostType.OnDetachFragmentClickListener, RouteNodeAdapter.OnNodeClickListener, RouteNodeAdapter.OnNodeLongClickListener {


    String TAG = "Trav.EditingPost.";

    List<SmallPost> smallList = new ArrayList<>();

    EditingPostViewModel editingPostVm;
    EditingPostBinding binding;

    Post post;

    final int SELECT_POST_PERIOD = 33;

    int pos;
    boolean isClick = false;

    String test = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //EditingPost의 ViewModel
        editingPostVm = new EditingPostViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.editing_post);

        binding.setEditingPostVm(editingPostVm);
        binding.setLifecycleOwner(this);

        binding.getEditingPostVm().fm.setValue(getSupportFragmentManager());

        binding.getEditingPostVm().setRouteNodeAdapter(new RouteNodeAdapter(this, binding.routeNodeLayout));
        binding.getEditingPostVm().routeNodeAdapter.setOnNodeClickListener(this);
        binding.getEditingPostVm().routeNodeAdapter.setOnNodeLongClickListener(this);

        //Editing Post 뒤로 가기 버튼 클릭 이벤트
        binding.getEditingPostVm().onBackClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };

        //Editing Post 저장 버튼 클릭 이벤트
        binding.getEditingPostVm().onSaveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                메인 포스트에서 작성 데이터
                1. 국가 - 필수
                2. 기간 - 필수
                3. 비용(Small Post 비용 총합) - if node.cnt 0이면 null, cnt > 0이면 0원
                4. 내용
                5. 스몰 포스트
                    1) 장소 - 필수
                    2) 경비 - 안쓰면 0원
                    3) 내용
                    4) 포토
                    5) 기간
                    6) 태그
                 */

                if (JavaUtil.checkBlankString(binding.getEditingPostVm().inputPlace.getValue()) &&
                        JavaUtil.checkBlankString(binding.getEditingPostVm().travelPeriod.getValue()[0]) &&
                        binding.getEditingPostVm().routeNodeAdapter.getItemSize() > 0) {

                    post = new Post(App.Companion.getUser().getU_nick_name(),
                            binding.getEditingPostVm().inputPlace.getValue(),
                            binding.getEditingPostVm().travelPeriod.getValue()[0],
                            binding.getEditingPostVm().travelPeriod.getValue()[1],
                            binding.getEditingPostVm().isOpen.getValue());

                    if (binding.getEditingPostVm().inputExpenses.getValue() != null) {

                        if (binding.getEditingPostVm().routeNodeAdapter.getItemSize() > 0) {
                            int total_expense = 0;

                            for (int i = 0; i < binding.getEditingPostVm().routeNodeAdapter.getItemSize(); i++) {

                                if (binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses().contains("₩"))
                                    total_expense += Integer.parseInt(binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses().replace("₩", "").replace(",", ""));
                                else
                                    total_expense += Integer.parseInt(binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses());
                            }

                            post.setP_expenses(total_expense + "");
                        }
                    } else {
                        post.setP_expenses(0 + "");
                    }

                    if (binding.getEditingPostVm().inputComment.getValue() != null)
                        post.setP_comment(binding.getEditingPostVm().inputComment.getValue());

                    if (binding.getEditingPostVm().travelPeriod.getValue() != null) {
                        post.setP_start_date(binding.getEditingPostVm().travelPeriod.getValue()[0]);
                        post.setP_end_date(binding.getEditingPostVm().travelPeriod.getValue()[1]);
                    }

                    List<SmallPost> spList = binding.getEditingPostVm().routeNodeAdapter.returnList();

                    for (int i = 0; i < spList.size(); i++) {
                        if (spList.get(i).getSp_expenses() != null && spList.get(i).getSp_expenses().contains("₩")) {

                            String removeS = spList.get(i).getSp_expenses().replace("₩", "").replace(",", "");

                            spList.get(i).setSp_expenses(removeS);
                        }
                    }

                    post.setP_sp_list(spList);

                    PostAPI postAPI = WebService.INSTANCE.getClient().create(PostAPI.class);

                    ArrayList<MultipartBody.Part> imgs = new ArrayList<>();

                    for (int i = 0; i < post.getP_sp_list().size(); i++) {

                        if (post.getP_sp_list().get(i).getSp_imgs() != null) {
                            for (int j = 0; j < post.getP_sp_list().get(i).getSp_imgs().size(); j++) {

                                File f = new File(Uri.parse(post.getP_sp_list().get(i).getSp_imgs().get(j)).getPath());

                                int pos = f.getName().lastIndexOf(".");
                                String ext = f.getName().substring(pos + 1);

                                test = ext;

                                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
                                MultipartBody.Part imgBody = MultipartBody.Part.createFormData("sp_img", i + "_" + j + "." + ext, requestFile);

                                imgs.add(imgBody);
                            }
                        } else {
                            imgs.add(null);
                        }

                    }

                    binding.postSave.setClickable(false);

                    Call<ResponseResult<Integer>> call = postAPI.editingPost(post, imgs);

                    call.enqueue(new Callback<ResponseResult<Integer>>() {
                        @Override
                        public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                            Toast.makeText(App.INSTANCE, "성공적으로 포스트 등록했습니다.", Toast.LENGTH_LONG).show();

                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            t.printStackTrace();
                            binding.postSave.setClickable(true);

                        }
                    });

                } else {
                    if (binding.getEditingPostVm().inputPlace.getValue() == null || binding.getEditingPostVm().inputPlace.getValue().equals("")) {
                        Toast.makeText(App.INSTANCE, "국가를 기입해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (binding.getEditingPostVm().routeNodeAdapter.getItemSize() == 0) {
                        Toast.makeText(App.INSTANCE, "노드를 최소 한개 이상 작성해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        binding.getEditingPostVm().onCalendarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(binding.getEditingPostVm().onCalendar(binding.getEditingPostVm().travelPeriod.getValue()), SELECT_POST_PERIOD);
            }
        };

        // Post 저장 버튼 활성화-----------------------------------------------------------------
        binding.getEditingPostVm().inputPlace.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d(TAG, "onChanged: " + s);
                Log.d(TAG, "onChanged: " + JavaUtil.checkBlankString(s));
                Log.d(TAG, "onChanged: " + JavaUtil.checkBlankString(binding.getEditingPostVm().travelPeriod.getValue()[0]));

                if (JavaUtil.checkBlankString(s) && JavaUtil.checkBlankString(binding.getEditingPostVm().travelPeriod.getValue()[0]) && binding.getEditingPostVm().routeNodeAdapter.getItemSize() > 0) {
                    binding.postSave.setTextColor(getColor(R.color.clicked));
                } else {
                    binding.postSave.setTextColor(getColor(R.color.non_clicked));
                }
            }
        });

        binding.getEditingPostVm().travelResult.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (JavaUtil.checkBlankString(s) && JavaUtil.checkBlankString(binding.getEditingPostVm().inputPlace.getValue()) && binding.getEditingPostVm().routeNodeAdapter.getItemSize() > 0) {
                    binding.postSave.setTextColor(getColor(R.color.clicked));
                } else {
                    binding.postSave.setTextColor(getColor(R.color.non_clicked));
                }

            }
        });

        binding.getEditingPostVm().fragment.observe(this, new Observer<Fragment>() {
            @Override
            public void onChanged(Fragment fragment) {
                if (fragment != null) {
                    Log.d(TAG, "Small Post 작성 중...");
                    binding.getEditingPostVm().isSmallPost.setValue(false);
                } else {
                    binding.getEditingPostVm().isSmallPost.setValue(true);
                    if (JavaUtil.checkBlankString(binding.getEditingPostVm().inputPlace.getValue()) && JavaUtil.checkBlankString(binding.getEditingPostVm().travelPeriod.getValue()[0])) {
                        binding.postSave.setTextColor(getColor(R.color.clicked));
                    } else {
                        binding.postSave.setTextColor(getColor(R.color.non_clicked));
                    }
                    Log.d(TAG, "Post 작성 중...");
                }
            }
        });

        binding.getEditingPostVm().setOnCartlistClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DrawerLayout drawer = binding.drawer;
                        drawer.closeDrawer(Gravity.LEFT);
                    }
                }
        );

        /*binding.getEditingPostVm().isCartlist.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean bool) {
                if (bool) {
                    System.out.println("여긴뭐...");
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.content.getLayoutParams();
                    layoutParams.topMargin = binding.getEditingPostVm().getItemHeight();
                    layoutParams.leftMargin = binding.getEditingPostVm().getItemWidth();
                    binding.content.setLayoutParams(layoutParams);
                } else {

                }
            }
        });*/

        binding.getEditingPostVm().timelineAdapter.observe(this, new Observer<IndicatorAdapter<Timeline>>() {
            @Override
            public void onChanged(IndicatorAdapter<Timeline> adapter) {
                if (adapter.getItem(0).getStatus() != Status.ATTENTION) {
                    adapter.getItemViewWidth().observe(EditingPost.this, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer i) {
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.content.getLayoutParams();
                            layoutParams.topMargin = binding.getEditingPostVm().timelineAdapter.getValue().getItemViewHeight().getValue();
                            layoutParams.leftMargin = binding.getEditingPostVm().timelineAdapter.getValue().getItemViewWidth().getValue();
                            binding.content.setLayoutParams(layoutParams);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == SELECT_POST_PERIOD) {
            if (data != null) {

                String startDate = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE);
                String endDate = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE);

                if (binding.getEditingPostVm().travelPeriod.getValue() != null) {
                    Log.d(TAG, "onActivityResult: Not Null");

                    if (!startDate.equals("") && !startDate.equals(binding.getEditingPostVm().travelPeriod.getValue()[0])) {
                        binding.getEditingPostVm().travelPeriod.getValue()[0] = startDate;

                        binding.getEditingPostVm().travelResult.setValue(startDate + " ~ " + endDate);

                    }

                    if (!endDate.equals("") && !endDate.equals(binding.getEditingPostVm().travelPeriod.getValue()[1])) {
                        binding.getEditingPostVm().travelPeriod.getValue()[1] = endDate;

                        binding.getEditingPostVm().travelResult.setValue(startDate + " ~ " + endDate);

                    }
                } else {
                    binding.getEditingPostVm().travelPeriod.setValue(new String[]{startDate, endDate});
                    binding.getEditingPostVm().travelResult.setValue(startDate + " ~ " + endDate);
                }
            }
        }
    }

    @Override
    public void onDetachFragmentClicked() {

        binding.getEditingPostVm().fm.getValue().beginTransaction()
                .setCustomAnimations(R.anim.enter_to_top, R.anim.exit_to_down)
                .remove(binding.getEditingPostVm().fragment.getValue())
                .detach(binding.getEditingPostVm().fragment.getValue())
                .addToBackStack(null).commit();

        binding.getEditingPostVm().fragment.setValue(null);


        if (binding.getEditingPostVm().routeNodeAdapter.getItemSize() > 0) {

            long total_expense = 0;

            for (int i = 0; i < binding.getEditingPostVm().routeNodeAdapter.getItemSize(); i++) {

                if (binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses().contains("₩"))
                    total_expense += Long.parseLong(binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses().replace("₩", "").replace(",", ""));
                else
                    total_expense += Long.parseLong(binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses());
            }

            binding.getEditingPostVm().inputExpenses.setValue(CurrencyChange.moneyFormatToWon(total_expense));
        } else {
            binding.getEditingPostVm().inputExpenses.setValue(null);
        }


    }

    //스몰 포스트 임시 저장
    // -> 메인 포스트 상태로 돌아오고 상단에 노드가 추가된다.
    @Override
    public void onSaveSmallPostClicked(SmallPost sp) {
        smallList.add(sp);

        if (isClick) {
            binding.getEditingPostVm().routeNodeAdapter.updateNode(sp, pos);
            isClick = false;
        } else {
            binding.getEditingPostVm().routeNodeAdapter.putItem(sp);
        }

        binding.getEditingPostVm().isClick.setValue(false);
    }

    //상단 노드를 클릭하면 해당 노드를 획인 및 수정할 수 있음
    @Override
    public void onNodeClicked(@NotNull SmallPost sp, int pos) {
        Log.d(TAG, "onNodeClicked: " + sp.toString());

        if (binding.getEditingPostVm().fragment.getValue() != null) {
            onDetachFragmentClicked();
        }

        binding.getEditingPostVm().fragment.setValue(new WritePostType().newInstance(sp, pos));

        isClick = true;
        this.pos = pos;
    }

    //상단 노드 삭제할 때
    @Override
    public void onNodeLongClicked() {

        if (binding.getEditingPostVm().routeNodeAdapter.getItemSize() == 0) {
            binding.getEditingPostVm().isClick.setValue(true);
            binding.postSave.setTextColor(getColor(R.color.non_clicked));
        }
        if (binding.getEditingPostVm().routeNodeAdapter.getItemSize() > 0) {
            //노드를 삭제했을 때 메인 포스트의 경비 계산을 대신 한다.
            long total_expense = 0;

            for (int i = 0; i < binding.getEditingPostVm().routeNodeAdapter.getItemSize(); i++) {

                if (binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses().contains("₩"))
                    total_expense += Long.parseLong(binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses().replace("₩", "").replace(",", ""));
                else
                    total_expense += Long.parseLong(binding.getEditingPostVm().routeNodeAdapter.returnList().get(i).getSp_expenses());
            }

            binding.getEditingPostVm().inputExpenses.setValue(CurrencyChange.moneyFormatToWon(total_expense));
        } else {
            binding.getEditingPostVm().inputExpenses.setValue(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.getEditingPostVm().fragment.getValue() != null) {
            onDetachFragmentClicked();
        } else if (binding.getEditingPostVm().isCartlist.getValue()) {
            binding.getEditingPostVm().isCartlist.setValue(false);
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage("저장하지 않고 나가시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                //setResult(EDITING_POST);
                                setResult(RESULT_CANCELED);
                                finish();
                            } catch (Exception e) {
                                //setResult(EDITING_POST);
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        }
                    }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }
}
