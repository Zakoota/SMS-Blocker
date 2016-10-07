package com.example.ultimateSmsBlocker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import static android.content.ContentValues.TAG;

public class BlockMessagesList extends ListActivity
{

    ArrayAdapter<Message> adapter;
    List<Message> messages_list;
    Data dataSource;

    @Override
    protected void onRestart ()
    {
        super.onRestart ();
        refresh ();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );

        try
        {
            dataSource = new Data ( getApplicationContext () );
            messages_list = dataSource.findAll ();
            adapter = new ArrayAdapter<Message> ( getApplicationContext (), R.layout.tv, messages_list );
            setListAdapter ( adapter );
        } catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onListItemClick (ListView l, View v, final int position, long id)
    {
        super.onListItemClick ( l, v, position, id );
        AlertDialog.Builder msg = new AlertDialog.Builder ( this );
        msg.setTitle ( "Select Option" );

        msg.setPositiveButton ( "Move to Inbox", new DialogInterface.OnClickListener ()
        {
            @Override
            public void onClick (DialogInterface dialog, int which)
            {

                Data dataSource = new Data ( getApplicationContext () );

                List<Message> messages_list;

                dataSource.open ();

                messages_list = dataSource.findAll ();

                ContentValues values = new ContentValues ();
                values.put ( "address", messages_list.get ( position ).getAddress () );//sender name
                values.put ( "body", messages_list.get ( position ).getBody () );
                values.put ( "date", messages_list.get ( position ).getReceiveDate () );
                getContentResolver ().insert ( Uri.parse ( "content://sms/inbox" ), values );

                dataSource.open ();
                dataSource.MoveMessageToInbox ( messages_list.get ( position ) );
                refresh ();

            }
        } );


        msg.setNegativeButton ( "Cancel", new DialogInterface.OnClickListener ()
        {
            @Override
            public void onClick (DialogInterface dialog, int which)
            {

            }
        } );

        msg.show ();
    }

    private void refresh ()
    {
        messages_list = dataSource.findAll ();
        adapter = new ArrayAdapter<Message> ( getApplicationContext (), R.layout.tv, messages_list );
        setListAdapter ( adapter );
    }


}
