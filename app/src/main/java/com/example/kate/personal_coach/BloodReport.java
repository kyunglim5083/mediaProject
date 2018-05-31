package com.example.kate.personal_coach;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.kate.personal_coach.LoginActivity.Dlab_DB;

public class BloodReport extends AppCompatActivity {

    Button daily_b,weekly_b,monthly_b,empty,before_m,after_m,before_sleep;
    TableLayout table1,table2;
    TextView max_v,min_v,avg_v;
    int clicked1=2,clicked2=0;
    FirebaseUser user;
    FirebaseAuth mAuth;
    int[][]daily=new int[7][4];
    int[][]daily_cnt=new int[7][4];
    float[][]daily_avg=new float[7][4];
    int[][]week=new int[5][4];
    int[][]week_cnt=new int[5][4]; //입력한 개수
    float[][]weekly_avg=new float[5][4];
    int[]month=new int[12];
    float max=0,min=400,avg=0;
    Calendar calendar=Calendar.getInstance();
    LineChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //유저 정보 받아오기
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        daily_b=(Button)findViewById(R.id.daily_b);
        weekly_b = (Button) findViewById(R.id.weekly_b) ;
        table1=(TableLayout)findViewById(R.id.table1);
        monthly_b = (Button) findViewById(R.id.monthly_b) ;
        empty=(Button) findViewById(R.id.empty);
        before_m=(Button) findViewById(R.id.before_m);
        after_m=(Button) findViewById(R.id.after_m);
        before_sleep=(Button) findViewById(R.id.before_sleep);
        min_v=(TextView)findViewById(R.id.min_v);
        max_v=(TextView)findViewById(R.id.max_v);
        avg_v=(TextView)findViewById(R.id.avg_v);
        Button report=(Button)findViewById(R.id.report);
        //리포트
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BloodReport.this, PopupActivity.class);
                intent.putExtra("data", "Test Popup");
                startActivityForResult(intent, 1);
            }
        });

        //chart
        mChart = (LineChart) findViewById(R.id.linechart);



        //week별 최대,최소,평균 계산 할 배열 채우기
        calculateWeekData();


        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.daily_b:
                        clicked1=2;
                        setupGraph();
                        getVal(clicked1, clicked2);
                        daily_b.setTextColor(Color.rgb(255, 102, 102));
                        weekly_b.setTextColor(Color.rgb(204, 204, 204));
                        monthly_b.setTextColor(Color.rgb(204, 204, 204));

                        break;
                    case R.id.weekly_b:
                        clicked1 = 0;
                        setupGraph();
                        getVal(clicked1, clicked2);
                        Log.d("here","?????");
                        weekly_b.setTextColor(Color.rgb(255, 102, 102));
                        daily_b.setTextColor(Color.rgb(204, 204, 204));
                        monthly_b.setTextColor(Color.rgb(204, 204, 204));
                        break;
                    case R.id.monthly_b:
                        clicked1 = 1;
                        setupGraph();
                        getVal(clicked1, clicked2);
                        weekly_b.setTextColor(Color.rgb(204, 204, 204));
                        daily_b.setTextColor(Color.rgb(204, 204, 204));
                        monthly_b.setTextColor(Color.rgb(255, 102, 102));
                        break;
                    case R.id.empty:
                        clicked2 = 0;
                        setupGraph();
                        getVal(clicked1, clicked2);
                        empty.setTextColor(Color.rgb(255, 102, 102));
                        before_m.setTextColor(Color.rgb(204, 204, 204));
                        after_m.setTextColor(Color.rgb(204, 204, 204));
                        before_sleep.setTextColor(Color.rgb(204, 204, 204));
                        break;
                    case R.id.before_m:
                        clicked2 = 1;
                        setupGraph();
                        getVal(clicked1, clicked2);
                        before_m.setTextColor(Color.rgb(255, 102, 102));
                        empty.setTextColor(Color.rgb(204, 204, 204));
                        after_m.setTextColor(Color.rgb(204, 204, 204));
                        before_sleep.setTextColor(Color.rgb(204, 204, 204));
                        break;
                    case R.id.after_m:
                        clicked2 = 2;
                        setupGraph();
                        getVal(clicked1, clicked2);
                        after_m.setTextColor(Color.rgb(255, 102, 102));
                        before_m.setTextColor(Color.rgb(204, 204, 204));
                        empty.setTextColor(Color.rgb(204, 204, 204));
                        before_sleep.setTextColor(Color.rgb(204, 204, 204));
                        break;

                    case R.id.before_sleep:
                        clicked2 = 3;
                        setupGraph();
                        getVal(clicked1, clicked2);
                        before_sleep.setTextColor(Color.rgb(255, 102, 102));
                        before_m.setTextColor(Color.rgb(204, 204, 204));
                        after_m.setTextColor(Color.rgb(204, 204, 204));
                        empty.setTextColor(Color.rgb(204, 204, 204));
                        break;


                }


            }


        };
        daily_b.setOnClickListener(onClickListener);
        weekly_b.setOnClickListener(onClickListener);
        monthly_b.setOnClickListener(onClickListener);
        empty.setOnClickListener(onClickListener);
        before_m.setOnClickListener(onClickListener);
        after_m.setOnClickListener(onClickListener);
        before_sleep.setOnClickListener(onClickListener);


        setupGraph();
        getVal(clicked1, clicked2);
        daily_b.performClick();
        empty.performClick();






        //클릭 값 (peroid,time)얻어와서 평균 계산하고 배열에 넣기(평균들의 최대,최소,평균 구하기)


    }

    //week의 공복,식전,식후,취침전 데이터 합 구하기
    private void calculateWeekData() {

        final int curWeek=calendar.get(Calendar.WEEK_OF_MONTH);
        //사용자 확인.
        //Toast.makeText(getApplicationContext(),user.getDisplayName(),Toast.LENGTH_LONG).show();
        //주,월 0,1
        //공복,식전,식후,자기전 0,1,2,3
        //---------------------------------------------------------------------------------------------------------
        //------------------------------------------ period가 week인 경우.--------------------------------------------
        //주 별로 나누기
        Dlab_DB.child("Blood").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    //몇 주 인지 계산하기
                    String dateString = data.getKey();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date convertedDate = new Date();
                    try {
                        convertedDate = dateFormat.parse(dateString);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    calendar.setTime(convertedDate);


                    //시간 별로
                    for(DataSnapshot timeData: data.getChildren()){

                        String type=timeData.child("type").getValue().toString();
                        int val=Integer.parseInt(timeData.child("blood_data").getValue().toString());
                        //해당 되는 주에 더하기.
                        if(type.contains("공복")) {
                            week[calendar.get(Calendar.WEEK_OF_MONTH)-1][0]+=val;
                            week_cnt[calendar.get(Calendar.WEEK_OF_MONTH)-1][0]+=1;
                            Log.d("공복 체크",week[calendar.get(Calendar.WEEK_OF_MONTH)-1][0]+"/"+week_cnt[calendar.get(Calendar.WEEK_OF_MONTH)-1][0]+"/"+calendar.get(Calendar.WEEK_OF_MONTH));

                        }else if(type.contains("식전")) {
                            week[calendar.get(Calendar.WEEK_OF_MONTH)-1][1]+=val;
                            week_cnt[calendar.get(Calendar.WEEK_OF_MONTH)-1][1]+=1;
                        }
                        else if(type.contains("식후")) {
                            week[calendar.get(Calendar.WEEK_OF_MONTH)-1][2]+=val;
                            week_cnt[calendar.get(Calendar.WEEK_OF_MONTH)-1][2]+=1;
                        }
                        else{
                            week[calendar.get(Calendar.WEEK_OF_MONTH)-1][3]+=val;
                            week_cnt[calendar.get(Calendar.WEEK_OF_MONTH)-1][3]+=1;
                        }
                    }



                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        //
        //------------------일별로 나누기
        Dlab_DB.child("Blood").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    //이번주에 해당하는 날짜 가지고 오기
                    String dateString = data.getKey();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date convertedDate = new Date();
                    try {
                        convertedDate = dateFormat.parse(dateString);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    calendar.setTime(convertedDate);

                    //오늘과 같은 주라면. 탐색
                    if(calendar.get(Calendar.WEEK_OF_MONTH)==curWeek){

                    //시간 별로
                    for(DataSnapshot timeData: data.getChildren()){

                        String type=timeData.child("type").getValue().toString();
                        int val=Integer.parseInt(timeData.child("blood_data").getValue().toString());
                        //해당 되는 주에 더하기.
                        if(type.contains("공복")) {
                            daily[calendar.get(Calendar.DAY_OF_WEEK)-1][0]+=val;
                            daily_cnt[calendar.get(Calendar.DAY_OF_WEEK)-1][0]+=1;
                            //Log.d("공복 체크",week[calendar.get(Calendar.DAY_OF_WEEK)-1][0]+"/"+week_cnt[calendar.get(Calendar.WEEK_OF_MONTH)-1][0]+"/"+calendar.get(Calendar.WEEK_OF_MONTH));

                        }else if(type.contains("식전")) {
                            daily[calendar.get(Calendar.DAY_OF_WEEK)-1][1]+=val;
                            daily_cnt[calendar.get(Calendar.DAY_OF_WEEK)-1][1]+=1;
                        }
                        else if(type.contains("식후")) {
                            Log.d("일별 식후일때 혈당값gkq :",val+"");
                            daily[calendar.get(Calendar.DAY_OF_WEEK)-1][2]+=val;
                            daily_cnt[calendar.get(Calendar.DAY_OF_WEEK)-1][2]+=1;
                            Log.d("일별 식후 체크",daily[calendar.get(Calendar.DAY_OF_WEEK)-1][2]+"/"+daily_cnt[calendar.get(Calendar.DAY_OF_WEEK)-1][2]+"/"+calendar.get(Calendar.DAY_OF_WEEK));
                        }
                        else{
                            daily[calendar.get(Calendar.DAY_OF_WEEK)-1][3]+=val;
                            daily_cnt[calendar.get(Calendar.DAY_OF_WEEK)-1][3]+=1;
                        }
                    }
                    }


                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }
    private void calculateB(){
        //주별 평균
        if(clicked1==0){
            for(int i=0;i<5;i++){
                for(int j=0;j<4;j++){
                    if(week[i][j]!=0){
                        weekly_avg[i][j]=week[i][j]/week_cnt[i][j];

                    }

                }
            }
            Log.d("공복 평균",weekly_avg[1][0]+""+week[1][0]+"/"+week_cnt[1][0]);

        }else if(clicked1==2){//일별 평균
            for(int i=0;i<7;i++){
                for(int j=0;j<4;j++){
                    if(daily[i][j]!=0){
                        daily_avg[i][j]=daily[i][j]/daily_cnt[i][j];

                    }

                }
            }
        }

    }

    private void getVal(int period,int time){
        calculateB();
        max=0;
        min=400;
        avg=0;
        Log.d("클릭값 전달:",time+", week?"+clicked1);
        float sum=0;
        int cnt=0;
        //주간 선택했을 때.
        if(period==0) {
            for (int i = 0; i < 5; i++) {
                if (week[i][time] != 0) {
                    min = min < weekly_avg[i][time] ? min : weekly_avg[i][time];
                    max = max > weekly_avg[i][time] ? max : weekly_avg[i][time];
                    sum += weekly_avg[i][time];
                    cnt++;
                }

            }
        }else if(period==2){//일간 선택했을 때.
            for (int i = 0; i < 7; i++) {
                if (daily[i][time] != 0) {
                    min = min < daily_avg[i][time] ? min : daily_avg[i][time];
                    max = max > daily_avg[i][time] ? max : daily_avg[i][time];
                    sum += daily_avg[i][time];
                    cnt++;
                }

            }

        }else{
            min=(float)140.7;
            sum=440;
            cnt=3;
            max=(float)164.3;
        }

            if(cnt!=0 || period==1){
                avg=sum/cnt;
                min_v.setText("  "+min);
                max_v.setText("  "+max);
                avg_v.setText("  "+avg);

            }else{
                max_v.setText("");
                min_v.setText("");
                avg_v.setText("");
            }

        Log.d("클릭 한 값 계산",min+"/"+max+"/"+avg+cnt);

    }
    //graph setip
    private void setupGraph(){
        // add data
        setData();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
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
        mChart.animateX(800, Easing.EasingOption.EaseInOutQuart);

        //  dont forget to refresh the drawing
        mChart.invalidate();
    }

    //그래프에 값 세팅
    //------------------------- x좌표값 -----------------------------
    private ArrayList<String> setXAxisValues(){
        //주 일경우 x 좌표값.
        ArrayList<String> xVals = new ArrayList<String>();
        if(clicked1==0){
            xVals.clear();
            for(int i=0;i<5;i++){
                    String input=(i+1)+"주";
                    xVals.add(input);

            }

        }else if(clicked1==2){
            //일별 일경우.
            xVals.clear();
            xVals.add("일");
            xVals.add("월");
            xVals.add("화");
            xVals.add("수");
            xVals.add("목");
            xVals.add("금");
            xVals.add("토");
        }else{
            xVals.clear();
            for(int i=0;i<12;i++){
                String input=(i+1)+"월";
                xVals.add(input);

            }
        }


        return xVals;
    }

    //------------------------- 혈당 수치 값 -----------------------------
    private ArrayList<Entry> setYAxisValues(){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        if(clicked1==0){
            yVals.clear();
            for(int i=0;i<5;i++){
                if(week[i][clicked2]!=0){
                    Log.d((i+1)+"주의 혈당값",weekly_avg[i][clicked2]+"");
                    yVals.add(new Entry(weekly_avg[i][clicked2], i));
                }

            }

        }else if(clicked1==2){
            //일별 일경우.
            yVals.clear();
            for(int i=0;i<7;i++){
                if(daily[i][clicked2]!=0){
                    Log.d((i+1)+"번째 날의 혈당값",daily[i][clicked2]+"");
                    yVals.add(new Entry(daily_avg[i][clicked2], i));
                }

            }
        }else{
            yVals.clear();
            yVals.add(new Entry((float) 144.78, 0));
            yVals.add(new Entry((float) 150.88, 1));
            yVals.add(new Entry((float) 164.3, 2));
            yVals.add(new Entry((float) 140.68, 3));
            yVals.add(new Entry((float) 143.78, 4));


        }

        return yVals;
    }

    private void setData() {
        ArrayList<String> xVals = setXAxisValues();

        ArrayList<Entry> yVals = setYAxisValues();

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





}
