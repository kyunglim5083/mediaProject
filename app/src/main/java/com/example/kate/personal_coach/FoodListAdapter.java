package com.example.kate.personal_coach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by SeoYeon Choi on 2018-04-30.
 */

public class FoodListAdapter extends BaseAdapter{

    private ArrayList<Map<String, Object>> foodList;

    public FoodListAdapter() {
        foodList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Object getItem(int i) {
        return foodList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();

        String keyName = null;
        Food food = null;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_meal, viewGroup, false);
        }

        TextView timeText = (TextView) view.findViewById(R.id.timeText);
        TextView foodNameText = (TextView) view.findViewById(R.id.foodNameText);
        TextView kcalText = (TextView) view.findViewById(R.id.kcalText);

        Map<String, Object> userFood = foodList.get(i);

        Set key = userFood.keySet();

        for (Iterator iterator = key.iterator(); iterator.hasNext();) {
            keyName = (String) iterator.next();
            food = (Food) userFood.get(keyName);
        }
        timeText.setText(keyName);
        foodNameText.setText(food.getName());
        kcalText.setText(food.getKcal()+"");

        return view;
    }

    public void addFoodList(Map<String, Object> food){
        foodList.add(food);
    }
}
