package com.example.kate.personal_coach;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FoodActivity extends AppCompatActivity implements View.OnClickListener{

    public static DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    static FirebaseUser user = null;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Food> foodList;
    private ArrayList<UserFood> userFoodList;

    private UserFoodAdapter foodAdapter;
    private TextView tv_totalCalory;
    private Food food;
    private UserFood userFood;
    private String mealTime, currDate;
    private double totalCalory=0;

    private ValueEventListener getFoodList = new ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                Food food = snapshot.getValue(Food.class);

                foodList.add(food);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener getUserFoodList = new ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                UserFood uFood = snapshot.getValue(UserFood.class);
                userFoodList.add(uFood);
                totalCalory += uFood.getKcal();
            }
            tv_totalCalory.setText(totalCalory+"");
            foodAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        mRecyclerView = (RecyclerView) findViewById(R.id.food_rc_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        foodAdapter = new UserFoodAdapter();
        mRecyclerView.setAdapter(foodAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        getCurrentDate();
        setTimeSpinner();

        foodList = new ArrayList<>();
        userFoodList = new ArrayList<>();

        tv_totalCalory = (TextView) findViewById(R.id.totalCalory);

        databaseReference.child("Food").addValueEventListener(getFoodList);
        databaseReference.child("User_Food").child(user.getUid()).child(currDate).addValueEventListener(getUserFoodList);

        Button saveMealBtn = (Button)findViewById(R.id.saveMealBtn);
        saveMealBtn.setOnClickListener(this);

        //키워드 자동완성
        AutoCompleteTextView foodNameAuto = (AutoCompleteTextView)findViewById(R.id.foodNameAuto);
        foodNameAuto.setAdapter(new ArrayAdapter<Food>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                foodList));

        foodNameAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                food = (Food) adapterView.getItemAtPosition(i);
            }
        });

    }

    //현재 날짜 구하기
    public void getCurrentDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        currDate = sdfNow.format(date);
    }

    //현재 시간 구하기
    public String currentTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
        String formatDate = sdfNow.format(date);

        return formatDate;
    }

    public void setTimeSpinner(){
        final String currTime = currentTime();

        String[] timeList = {
                currTime, "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"
        };

        Spinner mealTimeSpinner = (Spinner)findViewById(R.id.mealTimeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                timeList);
        mealTimeSpinner.setAdapter(adapter);
        mealTimeSpinner.setSelection(0);
        mealTime = currTime;

        mealTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mealTime = (String) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mealTime = currTime;
            }
        });
    }

    @Override
    public void onClick(View view) {
        // 음식정보 저장
        userFood = new UserFood();
        userFood.setTime(currDate+" "+mealTime);
        userFood.setKcal(food.getKcal());
        userFood.setName(food.getName());

        databaseReference.child("User_Food").child(user.getUid()).child(currDate).child(mealTime).setValue(userFood);

        //adapter 갱신
        userFoodList.clear();
        totalCalory=0;
        foodAdapter.notifyDataSetChanged();
    }



    class UserFoodAdapter extends RecyclerView.Adapter<UserFoodAdapter.ViewHolder>{


        @Override
        public UserFoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);

            return new UserFoodAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserFoodAdapter.ViewHolder holder, int position) {
            holder.tv_food_name.setText(String.valueOf(userFoodList.get(position).getName()));
            holder.tv_kcal.setText(String.valueOf(userFoodList.get(position).getKcal())+" kcal");
            holder.tv_timeInfo.setText(String.valueOf(userFoodList.get(position).getTime()));
        }



        @Override
        public int getItemCount() {
            return userFoodList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_food_name;
            TextView tv_kcal;
            TextView tv_timeInfo;
            CardView cv;

            public ViewHolder(View view) {
                super(view);
                tv_food_name = (TextView) view.findViewById(R.id.tv_food_name);
                tv_kcal = (TextView) view.findViewById(R.id.tv_kcal);
                tv_timeInfo = (TextView) view.findViewById(R.id.tv_timeInfo);
                cv = (CardView) view.findViewById(R.id.cv_food);

            }
        }
    }
}
