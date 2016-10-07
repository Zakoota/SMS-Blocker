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
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmsMessageReceiver extends BroadcastReceiver
{

    private static final String TAG = "SmsMessageReceiver";
    boolean isMatch;
    Boolean block_unknown;
    SharedPreferences settings;


    @Override
    public void onReceive (final Context context, Intent intent)
    {
        settings = context.getSharedPreferences ( "settings",
                context.MODE_PRIVATE );

        block_unknown = settings.getBoolean ( "delete_unknown", false );
        isMatch = false;

        Bundle extras = intent.getExtras ();
        if (extras == null)
        {
            return;
        }

        Object[] pdus = (Object[]) extras.get ( "pdus" );

        for (int i = 0; i < pdus.length; i++)
        {
            SmsMessage message = SmsMessage.createFromPdu ( (byte[]) pdus[ i ] );
            String fromAddress = message.getOriginatingAddress ();
            String fromDisplayName = "";

            Uri uri;
            String[] projection;

            uri = Uri.withAppendedPath (
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode ( fromAddress ) );

            projection = new String[]{ ContactsContract.PhoneLookup.DISPLAY_NAME };

            // Query the filter URI
            Cursor cursor = context.getContentResolver ().query ( uri, projection, null, null, null );
            if (cursor != null)
            {
                if (cursor.moveToFirst ())
                {
                    fromDisplayName = cursor.getString ( 0 );
                }

                cursor.close ();
            }

            try
            {

                List<BlockMessage> block_list;

                Data dataSource = new Data ( context );

                dataSource.open ();

                block_list = dataSource.getBlockList ();

                List<String> list = new ArrayList<> ();

                for (BlockMessage item :
                        block_list)
                {
                    list.add ( item.getNumber () );
                }
                if (fromAddress.startsWith ( "+" ))
                {
                    fromAddress = fromAddress.substring ( 1 );
                }

                Boolean isContact = false;


                if (fromDisplayName.length()>0)
                {
                    isContact = true;
                }

                // checking from contacts
                // if condition checks if it is Blocklist or (block if it is not contact and block unknown is checked)

                if (( list.contains ( fromAddress ) ) || (block_unknown && !isContact ))
                {
                    abortBroadcast();

                    if(isContact){
                        Toast.makeText(context, "Message blocked from " + fromDisplayName, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "Message Blocked from " + fromAddress, Toast.LENGTH_SHORT).show();
                    }

                    try
                    {
                        int days = settings.getInt ( "retain_days", 0 );

                        if (days < 0)
                        {
                            days = 1;
                        }else if (days > 1000)
                        {
                            days = 1000;
                        }

                        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat ( context );
//                                    String s = dateFormat.format ( ( 1466596894376L ) );
                        String s = dateFormat.format ( ( message.getTimestampMillis () ) );

                        Date d = new Date ( s );

                        Calendar c = Calendar.getInstance ();
                        c.setTime ( d );
                        c.add ( Calendar.DATE, days );

                        String input_date = c.getTimeInMillis () + "";

                        dataSource.open ();

                        Message _message = new Message ( fromAddress, message.getMessageBody (), ( message.getTimestampMillis () + "" ), input_date, 0 );

                        dataSource.create ( _message );
                    } catch (Exception e)
                    {
                        Toast.makeText ( context, e.toString (), Toast.LENGTH_LONG ).show ();
                    }

                }

                AlertDialog.Builder builder = new AlertDialog.Builder ( context ).setTitle ( fromDisplayName );
                builder.show();
                Log.i ( TAG, "size of global passed size = " + block_list.size () );


            } catch (Exception e)
            {

                Log.i ( TAG, "size of blocked list error" + e.toString () );
            }
            break;
        }
    }
}
