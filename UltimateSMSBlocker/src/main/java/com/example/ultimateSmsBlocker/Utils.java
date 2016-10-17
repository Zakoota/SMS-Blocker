package com.example.ultimateSmsBlocker;

import android.os.Build;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String dateFromString (String v) {
        Long l = Long.parseLong(v);
        Date d = new Date(l);
        DateFormat fmtr = SimpleDateFormat.getDateTimeInstance();
        return fmtr.format(d);
    }
    public static boolean hasHoneycomb () {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
