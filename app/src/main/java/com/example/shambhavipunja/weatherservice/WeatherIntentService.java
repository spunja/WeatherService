package com.example.shambhavipunja.weatherservice;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.model.DaoMaster;
import com.example.model.DaoSession;
import com.example.model.WeatherInfo;
import com.example.model.WeatherInfoDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by shambhavipunja on 1/12/16.
 */
public class WeatherIntentService extends IntentService {
    private static final String TAG = "MyActivity";
    public static final String GET_WEATHER = "weather";
    public static final String WEATHER_DATA = "wdata";
    public static final String PREV_WEATHER_DATA = "prev_wdata";

    private SQLiteDatabase db;
    private WeatherInfoDao Dao;

    public WeatherIntentService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if(GET_WEATHER.equals(action)){

            Info result = null;//Parcelable class
            Info prev_result = null;

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

                //Save weather data in database
                String database_name = "weather-db";
                initDB(database_name);

                Date date = new Date(System.currentTimeMillis());
                WeatherInfo weatherInfo = new WeatherInfo(null,date,city,temp);
                Dao.insert(weatherInfo);
                Log.d("DaoExample", "Inserted new weatherInfo, ID: " + weatherInfo.getId());

                List<WeatherInfo> list = Dao.queryBuilder()
                        .where(WeatherInfoDao.Properties.Id.eq(weatherInfo.getId()-1))
                        .list();

                if(list.size() > 0){
                    WeatherInfo w = list.get(0);

                    prev_result = new Info();
                    String prev_temp = w.getTemperature();
                    Date prev_timestamp = w.getTimestamp();

                    prev_result.setTemp(prev_temp);
                    prev_result.setTimestamp(prev_timestamp);

                    Log.d("res", "time: " + prev_result.getTimestamp());
                }
                closeDB();

            }
            catch(JSONException e){
            }
            catch (IOException e) {
            }

            //send broadcast
            Intent bintent = new Intent();
            bintent.setAction(MainActivity.ResponseReciever.ACTION_RESP);
            bintent.putExtra(WEATHER_DATA, result);
            bintent.putExtra(PREV_WEATHER_DATA,prev_result);
            sendBroadcast(bintent);

        }
    }


    //Initializing db session
    private void initDB(String database_name){

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,database_name,null);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        Dao = daoSession.getWeatherInfoDao();
    }

    //Close db
    private void closeDB(){
        db.close();
    }

    //Get data from API return: response in string format
    public String getData(String urls) throws IOException {
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
    public String readIt(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String rLine = "";
        while ((rLine = reader.readLine()) != null) {
            result.append(rLine);

        }
        return result.toString();
    }
}


