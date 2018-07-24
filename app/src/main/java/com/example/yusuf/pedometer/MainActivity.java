package com.example.yusuf.pedometer;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    TextView stepCountTxV;
    TextView stepDetectTxV;

    ToggleButton ServiceBtn;

    String countedStep;
    String DetectedStep;
    static final String State_Count = "Counter";
    static final String State_Detect = "Detector";

    boolean isServiceStopped;

    Animation animationCustomView;

    RelativeLayout parentLayout;
    int pLayoutHeight;
    int pLayoutWidth;
    RelativeLayout relativeLayout;
    int rLayoutT;
    int rLayoutB;
    int rLayoutL;
    int rLayoutR;
    int rLayoutHeight;
    int rLayoutWidth;
    LinearLayout childLayout;

    ImageView imageView2;


    private Intent intent;
    private static final String TAG = "SensorEvent";


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ___ instantiate intent ___ \\
        //  Instantiate the intent declared globally - which will be passed to startService and stopService.
        intent = new Intent(this, StepCountingService.class);

        init(); // Call view initialisation method.

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);
        createBarChart();
    }

    // Initialise views.
    public void init() {

        isServiceStopped = true; // variable for managing service state - required to invoke "stopService" only once to avoid Exception.


        // ________________ Service Management (Start & Stop Service). ________________ //
        // ___ start Service & register broadcast receiver ___ \\

        //トグルボタン内の処理
        ServiceBtn = findViewById(R.id.ServiceBtn);
        ServiceBtn.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked){
                        // オンなら
                        if(isChecked){
                            startService(new Intent(getBaseContext(), StepCountingService.class));
                            registerReceiver(broadcastReceiver, new IntentFilter(StepCountingService.BROADCAST_ACTION));
                            isServiceStopped = false;
                        }
                        // オフなら
                        else{
                            unregisterReceiver(broadcastReceiver);
                            stopService(new Intent(getBaseContext(), StepCountingService.class));
                            isServiceStopped = true;
                        }
                    }
                }
        );

        // ___________________________________________________________________________ \\

        // Textviews
        stepCountTxV = (TextView)findViewById(R.id.stepCountTxV);
        stepDetectTxV = (TextView)findViewById(R.id.stepDetectTxV);

        // ImageView
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.mipmap.sneaker);
        imageView2.setImageBitmap(bitmap2);

    }


    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();

        layoutsDimen();

        // Currently, when "onResume" is called, the animation continues from where it left off, but this commented code, retarts animation from the beginning.
        /*
        animationCustomView = (Animation)findViewById(R.id.custom_view);
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                animationStart();
            }
        },1000);
        */

    }

    // Method for monitoring layout dimensions.
    public void layoutsDimen() {
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        Log.d("parent layout Height", String.valueOf(parentLayout.getHeight()));
        Log.d("parent layout Width", String.valueOf(parentLayout.getWidth()));
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        Log.d("Rlayout Bottom-most px", String.valueOf(relativeLayout.getBottom()));
        Log.d("Rlayout Right-most px", String.valueOf(relativeLayout.getRight()));

        /*childLayout = (LinearLayout) findViewById(R.id.childLayout);
        Rect rect = new Rect();
        childLayout.getLocalVisibleRect(rect);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu ,menu);
        return true;
    }

    //aboutを押した際の挙動
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_about) {
            String msg = "Thank you for enjoying Pedometer!";
            Toast toast = Toast.makeText(this,msg,Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // --------------------------------------------------------------------------- \\
    // ___ create Broadcast Receiver ___ \\
    // create a BroadcastReceiver - to receive the message that is going to be broadcast from the Service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call updateUI passing in our intent which is holding the data to display.
            updateViews(intent);
        }
    };
    // ___________________________________________________________________________ \\




    // --------------------------------------------------------------------------- \\
    // ___ retrieve data from intent & set data to textviews __ \\
    private void updateViews(Intent intent) {
        // retrieve data out of the intent.
        countedStep = intent.getStringExtra("Counted_Step");
        DetectedStep = intent.getStringExtra("Detected_Step");
        Log.d(TAG, String.valueOf(countedStep));
        Log.d(TAG, String.valueOf(DetectedStep));

        stepCountTxV.setText("歩数 " + String.valueOf(countedStep));
        //stepDetectTxV.setText("Steps Detected = " + String.valueOf(DetectedStep) + '"');
        stepDetectTxV.setText("");

    }
    // ___________________________________________________________________________ \\


    public void animationStart() {
        Log.v("animation", "start");
        animationCustomView.init();
    }

    // ------------------ 棒グラフ ここから -----------------
    private void createBarChart() {
        BarChart barChart =findViewById(R.id.bar_chart);
        barChart.setDescription("BarChart 説明");

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(true);
        barChart.setDrawGridBackground(true);
        barChart.setDrawBarShadow(false);
        barChart.setEnabled(true);

        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(true);
        barChart.setDoubleTapToZoomEnabled(true);

        barChart.setHighlightEnabled(true);
        barChart.setDrawHighlightArrow(true);
        barChart.setHighlightEnabled(true);

        barChart.setScaleEnabled(true);

        barChart.getLegend().setEnabled(true);

        //X軸周り
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setSpaceBetweenLabels(0);

        barChart.setData(createBarChartData());

        barChart.invalidate();
        // アニメーション
        barChart.animateY(2000, Easing.EasingOption.EaseInBack);
    }

    // BarChartの設定
    private BarData createBarChartData() {
        ArrayList<BarDataSet> barDataSets = new ArrayList<>();

        // X軸
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("1月");
        xValues.add("2月");
        xValues.add("3月");
        xValues.add("4月");
        xValues.add("5月");
        xValues.add("6月");

        // valueA
        ArrayList<BarEntry> valuesA = new ArrayList<>();
        valuesA.add(new BarEntry(300, 0));
        valuesA.add(new BarEntry(200, 1));
        valuesA.add(new BarEntry(300, 2));
        valuesA.add(new BarEntry(400, 3));
        valuesA.add(new BarEntry(500, 4));
        valuesA.add(new BarEntry(600, 5));

        BarDataSet valuesADataSet = new BarDataSet(valuesA, "A");
        valuesADataSet.setColor(ColorTemplate.COLORFUL_COLORS[3]);

        barDataSets.add(valuesADataSet);


        BarData barData = new BarData(xValues, barDataSets);
        return barData;
    }

    // ------------------ 棒グラフ ここまで -----------------

}
