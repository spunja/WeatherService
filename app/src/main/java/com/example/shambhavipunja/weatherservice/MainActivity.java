package com.example.shambhavipunja.weatherservice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private ResponseReciever reciever;
    public static final String GET_WEATHER = "weather";
    //public static final int CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        IntentFilter filter = new IntentFilter(ResponseReciever.ACTION_RESP);
        reciever = new ResponseReciever();
        registerReceiver(reciever, filter);

        Intent intent = new Intent(this, WeatherIntentService.class);
        intent.setAction(GET_WEATHER);
        startService(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(reciever);
        super.onDestroy();


    }

    public class ResponseReciever extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.intent.action.WEATHER_DATA";
        final String DEGREE = "\u00b0";

        @Override
        public void onReceive(Context context, Intent intent) {
            //task
            if (ACTION_RESP.equals(intent.getAction())) {


                Info result = intent.getParcelableExtra(WeatherIntentService.WEATHER_DATA);
                Info prev = intent.getParcelableExtra(WeatherIntentService.PREV_WEATHER_DATA);

                TextView temp = (TextView) findViewById(R.id.temperature_text);
                TextView city = (TextView) findViewById(R.id.city_text);
                TextView prev_temp = (TextView) findViewById(R.id.prev_temp);
                TextView prev_stamp = (TextView) findViewById(R.id.prev_stamp);

                if (result != null) {
                    city.setText(result.getCity());
                    temp.setText(result.getTemp() + " " + DEGREE + "C");
                } else {
                    Log.v("Null", "NULL");
                }

                if (prev != null) {
                    prev_temp.setText(prev.getTemp()+ " " + DEGREE + "C");
                    Log.d("res", "temp: " + prev.getTemp());

                    Date timestamp = prev.getTimestamp();
                    prev_stamp.setText(timestamp.toString());
                }
            }

        }
    }


}







