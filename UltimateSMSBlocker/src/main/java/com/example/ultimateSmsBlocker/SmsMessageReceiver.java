/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.ultimateSmsBlocker;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.*;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class SmsMessageReceiver extends BroadcastReceiver
{

    private static final String TAG = "SmsMessageReceiver";
    Boolean notify_toggle;
    Boolean block_unknown;
    SharedPreferences settings;
    int rg_code;


    @Override
    public void onReceive (final Context context, Intent intent) {
        settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        rg_code = settings.getInt("rb_set", 1);

        /**
         * case 4: block none
         */
        if (rg_code == 4) {
            return;
        }else{
            notify_toggle = settings.getBoolean("notify_toggle", true);
            block_unknown = settings.getBoolean("block_unknown", false);
            Bundle extras = intent.getExtras();
            //ends if empty message
            if (extras == null) {
                return;
            }

            Object[] pdus = (Object[]) extras.get("pdus");

            for (int i = 0; i < pdus.length; i++) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String fromAddress = message.getOriginatingAddress();
                String fromDisplayName = "";

                Uri uri;
                String[] projection;

                uri = Uri.withAppendedPath(
                        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(fromAddress));

                projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

                // Query the filter URI
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        fromDisplayName = cursor.getString(0);
                    }

                    cursor.close();
                }

                try {

                    List<BlockMessage> block_list;

                    Data dataSource = new Data(context);

                    dataSource.open();

                    block_list = dataSource.getBlockList();

                    List<String> list = new ArrayList<>();

                    for (BlockMessage item :
                            block_list) {
                        list.add(item.getNumber());
                    }
                    if (fromAddress.startsWith("+")) {
                        fromAddress = fromAddress.substring(1);
                    }
                    Boolean isContact = false;
                    if (fromDisplayName.length() > 0) {
                        isContact = true;
                    }
                    switch(rg_code){
                        /**
                         * Case 1: Block the Blocklist or block unknown
                         */
                        case 1:{
                            if (list.contains(fromAddress) || (block_unknown && !isContact)){
                                abortBroadcast();

                                if (notify_toggle) {
                                    if (isContact) {
                                        Toast.makeText(context, "Message blocked from " + fromDisplayName, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Message Blocked from " + fromAddress, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                return;
                            }
                            break;
                        }//case 1 end

                        /**
                         * Case 2: Block if it is not in the Blocklist
                         */
                        case 2:{
                            if(!list.contains(fromAddress)){
                                abortBroadcast();
                                if (notify_toggle) {
                                    if (isContact) {
                                        Toast.makeText(context, "Message blocked from " + fromDisplayName, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Message Blocked from " + fromAddress, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                return;
                            }
                            break;
                        }//case 2 end

                        /**
                         * Case 3:Block all incoming messages
                         */
                        case 3:{
                            abortBroadcast();
                            if (notify_toggle) {
                                if (isContact) {
                                    Toast.makeText(context, "Message blocked from " + fromDisplayName, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Message Blocked from " + fromAddress, Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        }//case 3 end
                    }//switch end

                    /**
                     * save blocked messages to message table
                     */
                    try {
                        int days = settings.getInt("retain_days", 0);
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(message.getTimestampMillis());

                        c.add(Calendar.DATE, days);

                        String expiry_date = c.getTimeInMillis() + "";

                        dataSource.open();

                        if(isContact){
                            fromAddress = fromDisplayName + " ("+fromAddress+")";
                        }

                        Message _message = new Message(fromAddress, message.getMessageBody(), rec_date, expiry_date, 0);

                        dataSource.create(_message);
                    } catch (Exception e) { }
                    Log.i(TAG, "size of global passed size = " + block_list.size());
                } catch (Exception e){
                    Log.i(TAG, "size of blocked list error" + e.toString());
                }
                break;
            }
        }
    }
}
