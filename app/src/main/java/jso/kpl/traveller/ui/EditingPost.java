package jso.kpl.traveller.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

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
import jso.kpl.traveller.network.PostAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.Fragment.WritePostType;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.util.CurrencyChange;
import jso.kpl.traveller.viewmodel.EditingPostViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditingPost extends AppCompatActivity implements WritePostType.OnDetachFragmentClickListener, RouteNodeAdapter.OnNodeClickListener {

    String TAG = "Trav.EditingPost.";

    List<SmallPost> smallList = new ArrayList<>();

    EditingPostViewModel editingPostVm;
    EditingPostBinding binding;

    final int EDITING_POST = 22;
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

                if (binding.getEditingPostVm().inputPlace.getValue() != null &&
                        !binding.getEditingPostVm().inputPlace.getValue().equals("") &&
                        binding.getEditingPostVm().routeNodeAdapter.getItemSize() > 0) {

                    Post post = new Post("test", binding.getEditingPostVm().inputPlace.getValue(), binding.getEditingPostVm().isOpen.getValue());

                    if (binding.getEditingPostVm().inputExpenses.getValue() != null){

                        if(binding.getEditingPostVm().inputExpenses.getValue().contains("₩"))
                            post.setP_expenses(binding.getEditingPostVm().inputExpenses.getValue());
                        else
                            post.setP_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(binding.getEditingPostVm().inputExpenses.getValue())));

                    } else{
                       post.setP_expenses(CurrencyChange.moneyFormatToWon(0));
                    }

                    if (binding.getEditingPostVm().inputComment.getValue() != null)
                        post.setP_comment(binding.getEditingPostVm().inputComment.getValue());

                    if (binding.getEditingPostVm().travelPeriod.getValue() != null) {
                        post.setP_start_date(binding.getEditingPostVm().travelPeriod.getValue()[0]);
                        post.setP_end_date(binding.getEditingPostVm().travelPeriod.getValue()[1]);
                    }

                    post.setP_sp_list(binding.getEditingPostVm().routeNodeAdapter.returnList());

                    PostAPI postAPI = WebService.INSTANCE.getClient().create(PostAPI.class);

                    ArrayList<ArrayList<MultipartBody.Part>> imgList = new ArrayList<>();

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

//                    for (int i = 0; i < post.getP_sp_list().size(); i++) {
//
//                        imgs = new ArrayList<>();
//
//                        for (int j = 0; j < post.getP_sp_list().get(i).getSp_imgs().size(); j++) {
//
//                            File f = new File(Uri.parse(post.getP_sp_list().get(i).getSp_imgs().get(j)).getPath());
//
//                            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
//                            MultipartBody.Part imgBody = MultipartBody.Part.createFormData("sp_img", f.getName(), requestFile);
//
//                            imgs.add(imgBody);
//                        }
//
//                        post.getP_sp_list().get(i).setSp_img_body(imgs);
//                        imgList.add(imgs);
//                    }

                    Call<ResponseResult<Integer>> call = postAPI.editingPost(post, imgs);

                    call.enqueue(new Callback<ResponseResult<Integer>>() {
                        @Override
                        public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            t.printStackTrace();


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

        //Small Post 저장 버튼 활성화-----------------------------------------------------------------
        binding.getEditingPostVm().inputPlace.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s.length() > 0 && binding.getEditingPostVm().routeNodeAdapter.getItemSize() > 0) {
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
                    if (binding.getEditingPostVm().inputPlace.getValue() != null
                            && binding.getEditingPostVm().inputPlace.getValue().length() > 0) {
                        binding.postSave.setTextColor(getColor(R.color.clicked));
                    } else {
                        binding.postSave.setTextColor(getColor(R.color.non_clicked));
                    }
                    Log.d(TAG, "Post 작성 중...");
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
                String[] cals = new String[3];

                cals[0] = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE);
                cals[1] = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE);
                cals[2] = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE) + " ~ " + data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE);

                binding.getEditingPostVm().travelPeriod.setValue(cals);

                Log.d(TAG, "onActivityResult: " + binding.getEditingPostVm().travelPeriod.getValue()[2]);
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
    }

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

    @Override
    public void onNodeLongClicked() {
        if (binding.getEditingPostVm().routeNodeAdapter.getItemSize() == 1) {
            binding.getEditingPostVm().isClick.setValue(true);
            binding.postSave.setTextColor(getColor(R.color.non_clicked));
        }
    }

    @Override
    public void onBackPressed() {

        if(binding.getEditingPostVm().fragment.getValue() != null){
            onDetachFragmentClicked();
        } else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage("저장하지 않고 나가시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                setResult(EDITING_POST);
                                finish();
                            } catch (Exception e) {
                                setResult(EDITING_POST);
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
