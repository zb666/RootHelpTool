package com.example.mechrevo.roothelptool;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class TestAnnotation {

    public static final int STATUS_ON = 1;
    public static final int STATUS_OFF = 2;
    public static  int sStatus = STATUS_ON;

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER,ElementType.METHOD,ElementType.FIELD})
    @IntDef({STATUS_ON,STATUS_OFF})
    @interface Status{}

    @Status
    public static int getStatus(){
        return sStatus;
    }

    public static void setStatus(@Status int status){
        sStatus = status;
    }

}
