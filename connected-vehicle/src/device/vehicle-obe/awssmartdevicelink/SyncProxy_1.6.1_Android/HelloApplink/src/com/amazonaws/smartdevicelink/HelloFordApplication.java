package com.amazonaws.smartdevicelink;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.amazonaws.com.amazonaws.model.Car;

/**
 * Created by sanjoyg on 9/25/15.
 */
public class HelloFordApplication extends MultiDexApplication {

    private Car car;
    private MainActivity mainActivity;
    private MqttConnection mqttConnection;
    private String topicPublish = "vehicle/";

    @Override
    public void onCreate() {
        super.onCreate();
        car = new Car(this);
        mqttConnection = new MqttConnection();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public MqttConnection getMqttConnection() {
        return mqttConnection;
    }

    public void setMqttConnection(MqttConnection mqttConnection) {
        this.mqttConnection = mqttConnection;
    }

    public String getTopicPublish() {
        return topicPublish;
    }

    public void setTopicPublish(String topicPublish) {
        this.topicPublish = topicPublish;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Car getCar() {
        if(car == null){
            car = new Car(this);
        }
        return car;
    }
}
