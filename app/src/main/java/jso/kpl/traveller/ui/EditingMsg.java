package jso.kpl.traveller.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.EditingMsgBinding;
import jso.kpl.traveller.ui.Fragment.ImageSideItem;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import jso.kpl.traveller.util.JavaUtil;
import jso.kpl.traveller.viewmodel.EditingMsgViewModel;

public class EditingMsg extends AppCompatActivity {

    EditingMsgBinding binding;
    EditingMsgViewModel editingMsgVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editingMsgVm = new EditingMsgViewModel();

        binding = DataBindingUtil.setContentView(this, R.layout.editing_msg);
        binding.setEditingMsgVm(editingMsgVm);
        binding.setLifecycleOwner(this);

        binding.getEditingMsgVm().setMsgBgAdapter(new ImageSideVpAdapter(getSupportFragmentManager(), 3));

        for (int j = 1; j < 7; j++) {

            ImageSideItem item = new ImageSideItem();

            Bundle bundle = new Bundle();
            bundle.putString("img", "dummy_travel_img" + j);
            item.setArguments(bundle);

            binding.getEditingMsgVm().getMsgBgAdapter().addItem(item);
        }

        binding.vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Trav.Msg", "onFocusChange: " + position);

                binding.getEditingMsgVm().cardImg = "dummy_travel_img" + (position + 1);
                /*
                 추가 예정 해당 이미지 순서를 파악해서 1~6까지는 더미 데이터 7일 때에는 이미지 추가 가능하게 할 것
                 */
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (getIntent() != null) {
            binding.getEditingMsgVm().receiverLD.setValue(getIntent().getStringExtra("p_author"));
        }

        //300자 제한을 보여주고, 10자 이상이어야 전송이 가능하다.
        binding.getEditingMsgVm().contentLD.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d("Trav.Msg", "길이: " + s.length());
                binding.getEditingMsgVm().limitCharLD.setValue(s.length() + "/300");

                if (s.length() > 9) {
                    binding.getEditingMsgVm().isSendLD.setValue(true);
                } else {
                    binding.getEditingMsgVm().isSendLD.setValue(false);
                }

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.d("Trav.Msg", "onWindowFocusChanged: " + binding.realBody.getHeight());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, binding.realBody.getHeight());
        lp.setMargins(JavaUtil.dpToPx(5), 0, JavaUtil.dpToPx(5), 0);
        binding.bgBody.setLayoutParams(lp);

    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0, 0);
        finish();
    }
}
