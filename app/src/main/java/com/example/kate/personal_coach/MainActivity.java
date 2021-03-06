package com.example.kate.personal_coach;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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

public class MainActivity extends AppCompatActivity implements OnChartGestureListener,
        OnChartValueSelectedListener{

    private LineChart mChart;

    private LinearLayout bloodLayout, foodLayout;
    private FloatingActionButton mFab, addBloodBtn, addFoodBtn;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    static FirebaseUser user = null;
    private ViewPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private String currDate;

    private UserFood[] foodList;
    private ArrayList<String> xVals;
    private ArrayList<Entry> yVals;

    private ValueEventListener getUserFoodList = new ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                UserFood food = snapshot.getValue(UserFood.class);

                if(food.getMealType().equals("아침")){
                    foodList[0] = food;
                }else if(food.getMealType().equals("점심")){
                    foodList[1] = food;
                }else if(food.getMealType().equals("저녁")){
                    foodList[2] = food;
                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolBar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);

        //주간,월간 보고서로 넘어가는 버튼

        ImageView gotoGraph=(ImageView)findViewById(R.id.gotoGraph);
        gotoGraph.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,BloodReport.class);
                startActivity(intent);
            }
        });
        mChart = (LineChart) findViewById(R.id.linechart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        //------------------------- 그래프 밑에 채우는거 -----------------------------
        mChart.setDrawGridBackground(false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        foodList = new UserFood[3];

        getbloodData();
        // add data
        setData();

        databaseReference.child("User_Food").child(user.getUid()).child(currDate).addValueEventListener(getUserFoodList);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // no description text
        mChart.setDescription("Dlab");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        //------------------------- 경계 -----------------------------
        LimitLine upper_limit = new LimitLine(140f, "신장 역치(renal threshold)");
        upper_limit.setLineWidth(2f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

       /* LimitLine lower_limit = new LimitLine(-30f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);*/

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);
        //leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(220f);
        leftAxis.setAxisMinValue(70f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 5f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        //------------------------- 점점 나타나게 애니메이션  -----------------------------
        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        //  dont forget to refresh the drawing
        mChart.invalidate();

        //추천 pager adapter
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        setViewPager();

        //set 플로팅 액션 버튼
        mFab = (FloatingActionButton)findViewById(R.id.fab_plus);
        addBloodBtn = (FloatingActionButton)findViewById(R.id.addBloodBtn);
        addFoodBtn = (FloatingActionButton)findViewById(R.id.addFoodBtn);
        bloodLayout = (LinearLayout)findViewById(R.id.bloodLayout);
        foodLayout = (LinearLayout)findViewById(R.id.foodLayout);
        final LinearLayout blackLayout = (LinearLayout)findViewById(R.id.main_black);

        final Animation mShowButton = AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_button);
        final Animation mHideButton = AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_button);
        final Animation mShowLayout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_layout);
        final Animation mHideLayout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_layout);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bloodLayout.getVisibility() == View.VISIBLE && foodLayout.getVisibility() == View.VISIBLE){
                    bloodLayout.setVisibility(View.GONE);
                    foodLayout.setVisibility(View.GONE);
                    blackLayout.setVisibility(View.GONE);
                    bloodLayout.startAnimation(mHideLayout);
                    foodLayout.startAnimation(mHideLayout);
                    blackLayout.startAnimation(mHideLayout);
                    mFab.startAnimation(mHideButton);

                }else{
                    bloodLayout.setVisibility(View.VISIBLE);
                    foodLayout.setVisibility(View.VISIBLE);
                    blackLayout.setVisibility(View.VISIBLE);
                    bloodLayout.startAnimation(mShowLayout);
                    foodLayout.startAnimation(mShowLayout);
                    blackLayout.startAnimation(mShowLayout);
                    mFab.startAnimation(mShowButton);

                }
            }
        });

        addBloodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodLayout.setVisibility(View.GONE);
                foodLayout.setVisibility(View.GONE);
                blackLayout.setVisibility(View.GONE);
                blackLayout.startAnimation(mHideLayout);
                bloodLayout.startAnimation(mHideLayout);
                foodLayout.startAnimation(mHideLayout);
                mFab.startAnimation(mHideButton);

                Intent intent= new Intent(MainActivity.this, BloodActivity.class);
                startActivity(intent);

            }
        });

        addFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodLayout.setVisibility(View.GONE);
                foodLayout.setVisibility(View.GONE);
                blackLayout.setVisibility(View.GONE);
                blackLayout.startAnimation(mHideLayout);
                bloodLayout.startAnimation(mHideLayout);
                foodLayout.startAnimation(mHideLayout);
                mFab.startAnimation(mHideButton);
                blackLayout.setVisibility(View.GONE);

                Intent intent= new Intent(MainActivity.this, FoodActivity.class);
                startActivity(intent);

            }
        });
    }

    private void getbloodData(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        currDate = sdfNow.format(date);

        yVals = new ArrayList<>();

        databaseReference.child("Blood").child(user.getUid()).child(currDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Blood blood = snapshot.getValue(Blood.class);

                    if(blood.getType().equals("공복")){
                        yVals.add(new Entry(blood.getBlood_data(), 0));
                    }else if(blood.getType().equals("아침 식전")){
                        yVals.add(new Entry(blood.getBlood_data(), 1));
                    }else if(blood.getType().equals("아침 식후")){
                        yVals.add(new Entry(blood.getBlood_data(), 2));
                    }
                    else if(blood.getType().equals("점심 식전")){
                        yVals.add(new Entry(blood.getBlood_data(), 3));
                    }
                    else if(blood.getType().equals("점심 식후")){
                        yVals.add(new Entry(blood.getBlood_data(), 4));
                    }
                    else if(blood.getType().equals("저녁 식전")){
                        yVals.add(new Entry(blood.getBlood_data(), 5));
                    }
                    else if(blood.getType().equals("저녁 식후")){
                        yVals.add(new Entry(blood.getBlood_data(), 6));
                    }else if(blood.getType().equals("자기전")){
                        yVals.add(new Entry(blood.getBlood_data(), 7));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //------------------------- x좌표값 -----------------------------
    private ArrayList<String> setXAxisValues(){
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("공복");
        xVals.add(" ");
        xVals.add("아침 식후");
        xVals.add(" ");
        xVals.add("점심 식후");
        xVals.add(" ");
        xVals.add("저녁 식후");
        xVals.add("자기전");

        return xVals;
    }

    private void setData() {

        xVals = setXAxisValues();

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "혈당 수치");

        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        //   set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.rgb(255,102,102));
        set1.setCircleColor(Color.rgb(255,204,000));
        set1.setLineWidth(3f);
        set1.setCircleRadius(5f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(13f);
        set1.setDrawFilled(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        // set data
        mChart.setData(data);

    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "tap" );
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("하하", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    //------------------------- 데이터 값 누르면 정보가 나옴. -----------------------------
    @Override
    public void onValueSelected(Entry e, final int dataSetIndex, Highlight h) {
        TextView tv_blood = (TextView)findViewById(R.id.tv_blood);
        TextView tv_food = (TextView)findViewById(R.id.tv_foodInfo);
        TextView tv_exercise = (TextView)findViewById(R.id.tv_exerciseInfo);
        LinearLayout infoLayout = (LinearLayout)findViewById(R.id.infoLayout);
        LinearLayout foodInfo = (LinearLayout)findViewById(R.id.foodInfoLayout);

        foodInfo.setVisibility(View.INVISIBLE);
        infoLayout.setVisibility(View.VISIBLE);

        String label = xVals.get(e.getXIndex()).toString();
        Log.i("########Entry selected",label+","+e.getVal());//e.toString()
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleXIndex() + ", high: " + mChart.getHighestVisibleXIndex());

        //Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());

        tv_blood.setText("혈당: "+ e.getVal()+"mg/dL");

        if(e.getXIndex() == 2){
            tv_food.setText("식단: " + foodList[0].getName() + "  /  칼로리: "  +foodList[0].getKcal()+ " kcal");
            foodInfo.setVisibility(View.VISIBLE);
        }else if(e.getXIndex() == 4){
            tv_food.setText("식단: " + foodList[1].getName() + "  /  칼로리: "  +foodList[1].getKcal()+ " kcal");
            foodInfo.setVisibility(View.VISIBLE);
        }else if(e.getXIndex() == 6) {
            tv_food.setText("식단: " + foodList[2].getName() + "  /  칼로리: "  +foodList[2].getKcal()+ " kcal");
            foodInfo.setVisibility(View.VISIBLE);
        }
        tv_exercise.setText("총 소모 칼로리: ");
    }


    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public void setViewPager(){
        databaseReference.child("Recommend_Comment").child("bloodsugar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> comments = new ArrayList<>();
                RecommendComment rcComment = new RecommendComment();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    comments.add(snapshot.getValue(String.class));
                }
                rcComment.setType(1);
                rcComment.setComment(comments.get(0));
                pagerAdapter.setCommentList(rcComment);
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("Recommend_Comment").child("meal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> comments = new ArrayList<>();
                RecommendComment rcComment = new RecommendComment();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    comments.add(snapshot.getValue(String.class));
                }
                rcComment.setType(2);
                rcComment.setComment(comments.get(0));

                pagerAdapter.setCommentList(rcComment);
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("Recommend_Comment").child("exercise").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> comments = new ArrayList<>();
                RecommendComment rcComment = new RecommendComment();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    comments.add(snapshot.getValue(String.class));
                }
                rcComment.setType(3);
                rcComment.setComment(comments.get(0));

                pagerAdapter.setCommentList(rcComment);
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
