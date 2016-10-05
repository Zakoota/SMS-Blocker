package com.example.ultimateSmsBlocker;

import android.app.ListActivity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by user on 6/16/2016.
 */
public class AddMessage extends ListActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        String list[] = { "Add Message" };

        setListAdapter ( new ArrayAdapter<String> ( getApplicationContext (), android.R.layout.simple_list_item_1, list ) );


    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        super.onListItemClick ( l, v, position, id );
        try {
            ContentValues values = new ContentValues ();
            values.put ( "address", "+923359110795" );//sender name
            values.put ( "body", "this is my text" );
            getContentResolver ().insert ( Uri.parse ( "content://sms/inbox" ), values );
        } catch (Exception e) {
            Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
        }
    }
}
