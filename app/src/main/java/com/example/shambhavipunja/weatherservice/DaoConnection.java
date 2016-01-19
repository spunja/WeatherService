package com.example.shambhavipunja.weatherservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.DaoMaster;
import com.example.model.DaoSession;
import com.example.model.WeatherInfo;
import com.example.model.WeatherInfoDao;

import java.util.List;

/**
 * Created by shambhavipunja on 1/15/16.
 */
public final class DaoConnection {

    public static DaoConnection daoConnection;
    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    private DaoSession daoSession;
    private DaoMaster daoMaster;
    private WeatherInfoDao dao;

    private DaoConnection(Context context){
        String dbName = "weather-db";

        //Start Session
        helper = new DaoMaster.DevOpenHelper(context,dbName,null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getWeatherInfoDao();
    }

    public static synchronized DaoConnection getDaoCon(Context context) {
        if ( daoConnection == null ) {
            daoConnection = new DaoConnection(context);
        }
        return daoConnection;

    }

    public Long Insert(Info info){
        long resultcode = -1;

        if(info != null){
            WeatherInfo weatherInfo = new WeatherInfo(null,info.getTimestamp(),info.getCity(),info.getTemp());
            dao.insert(weatherInfo);
            resultcode = weatherInfo.getId();
        }
        return resultcode;
    }

    public Info GetRowById (Long id) {
        Info info = null;
        List<WeatherInfo> list = dao.queryBuilder()
                .where(WeatherInfoDao.Properties.Id.eq(id)).list();
        if (list.size() > 0){
            WeatherInfo weatherInfo = list.get(0);
            info = new Info();
            info.setTimestamp(weatherInfo.getTimestamp());
            info.setCity(weatherInfo.getCity());
            info.setTemp(weatherInfo.getTemperature());
        }
        return info;
    }

    //Close db
    public void closeDao(){
        if(daoSession!=null)daoSession.clear();
        daoMaster=null;
        if(db.isOpen())db.close();
        helper.close();
        daoConnection = null;
    }



}
