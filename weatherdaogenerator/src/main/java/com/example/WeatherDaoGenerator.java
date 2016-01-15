package com.example;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.DaoGenerator;

public class WeatherDaoGenerator {
public static void main(String args[])throws Exception{

    Schema schema = new Schema(1, "com.example.model");
    Entity weatherInfo = schema.addEntity("WeatherInfo");
    weatherInfo.addIdProperty();
    weatherInfo.addDateProperty("timestamp").notNull();
    weatherInfo.addStringProperty("city");
    weatherInfo.addStringProperty("temperature");


    new DaoGenerator().generateAll(schema,"../app/src/main/java");

}
}
