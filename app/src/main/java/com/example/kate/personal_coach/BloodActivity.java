package com.example.kate.personal_coach;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BloodActivity extends AppCompatActivity {

    public static DatabaseReference Dlab_DB;
    private FirebaseAuth mAuth;
    static FirebaseUser user = null;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Blood> bloodList= new ArrayList<Blood>();
    private List<ActivityVO> activityList = new ArrayList<>();
    private List<UserFood> userFoodList = new ArrayList<>();
    final BloodAdapter bloodAdapter = new BloodAdapter();
    int blood_dif;
    String bloodType;
    String foodType;
    String food;
    int steps;
    int activityType;
    long activityTime;
    float calories;

    private ValueEventListener getBloodList = new ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                Blood bvo = snapshot.getValue(Blood.class);
                bloodList.add(bvo);

            }
            Log.i("BloodList" , "size"+bloodList.size());
            bloodAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private ValueEventListener getActivityList = new ValueEventListener(){
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                ActivityVO activityVO = snapshot.getValue(ActivityVO.class);
                //Log.d("LOST^^^^^" , ":" + activityVO);
                activityList.add(activityVO);
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
                UserFood userFood = snapshot.getValue(UserFood.class);
                //Log.d("LOST^^^^^" , ":" + activityVO);
                userFoodList.add(userFood);
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);
        mRecyclerView = (RecyclerView) findViewById(R.id.blood_rc_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(bloodAdapter);
        Dlab_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Dlab_DB.child("Blood").child(mAuth.getCurrentUser().getUid()).child(getDateStr()).addValueEventListener(getBloodList);
        Dlab_DB.child("Activity").child(mAuth.getCurrentUser().getUid()).child(getDateStr()).addValueEventListener(getActivityList);
        Dlab_DB.child("UserFood").child(mAuth.getCurrentUser().getUid()).child(getDateStr()).addValueEventListener(getUserFoodList);


//        FirebaseDatabase.getInstance().getReference("Blood").child(mAuth.getCurrentUser().getUid()).child(getDateStr()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Blood blood = snapshot.getValue(Blood.class);
//                    bloodList.add(blood);
//
//                }
//
//                bloodAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



        final EditText bloodInput = (EditText)findViewById(R.id.bloodInput);
        Button sendButton = (Button)findViewById(R.id.sendButton);

        Spinner s = (Spinner) findViewById(R.id.spinner);

        final Blood b = new Blood();
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = parent.getItemAtPosition(position);
                b.setType(itemAtPosition.toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                b.setTime(getDateStr()+" "+getTimeStr());
                b.setBlood_data(Integer.parseInt(bloodInput.getText().toString()));

                user = mAuth.getCurrentUser();
                Dlab_DB.child("Blood").child(user.getUid()).child(getDateStr()).child(getTimeStr()).setValue(b);

                Toast.makeText(BloodActivity.this, "혈당입력완료", Toast.LENGTH_SHORT).show();

                if(bloodList.size() >0){
                    if(b.getBlood_data() - bloodList.get(bloodList.size()-1).getBlood_data() > 70){
                        Log.d("Compare%%%", "blood" + (b.getBlood_data() - bloodList.get(bloodList.size()-1).getBlood_data() ));
                        final AnalysisVO avo = new AnalysisVO();

                        blood_dif = (b.getBlood_data() - bloodList.get(bloodList.size()-1).getBlood_data());
                        long walkingTime=0;
                        long noneTime=0;
                        long vehicleTime=0;

//                        bloodType;
//                        foodType;
//                        food;
//                        steps;
//                        activityType;
//                        activityTime;
//                        calories;

                        avo.setBloodType(b.getType());
                        avo.setBlood_dif(b.getBlood_data() - bloodList.get(bloodList.size()-1).getBlood_data());

                        if(avo.getBlood_dif() >= 70 || avo.getBlood_dif() <= -70){
                            Log.i("TTTTTT", "TTTTT");
                            for(ActivityVO activity: activityList){
                                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                try {

                                    Date compareBlood = sdfNow.parse(bloodList.get(bloodList.size()-1).getTime());
                                    Log.i("COMPARE1@@@@@@@@@", ":"+compareBlood);
                                    Date startTime = sdfNow.parse(activity.getStartTime().substring(0,16));//activity 시작시간
                                    Log.i("COMPARE2@@@@@@@@", ":"+startTime);
                                    Date endTime = sdfNow.parse(activity.getEndTime().substring(0,16));//activity 끝나시간
                                    Log.i("COMPARE3@@@@@@@@", ":"+endTime);

                                    Date now = new Date(System.currentTimeMillis());//현재 시

                                    if(now.after(endTime) || compareBlood.after(startTime)){
                                        if(activity.getField().equals("steps"))
                                            steps += Integer.parseInt(activity.getValue());
                                        if(activity.getField().equals("calories"))
                                            calories += Integer.parseInt(activity.getValue());
                                        if(activity.getField().equals("activity")){
                                            if(activity.getValue().equals("3")){
                                                //still-not moving
                                                noneTime += (endTime.getTime()-startTime.getTime())/60000;

                                            }else if(activity.getValue().equals("7")){
                                                //Walking
                                                walkingTime += (endTime.getTime() -startTime.getTime())/60000;

                                            }else if(activity.getValue().equals("5")){
                                                //in Vehicle
                                                vehicleTime += (endTime.getTime()-startTime.getTime())/60000;
                                            }
                                        }
                                        Log.i("Compare%%%%%%%", "steps:" + steps + " calories " + calories);

                                    }
                                } catch (ParseException e) {
                                    Log.i("ERROR", "error");
                                    e.printStackTrace();
                                }
                            }

                            HashMap<String, Long> activity = new HashMap<>();
                            activity.put("still",noneTime);
                            activity.put("walking", walkingTime);
                            activity.put("vehicle", vehicleTime);

                            avo.setSteps(steps);
                            avo.setCalories(calories);
                            avo.setActivity(activity);

                            Dlab_DB.child("Analysis").child(user.getUid()).child(getDateStr()).child(getTimeStr()).setValue(avo);

                        }
                        //운동데이터, 식사데이터 조회
                        //현재 입력한 혈당이 이전 혈당보다 높을 때
                       // Dlab_DB.child("Analysis").child(user.getUid()).child(getDateStr()).child(getTimeStr()).setValue(avo);

                    }
                }
                bloodList.clear();

            }
        });

    }

    private void compareBlood(Blood b) {
        //공복 - 정상: 100미만, 당뇨 전단계 : 100-125, 당뇨 : 126 이상
        //식후 - 정상 140미만, 당뇨 전단계 : 140-199, 당뇨 : 200 이상

        //이전에 입력한 혈당과의 차이가 70 이상일 때 혈당 type, 혈당차, 걸음수, 운동 종류, 먹은 음식 디비에 저장

        //디비에서 오늘 입력한 혈당중 제일 마지막에 입력한 값 가져오기

        //가져온 값과 현재 입력한 값 비교

        //차이가 +- 70 이상이면 디비에 등록




//        Dlab_DB.child("Blood").child(mAuth.getCurrentUser().getUid()).child(getDateStr()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("CompareBlood####", "value : " + snapshot.getValue());
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });




    }

    public String getDateStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        return sdfNow.format(date);
    }
    public String getTimeStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
        return sdfNow.format(date);
    }

    class BloodAdapter extends RecyclerView.Adapter<BloodAdapter.ViewHolder>{



        @Override
        public BloodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood, parent, false);

            return new BloodAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BloodAdapter.ViewHolder holder, int position) {


            holder.tv_blood_data.setText(String.valueOf(bloodList.get(position).blood_data));
            holder.tv_bloodInfo.setText(String.valueOf(bloodList.get(position).type));
            holder.tv_dateInfo.setText(String.valueOf(bloodList.get(position).time));
        }



        @Override
        public int getItemCount() {
            //Log.i("Size", "size$$$" + bloodList.size());
            return bloodList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_blood_data;
            TextView tv_bloodInfo;
            TextView tv_dateInfo;
            CardView cv;

            public ViewHolder(View view) {
                super(view);
                tv_blood_data = (TextView) view.findViewById(R.id.tv_blood_data);
                tv_bloodInfo = (TextView) view.findViewById(R.id.tv_bloodInfo);
                tv_dateInfo = (TextView) view.findViewById(R.id.tv_dateInfo);
                cv = (CardView) view.findViewById(R.id.cv);

            }
        }
    }

}

