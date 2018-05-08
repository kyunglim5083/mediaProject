package com.example.kate.personal_coach;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SeoYeon Choi on 2018-05-07.
 */

public class RecommendFragment extends Fragment {

    public static RecommendFragment newInstance(int type, String comment){

        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("comment", comment);

        RecommendFragment fragment = new RecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_comment, container, false);
        ImageView image = (ImageView)view.findViewById(R.id.img_rc_type);
        ImageView page1 = (ImageView)view.findViewById(R.id.page1);
        ImageView page2 = (ImageView)view.findViewById(R.id.page2);
        ImageView page3 = (ImageView)view.findViewById(R.id.page3);
        TextView tv_type = (TextView)view.findViewById(R.id.tv_rc_type);
        TextView tv_comment = (TextView)view.findViewById(R.id.tv_rc_comment);

        //혈당
        if(getArguments().getInt("type") == 1){
            image.setImageResource(R.drawable.blood_drop);
            tv_type.setText("오늘의 혈당 관리 point!");
            page1.setImageResource(R.drawable.filled_circle);
            page2.setImageResource(R.drawable.dry);
            page3.setImageResource(R.drawable.dry);
        //식단
        }else if(getArguments().getInt("type") == 2){
            image.setImageResource(R.drawable.food);
            tv_type.setText("오늘의 식단 point!");
            page1.setImageResource(R.drawable.dry);
            page2.setImageResource(R.drawable.filled_circle);
            page3.setImageResource(R.drawable.dry);

        //운동
        }else{
            image.setImageResource(R.drawable.exercise);
            tv_type.setText("오늘의 운동 point!");
            page1.setImageResource(R.drawable.dry);
            page2.setImageResource(R.drawable.dry);
            page3.setImageResource(R.drawable.filled_circle);
        }

        tv_comment.setText(getArguments().getString("comment"));

        return view;
    }
}
