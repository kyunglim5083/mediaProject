package com.example.kate.personal_coach;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BloodReport extends AppCompatActivity {

    Button weekly_b,monthly_b,empty,before_m,after_m,before_sleep;
    int clicked1=0,clicked2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        weekly_b = (Button) findViewById(R.id.weekly_b) ;

        monthly_b = (Button) findViewById(R.id.monthly_b) ;
        empty=(Button) findViewById(R.id.empty);
        before_m=(Button) findViewById(R.id.before_m);
        after_m=(Button) findViewById(R.id.after_m);
        before_sleep=(Button) findViewById(R.id.before_sleep);


        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView2 = (TextView) findViewById(R.id.text2);
                TextView textView3 = (TextView) findViewById(R.id.text3);

                switch (v.getId()) {
                    case R.id.weekly_b:
                        clicked1=0;
                        textView2.setVisibility(View.VISIBLE);
                        textView3.setVisibility(View.INVISIBLE);
                        weekly_b.setTextColor(Color.rgb(255, 102, 102));
                        monthly_b.setTextColor(Color.rgb(204, 204, 204));
                        break;
                    case R.id.monthly_b:
                        clicked1=1;
                        textView2.setVisibility(View.INVISIBLE);
                        textView3.setVisibility(View.VISIBLE);
                        weekly_b.setTextColor(Color.rgb(204, 204, 204));
                        monthly_b.setTextColor(Color.rgb(255, 102, 102));
                        break;
                    case R.id.empty:
                        clicked2=0;
                        empty.setTextColor(Color.rgb(255, 102, 102));
                        before_m.setTextColor(Color.rgb(204, 204, 204));
                        after_m.setTextColor(Color.rgb(204, 204, 204));
                        before_sleep.setTextColor(Color.rgb(204, 204, 204));
                        break;
                    case R.id.before_m:
                        clicked2=1;
                        before_m.setTextColor(Color.rgb(255, 102, 102));
                        empty.setTextColor(Color.rgb(204, 204, 204));
                        after_m.setTextColor(Color.rgb(204, 204, 204));
                        before_sleep.setTextColor(Color.rgb(204, 204, 204));
                        break;
                    case R.id.after_m:
                        clicked2=2;
                        after_m.setTextColor(Color.rgb(255, 102, 102));
                        before_m.setTextColor(Color.rgb(204, 204, 204));
                        empty.setTextColor(Color.rgb(204, 204, 204));
                        before_sleep.setTextColor(Color.rgb(204, 204, 204));
                        break;

                    case R.id.before_sleep:
                        clicked2=3;
                        before_sleep.setTextColor(Color.rgb(255, 102, 102));
                        before_m.setTextColor(Color.rgb(204, 204, 204));
                        after_m.setTextColor(Color.rgb(204, 204, 204));
                        empty.setTextColor(Color.rgb(204, 204, 204));
                        break;


                }

            }


        };
        weekly_b.setOnClickListener(onClickListener);
        monthly_b.setOnClickListener(onClickListener);
        empty.setOnClickListener(onClickListener);
        before_m.setOnClickListener(onClickListener);
        after_m.setOnClickListener(onClickListener);
        before_sleep.setOnClickListener(onClickListener);

        weekly_b.callOnClick();
        empty.callOnClick();


        //첫번째가 디폴트..



        // 코드 계속 ...
    }

    private void changeGraph(int index) {
        //주,월 0,1
        //공복,식전,식후,자기전 0,1,2,3




    }



}
