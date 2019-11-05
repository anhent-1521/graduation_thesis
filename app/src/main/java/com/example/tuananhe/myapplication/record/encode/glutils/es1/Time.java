package com.example.tuananhe.myapplication.record.encode.glutils.es1;

import android.annotation.SuppressLint;
import android.os.SystemClock;

public class Time {

    public static boolean prohibitElapsedRealtimeNanos = true;

    private static Time sTime;
    static {
        reset();
    }

    public static long nanoTime() {
        return sTime.timeNs();
    }

    public static void reset() {
        sTime = new Time();
    }

    private Time() {
    }

    @SuppressLint("NewApi")
    private static class TimeJellyBeanMr1 extends Time {
        public long timeNs() {
            return SystemClock.elapsedRealtimeNanos();
        }
    }

    protected long timeNs() {
        return System.nanoTime();
    }
}
