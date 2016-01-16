package com.example.shambhavipunja.weatherservice;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.example.model.WeatherInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


/**
 * Created by shambhavipunja on 1/12/16.
 */
public class WeatherIntentService extends IntentService {
    private static final String TAG = "MyActivity";
    public static final String GET_WEATHER = "weather";
    public static final String WEATHER_DATA = "wdata";
    public static final String PREV_WEATHER_DATA = "prev_wdata";

    public WeatherIntentService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if(GET_WEATHER.equals(action)){

            Info result = null;//Parcelable class
            try {
                //json data from API
                String out_data = getData("http://api.openweathermap.org/data/2.5/weather?zip=90007&units=metric&appid=5d62949ed666358c95d7442961b25c05");

                //create and extract data from JSON object
                JSONObject object = new JSONObject(out_data);
                JSONObject main = new JSONObject(object.getString("main"));
                String temp = main.getString("temp");
                String city = object.getString("name");
                Log.v("TEMP CITY", temp + city);

                //Set data in result object
                result = new Info();
                result.setTemp(temp);
                result.setCity(city);
                Date date = new Date(System.currentTimeMillis());
                result.setTimestamp(date);

            }
            catch(JSONException e){
            }
            catch (IOException e) {
            }

            Info prev_result = DatabaseOpeartion(result);
            //send broadcast
            Intent bintent = new Intent();
            bintent.setAction(MainActivity.ResponseReciever.ACTION_RESP);
            bintent.putExtra(WEATHER_DATA, result);
            bintent.putExtra(PREV_WEATHER_DATA,prev_result);
            sendBroadcast(bintent);

        }
    }

    private Info DatabaseOpeartion(Info result){
        DaoConnection DaoC = DaoConnection.getDaoCon(this);

        //Save current session data
        Long id = DaoC.Insert(result);
        Log.d("DaoExample", "Inserted new weatherInfo, ID: " + id);

        //Retrieve prev session data
        Info prev_result = new Info();
        if (id >= -1) {
            Long prev_id = id - 1;
            prev_result= DaoC.GetRowById(prev_id);
        }

        DaoC.closeDao();
        return prev_result;

    }

    //Get data from API return: response in string format
    private String getData(String urls) throws IOException {
        InputStream is = null;

        try {

            URL url = new URL(urls);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            Log.v(TAG, "The response is: " + contentAsString);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.

        } finally {
            if (is != null) {
                is.close();
            }
        }

    }

    //Convert stream to string
    private String readIt(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String rLine = "";
        while ((rLine = reader.readLine()) != null) {
            result.append(rLine);

        }
        return result.toString();
    }
}


