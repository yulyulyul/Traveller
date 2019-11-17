package jso.kpl.traveller.ui.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.yongbeom.aircalendar.AirCalendarDatePickerActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener;
import jso.kpl.traveller.App;
import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.WritePostTypeBinding;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.viewmodel.EditingPostViewModel;

public class WritePostType extends Fragment {

    String TAG = "Trav.WritePostType.";

    private OnDetachFragmentClickListener onDetachFragmentClickListener;

    public interface OnDetachFragmentClickListener {
        void onDetachFragmentClicked();

        void onSaveSmallPostClicked(SmallPost sp);
    }

    public void setOnDetachFragmentClickListener(OnDetachFragmentClickListener onDetachFragmentClickListener) {
        this.onDetachFragmentClickListener = onDetachFragmentClickListener;
    }

    List<ImageView> ivList;
    List<HashMap<ImageView, Uri>> ivHmList;

    List<TextView> tagTvList = new ArrayList<>();

    public WritePostTypeBinding binding;
    EditingPostViewModel editingPostVm;

    public Fragment newInstance(SmallPost sp, int pos) {

        WritePostType writePostType = new WritePostType();
        Bundle args = new Bundle();
        args.putSerializable("smallpost", sp);
        args.putInt("position", pos);

        writePostType.setArguments(args);

        return writePostType;
    }

    public WritePostType() {
        // Required empty public constructor

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof OnDetachFragmentClickListener) {
            onDetachFragmentClickListener = (OnDetachFragmentClickListener) getActivity();
        }
    }

    SmallPost sp;
    int pos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            sp = (SmallPost) getArguments().getSerializable("smallpost");
            pos = getArguments().getInt("position");
        }
    }

    public void init() {
        if (sp != null) {

            binding.getPostTypeVm().inputPlace.setValue(sp.getSp_place());
            binding.getPostTypeVm().inputExpenses.setValue(sp.getSp_expenses());

            if (sp.getSp_comment() != null)
                binding.getPostTypeVm().inputComment.setValue(sp.getSp_comment());

            if (sp.getSp_imgs() != null) {

                List<Uri> strToUri = new ArrayList<>();

                for (String str : sp.getSp_imgs()) {
                    strToUri.add(Uri.parse(str));
                }

                binding.getPostTypeVm().photoList.setValue(strToUri);
                binding.getPostTypeVm().isPhoto.setValue(true);
                addPhotoLayout(binding.photoLayout, binding.getPostTypeVm().photoList.getValue());
            }

            if (sp.getSp_category() != null) {
                binding.getPostTypeVm().tagList.setValue(sp.getSp_category());
                binding.getPostTypeVm().isTag.setValue(true);

                addCategoryLayout(sp.getSp_category());
            }

            if (sp.getSp_start_date() != null) {
                binding.getPostTypeVm().isCalendar.setValue(true);
                binding.getPostTypeVm().travelPeriod.setValue(new String[]{sp.getSp_start_date(), sp.getSp_end_date(), sp.getSp_start_date() + " ~ " + sp.getSp_end_date()});
            }

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        editingPostVm = new EditingPostViewModel();

        binding = DataBindingUtil.inflate(inflater, R.layout.write_post_type, container, false);
        binding.setPostTypeVm(editingPostVm);
        binding.setLifecycleOwner(this);

        binding.getPostTypeVm().fm.setValue(getActivity().getSupportFragmentManager());
        init();

        //Small Post 저장 버튼 활성화-----------------------------------------------------------------
        binding.getPostTypeVm().inputPlace.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.length() > 0)
                    binding.save.setTextColor(getActivity().getColor(R.color.clicked));
                else
                    binding.save.setTextColor(getActivity().getColor(R.color.non_clicked));

            }
        });

        //------------------------------------------------------------------------------------------

        binding.getPostTypeVm().setOnCalendarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(binding.getPostTypeVm().onCalendar(binding.getPostTypeVm().travelPeriod.getValue()), 33);
            }
        });

        binding.addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.getPostTypeVm().photoList.getValue() != null) {

                    showGallery();
                }
            }
        });

        binding.getPostTypeVm().onDetachFragmentClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetachFragmentClickListener.onDetachFragmentClicked();
            }
        };

        // 각 추가 뷰를 눌렀을 때 이벤트---------------------------------------------------------------
        binding.getPostTypeVm().isPhoto.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showGallery();
                }
            }
        });

        binding.getPostTypeVm().isCalendar.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    startActivityForResult(binding.getPostTypeVm().onCalendar(binding.getPostTypeVm().travelPeriod.getValue()), 33);
                }
            }
        });

        //카테고리 추가
        binding.getPostTypeVm().isTag.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showCategoryList();
                }
            }
        });

        //------------------------------------------------------------------------------------------

        binding.getPostTypeVm().onAddCategoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryList();
            }
        };
        //------------------------------------------------------------------------------------------
        binding.getPostTypeVm().onSaveSpClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.save.requestFocus();

                if (binding.getPostTypeVm().inputPlace.getValue() != null && !binding.getPostTypeVm().inputPlace.getValue().equals("")) {

                    onDetachFragmentClickListener.onSaveSmallPostClicked(binding.getPostTypeVm().saveSmallPostData());
                    onDetachFragmentClickListener.onDetachFragmentClicked();

                } else {
                    Toast.makeText(App.INSTANCE, "장소는 필수적으로 기입해주셔야합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        };

        return binding.getRoot();
    }

    // 갤러리에서 이미지 가져오기 ---------------------------------------------------------------------
    public void showGallery(){
        TedImagePicker.with(getActivity())
                .showCameraTile(false)
                .max(3 - binding.getPostTypeVm().photoList.getValue().size(), (3 - binding.getPostTypeVm().photoList.getValue().size()) + "장의 사진을 선택가능합니다.")
                .startMultiImage(new OnMultiSelectedListener() {
                    @Override
                    public void onSelected(@NotNull List<? extends Uri> uriList) {

                        List<Uri> uris = (List<Uri>) uriList;

                        if(binding.getPostTypeVm().photoList.getValue() != null)
                            binding.getPostTypeVm().photoList.getValue().addAll(uris);
                        else
                            binding.getPostTypeVm().photoList.setValue(uris);

                        addPhotoLayout(binding.photoLayout, binding.getPostTypeVm().photoList.getValue());
                    }
                });
    }

    //포토 이미지 레이아웃 추가 함수
    public void addPhotoLayout(final LinearLayout linearLayout, final List<Uri> uriList) {
        //선택한 이미지를 감쌀 리니어 레이아웃 생성

        if (ivList != null) {
            for (ImageView iv : ivList) {
                linearLayout.removeView(iv);
            }
        }

        ivList = new ArrayList<>();
        ivHmList = new ArrayList<>();

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(metrics.widthPixels / 5, metrics.heightPixels / 7);
        params.rightMargin = 10;

        //선택한 이미지의 수에 따라 이미지 뷰 생성
        for (int i = 0; i < uriList.size(); i++) {

            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(params);

            imageView.setImageURI(uriList.get(i));

            //포토 뷰를 넣어주는 곳
            linearLayout.addView(imageView, ivList.size());

            //이미지 뷰만 가지고 있는 리스트
            ivList.add(imageView);


            HashMap<ImageView, Uri> map = new HashMap<>();
            map.put(imageView, uriList.get(i));

            //이미지뷰 + Uri
            ivHmList.add(map);

        }

        for (final ImageView iv : ivList) {

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    linearLayout.removeView(iv);

                    HashMap<ImageView, Uri> remove = new HashMap<>();

                    for (HashMap<ImageView, Uri> hm : ivHmList) {

                        ivList.remove(iv);
                        remove = hm;

                        binding.getPostTypeVm().photoList.getValue().remove(hm.get(iv));
                    }

                    ivHmList.remove(remove);

                    Log.d(TAG, "ivList Size: " + ivList.size());
                    Log.d(TAG, "ivHmList Size: " + ivHmList.size());
                    Log.d(TAG, "binding Size: " + binding.getPostTypeVm().photoList.getValue().size());

                }
            });

        }

    }
    //----------------------------------------------------------------------------------------------

    //카테고리 선택 다이얼로그 출력--------------------------------------------------------------------
    public void showCategoryList() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ArrayList<String> selectedItems = new ArrayList<String>();
        final String[] items = getResources().getStringArray(R.array.category);

        builder.setTitle("Select Category");

        boolean[] checkedList = new boolean[items.length];

        if (binding.getPostTypeVm().tagList.getValue() != null) {
            for (int i = 0; i < items.length; i++) {
                for (int j = 0; j < binding.getPostTypeVm().tagList.getValue().size(); j++) {

                    if (items[i].equals(binding.getPostTypeVm().tagList.getValue().get(j))) {
                        checkedList[i] = true;
                        selectedItems.add(items[i]);
                    }
                }
            }
        }

        builder.setMultiChoiceItems(R.array.category, checkedList, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos, boolean isChecked) {
                if (isChecked == true) // Checked 상태일 때 추가
                {
                    selectedItems.add(items[pos]);
                } else                  // Check 해제 되었을 때 제거
                {
                    selectedItems.remove(items[pos]);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {

                if (binding.categoryLayout.getChildCount() > 0) {
                    binding.categoryLayout.removeAllViews();
                }

                binding.getPostTypeVm().tagList.setValue(selectedItems);
                addCategoryLayout(binding.getPostTypeVm().tagList.getValue());
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //태그를 추가할 때 TextView(태그값)를 동적으로 생성하는 함수
    public TextView addCategoryItem(String tag) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.rightMargin = 5;
        params.leftMargin = 5;

        TextView tv = new TextView(getActivity());
        tv.setLayoutParams(params);
        tv.setPadding(10, 5, 10, 5);
        tv.setBackgroundResource(R.drawable.s_border_round_square);
        tv.setTextColor(getActivity().getColor(R.color.colorWhite));
        tv.setText(tag);
        tv.setTextSize(20);

        //버튼 있는 버전 - 현재 실패
//        LinearLayout categoryItem = new LinearLayout(getActivity());
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        params.rightMargin = 5;
//        params.leftMargin = 5;
//        params.gravity = Gravity.CENTER_VERTICAL;
//
//        categoryItem.setLayoutParams(params);
//        categoryItem.setBackgroundResource(R.drawable.s_border_round_square);
//        categoryItem.setOrientation(LinearLayout.HORIZONTAL);
//        categoryItem.setVerticalGravity(Gravity.CENTER_VERTICAL);
//
//        TextView tv = new TextView(getActivity());
//        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        tv.setPadding(5, 5, 5, 5);
//        tv.setTextColor(getActivity().getColor(R.color.colorWhite));
//        tv.setText(tag);
//
//        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(15, 15);
//        btnParams.rightMargin = 5;
//        btnParams.leftMargin = 5;
//
//        Button btn = new Button(getActivity());
//        btn.setLayoutParams(btnParams);
//        btn.setBackgroundResource(R.drawable.i_clear_icon);
//
//        HashMap<Button, TextView> map = new HashMap<>();
//        map.put(btn, tv);
//
//        categoryItem.addView(tv);
//        categoryItem.addView(btn);
//
//        tv.setClickable(true);
//        btn.setClickable(true);
//        categoryItem.setClickable(true);
//
//        return categoryItem;

        return tv;
    }

    //카테고리 레이아웃 추가 함수
    public void addCategoryLayout(List<String> list) {

        //선택한 카테고리의 수에 따라 텍스트 뷰 생성
        for (int i = 0; i < list.size(); i++) {

            Log.d(TAG, "전시된 태그: " + list.get(i));

            final TextView tv = addCategoryItem(list.get(i));

            tagTvList.add(tv);
            binding.categoryLayout.addView(tv);


            for (int j = 0; j < tagTvList.size(); j++) {
                tagTvList.get(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.categoryLayout.removeView(v);
                        binding.getPostTypeVm().tagList.getValue().remove(((TextView) v).getText());

                        tagTvList.remove(v);
                    }
                });
            }
        }
    }
   //-----------------------------------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != getActivity().RESULT_OK)
            return;

        if (requestCode == 33) {
            if (data != null) {

                binding.getPostTypeVm().travelPeriod.setValue(new String[]{
                        data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE),
                        data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE),
                        data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE) + " ~ " + data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE)
                });

                if (binding.getPostTypeVm().travelPeriod.getValue()[2].equals("~")) {
                    binding.getPostTypeVm().travelPeriod.getValue()[2] = null;
                } else {
                    Log.d(TAG, "onActivityResult: " + binding.getPostTypeVm().travelPeriod.getValue()[2]);
                }

            }
        }
    }

}
