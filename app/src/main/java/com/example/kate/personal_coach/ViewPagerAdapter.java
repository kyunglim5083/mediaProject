package com.example.kate.personal_coach;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by SeoYeon Choi on 2018-05-07.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<RecommendComment> commentList;

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
        commentList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return new RecommendFragment().newInstance(commentList.get(position).getType(), commentList.get(position).getComment());
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    public void setCommentList(RecommendComment comment){
        commentList.add(comment);
    }

}
