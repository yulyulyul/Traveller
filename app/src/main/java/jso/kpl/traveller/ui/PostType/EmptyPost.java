package jso.kpl.traveller.ui.PostType;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import jso.kpl.traveller.R;
import jso.kpl.traveller.databinding.EmptyPostBinding;

public class EmptyPost extends LinearLayout {

    EmptyPostBinding binding;

    public EmptyPost(Context context, int type) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        binding = DataBindingUtil.inflate(inflater, R.layout.empty_post, this, true);

        //좋아요 리스트로 가게 하는 방법
        //포스트 작성으로 가게 하는 방법
        switch (type){

            case 1:
                binding.setStr("좋아요를 누른 포스트가 없습니다.");
                break;
            case 2:
                binding.setStr("최근 보신 포스트가 없습니다.");
                break;
            case 3:
                binding.setStr("등록된 포스트가 없습니다.");
                break;


        }


    }
}
