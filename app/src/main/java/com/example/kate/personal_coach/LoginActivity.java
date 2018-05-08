package com.example.kate.personal_coach;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


//구글 로그인
public class LoginActivity extends BaseActivity implements
        View.OnClickListener,  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnDataPointListener{

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    public static DatabaseReference Dlab_DB;

    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private GoogleApiClient mClient;

    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    static String sex;
    static int age,height,weight;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    static FirebaseUser user=null;

    private GoogleSignInClient mGoogleSignInClient;
    private ActivityService activityService;

    DailyTotalResult resultcalories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //파베 db instance가져오기
        Dlab_DB = FirebaseDatabase.getInstance().getReference();

        // Views
        // Butn listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }



    }


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d("예외 ","예외");
                e.printStackTrace();



                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
        if (requestCode == 0 && resultCode==RESULT_OK) {
            sex=data.getStringExtra("sex");
            age=Integer.parseInt(data.getStringExtra("age"));
            height=Integer.parseInt(data.getStringExtra("height"));
            weight=Integer.parseInt(data.getStringExtra("weight"));
            User user_1 = new User(user.getEmail(), user.getDisplayName(),sex,age,height,weight);
            Dlab_DB.child("User").child(user.getUid()).setValue(user_1);
            Toast.makeText(getApplicationContext(),"가입 성공!",Toast.LENGTH_LONG).show();

            callGraphAct();
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                findFitnessDataSources();
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //#####  로그인 성공 #####
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            //Member DB에 입력
                            Log.d("###########", user.getDisplayName()+" "+user.getEmail());
                            writeNewUser(user.getUid());

                            //버튼 교체
                            //updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            // mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            //mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);


            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            Log.d("로그인 ","okokokokok");
            signIn();

        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.disconnect_button) {
            revokeAccess();
        }
    }

    //-----------------user DB에 등록--------------
    private void writeNewUser(final String userId) {
        //로그인만..회원 정보 입력 안한상태

        //회원 db 중복 검사
        // 데이터베이스 읽기 #2. Single ValueEventListener
        Dlab_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //없는 회원일 경우
                //페이지 이동해서 값 받아오기

                if(!dataSnapshot.child("User").child(userId).exists()){
                    //회원 db에 등록
                    Intent intent=new Intent(LoginActivity.this,GetUserInfo.class);
                    startActivityForResult(intent,0);
                }else{
                    callGraphAct();

                    startActivityService();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void callGraphAct(){
        //혈당 그래프 페이지(메인페이지)로 이동
        Intent intent  = new Intent(LoginActivity.this, BloodSugarG.class);
        startActivity(intent);
    }


    private void startActivityService(){
        mAuth = FirebaseAuth.getInstance();
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( DataType.TYPE_STEP_COUNT_CUMULATIVE, DataType.TYPE_CALORIES_EXPENDED)
                .setDataSourceTypes( DataSource.TYPE_RAW )
                .build();



        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }else if(DataType.TYPE_CALORIES_EXPENDED.equals(dataSource.getDataType())){
                        registerFitnessDataListener(dataSource, DataType.TYPE_CALORIES_EXPENDED);
                    }
                }
            }
        };



        Fitness.SensorsApi.findDataSources(mClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);

        if (hasRuntimePermissions()) {
            findFitnessDataSourcesWrapper();
        } else {
            requestRuntimePermissions();
        }




        Intent intent = new Intent(LoginActivity.this,ActivityService.class);
        startService(intent);


    }


    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                .setSamplingRate( 3, TimeUnit.SECONDS )
                .build();

        Fitness.SensorsApi.add( mClient, request, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                    }
                });
    }
    private void requestRuntimePermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }
    private void findFitnessDataSourcesWrapper() {
        if (hasOAuthPermission()) {
            findFitnessDataSources();
        } else {
            requestOAuthPermission();
        }
    }
    private void findFitnessDataSources() {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataTypes(DataType.TYPE_CALORIES_EXPENDED)
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                .setDataTypes(DataType.TYPE_WORKOUT_EXERCISE)
                .setDataTypes(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE)
                .setDataTypes(DataType.TYPE_DISTANCE_DELTA)
                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                .setDataTypes(DataType.TYPE_ACTIVITY_SEGMENT)
                .setDataSourceTypes( DataSource.TYPE_RAW )
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                    else if(DataType.TYPE_CALORIES_EXPENDED.equals(dataSource.getDataType())){
                        registerFitnessDataListener(dataSource, DataType.TYPE_CALORIES_EXPENDED);
                    }
                    else if(DataType.TYPE_STEP_COUNT_DELTA.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA);
                    }else if(DataType.TYPE_LOCATION_SAMPLE.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_LOCATION_SAMPLE);
                    }else if(DataType.TYPE_WORKOUT_EXERCISE.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_WORKOUT_EXERCISE);
                    }else if(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_CYCLING_PEDALING_CUMULATIVE);
                    }else if(DataType.TYPE_DISTANCE_DELTA.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_DISTANCE_DELTA);
                    }else if(DataType.TYPE_HEART_RATE_BPM.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_HEART_RATE_BPM);
                    }else if(DataType.TYPE_ACTIVITY_SEGMENT.equals( dataSource.getDataType() )){
                        registerFitnessDataListener(dataSource, DataType.TYPE_ACTIVITY_SEGMENT);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);

    }

    private FitnessOptions getFitnessSignInOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .addDataType(DataType.TYPE_LOCATION_SAMPLE)
                .addDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .addDataType(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE)
                .addDataType(DataType.TYPE_DISTANCE_DELTA)
                .addDataType(DataType.TYPE_HEART_RATE_BPM)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .build();
    }
    private boolean hasOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
    }
    private boolean hasRuntimePermissions() {
        int permissionState =
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions);
    }



    @Override
    public void onDataPoint(DataPoint dataPoint) {
        final String today = getDateStr();
        final String time = getTimeStr();
        final ActivityVO activityVO = new ActivityVO();

        Log.i("DataPoint", ""+dataPoint.getDataSource());

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setStreamName(TAG + " - calories")
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet dataSet = DataSet.create(dataSource);


        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        Log.i("**************startTime", ""+startTime);
        Log.i("**************endTime", ""+endTime);
        ////////


        DataPoint dataPoint2 = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint2.getValue(Field.FIELD_CALORIES);
        dataSet.add(dataPoint2);
        /////////


        Log.i("$$$$$$$$$readRequest", "test" + dataSet.getDataType().getFields().get(0).getName() + " " + dataPoint2.getValue(Field.FIELD_CALORIES));

        activityVO.setKcal(Float.parseFloat(dataPoint2.getValue((Field.FIELD_CALORIES)).toString()));
        for( final Field field : dataPoint.getDataType().getFields() ) {
            dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
            Value value = dataPoint.getValue( field );
            Toast.makeText(getApplicationContext(), "field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
            Log.i("FitnessActivity", "TEST" + field.getName() + ": "+value);
            activityVO.setTime(time);
            if(field.getName() == "com.google.calories.expended")
                activityVO.setKcal(value.asFloat());
            else{
                if(Dlab_DB.child("Activity").child(user.getUid()).child(today.replace("/", "")) == null){
                    value.setInt(0);
                }
                activityVO.setStep(value.asInt());

            }



        }

        user = mAuth.getCurrentUser();
         Dlab_DB.child("Activity").child(user.getUid()).child(today.replace("/","")).setValue(activityVO);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e("HistoryAPI", "onConnected");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }
    public String getDateStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM/dd");
        return sdfNow.format(date);
    }

    public String getTimeStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM/dd HH:mm:ss");
        return sdfNow.format(date);
    }


}
