package com.example.yusuf.pedometer;

/**
 * Created by 171y012 on 2018/07/23.
 */

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Structure extends RealmObject{
    public Date date;
    public int step;

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date=date;
    }

    public int getStep(){
        return step;
    }

    public  void setStep(int step){
        this.step=step;
    }
}
