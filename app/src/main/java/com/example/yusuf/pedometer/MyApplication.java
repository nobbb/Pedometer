package com.example.yusuf.pedometer;

/**
 * Created by 171y012 on 2018/07/23.
 */

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application{
    @Override
    public void onCreate(){
        super.onCreate();

        RealmConfiguration realmconfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmconfig);
    }
}
