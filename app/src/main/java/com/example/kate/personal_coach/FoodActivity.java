package com.example.kate.personal_coach;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private Spinner mealTimeSpinner;
    public static DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    static FirebaseUser user = null;
    private ArrayList<Food> foodList;
    private AutoCompleteTextView foodNameAuto;
    private FoodListAdapter adapter;
    private ListView userMealList;
    private TextView totalCalory;
    private Food userFood;
    private Button saveMealBtn;
    private String mealTime, currDate;
    private double totalCal;

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
            Map<String, Object> map = new HashMap<>();

            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                String key = snapshot.getKey();

                if(!key.equals("total_kcal")){
                    map.put(key, snapshot.getValue(Food.class));
                    adapter.addFoodList(map);
                }
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener getTotalCalory = new ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.getValue() == null){
                totalCal = 0;
            }else{
                totalCal = dataSnapshot.getValue(Double.class);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        getCurrentDate();
        setTimeSpinner();

        foodList = new ArrayList<>();
        adapter = new FoodListAdapter();

        userMealList = (ListView)findViewById(R.id.userMealList);
        totalCalory = (TextView) findViewById(R.id.totalCalory);
        userMealList.setAdapter(adapter);

        databaseReference.child("Food").addValueEventListener(getFoodList);
        databaseReference.child("User_Food")
                .child(user.getUid())
                .child(currDate)
                .addValueEventListener(getUserFoodList);

        databaseReference.child("User_Food")
                .child(user.getUid())
                .child(currDate).child("total_kcal")
                .addValueEventListener(getTotalCalory);

        saveMealBtn = (Button)findViewById(R.id.saveMealBtn);
        saveMealBtn.setOnClickListener(this);
        totalCalory.setText(totalCal+"");

        foodNameAuto = (AutoCompleteTextView)findViewById(R.id.foodNameAuto);
        foodNameAuto.setAdapter(new ArrayAdapter<Food>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                foodList));

        foodNameAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userFood = (Food) adapterView.getItemAtPosition(i);
            }
        });

    }

    public void getCurrentDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        currDate = sdfNow.format(date);
    }

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

        mealTimeSpinner = (Spinner)findViewById(R.id.mealTimeSpinner);
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
        databaseReference.child("User_Food")
                .child(user.getUid())
                .child(currDate).child(mealTime)
                .setValue(userFood);

        databaseReference.child("User_Food")
                .child(user.getUid())
                .child(currDate).child("total_kcal")
                .setValue(totalCal+userFood.getKcal());

    }
}
