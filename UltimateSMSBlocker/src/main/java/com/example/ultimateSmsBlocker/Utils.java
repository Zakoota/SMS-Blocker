package com.example.ultimateSmsBlocker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.StrictMode;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class Utils {

    public static String dateFromLong (Long v) {
        Date d = new Date ( v );
        String msg = d.toString ();

        String out = msg.substring ( 0, msg.indexOf ( "GMT" ) );

        return msg;
    }

    public static void displayText (Activity activity, int id, String text) {
        TextView tv = (TextView) activity.findViewById ( id );
        tv.setText ( text );
    }

    public static String getText (Activity activity, int id) {
        EditText et = (EditText) activity.findViewById ( id );
        return et.getText ().toString ();
    }

    public static boolean getCBChecked (Activity activity, int id) {
        CheckBox cb = (CheckBox) activity.findViewById ( id );
        return cb.isChecked ();
    }

    public static void setCBChecked (Activity activity, int id, boolean value) {
        CheckBox cb = (CheckBox) activity.findViewById ( id );
        cb.setChecked ( value );
    }

    @TargetApi(11)
    public static void enableStrictMode () {
        // Strict mode is only available on gingerbread or later
        if (Utils.hasGingerbread ()) {

            // Enable all thread strict mode policies
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder ()
                            .detectAll ()
                            .penaltyLog ();

            // Enable all VM strict mode policies
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder ()
                            .detectAll ()
                            .penaltyLog ();

            // Honeycomb introduced some additional strict mode features
            if (Utils.hasHoneycomb ()) {
                // Flash screen when thread policy is violated
                threadPolicyBuilder.penaltyFlashScreen ();
                // For each activity class, set an instance limit of 1. Any more instances and
                // there could be a memory leak.
//                vmPolicyBuilder
//                        .setClassInstanceLimit(ContactsListActivity.class, 1)
//                        .setClassInstanceLimit(ContactDetailActivity.class, 1);
            }

            // Use builders to enable strict mode policies
            StrictMode.setThreadPolicy ( threadPolicyBuilder.build () );
            StrictMode.setVmPolicy ( vmPolicyBuilder.build () );
        }
    }

    /**
     * Uses static final constants to detect if the device's platform version is Gingerbread or
     * later.
     */
    public static boolean hasGingerbread () {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb or
     * later.
     */
    public static boolean hasHoneycomb () {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or
     * later.
     */
    public static boolean hasHoneycombMR1 () {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Uses static final constants to detect if the device's platform version is ICS or
     * later.
     */
    public static boolean hasICS () {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
}
