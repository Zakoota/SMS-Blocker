package com.example.ultimateSmsBlocker;

import java.util.Date;

public class Utils {

    public static String dateFromLong (Long v) {
        Date d = new Date ( v );
        String msg = d.toString ();
        String out = msg.substring ( 0, msg.indexOf ( "GMT" ) );
        return out;
    }
}
