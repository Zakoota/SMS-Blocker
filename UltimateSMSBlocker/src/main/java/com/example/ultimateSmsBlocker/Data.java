package com.example.ultimateSmsBlocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 6/12/2016.
 */
public class Data
{

    private static final String LOGTAG = "message";

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    private String[] allColumns = {
            MessagesDbHelper.COLUMN_ID,
            MessagesDbHelper.COLUMN_ADDRESS,
            MessagesDbHelper.COLUMN_BODY,
            MessagesDbHelper.COLUMN_RECEIVE_DATE,
            MessagesDbHelper.COLUMN_RETAIN_DATE,
    };

    public Data (Context context) {
        dbHelper = new MessagesDbHelper ( context );
        db = dbHelper.getWritableDatabase ();
    }

    public void open () {
        db = dbHelper.getWritableDatabase ();
        Log.i ( LOGTAG, "db opened" );

    }

    public void close () {
        dbHelper.close ();
        Log.i ( LOGTAG, "db closed" );
    }

    public Message create (Message message) {
        ContentValues values = new ContentValues ();
        values.put ( MessagesDbHelper.COLUMN_ADDRESS, message.getAddress () );
        values.put ( MessagesDbHelper.COLUMN_BODY, message.getBody () );
        values.put ( MessagesDbHelper.COLUMN_RECEIVE_DATE, message.getReceiveDate () );
        values.put ( MessagesDbHelper.COLUMN_RETAIN_DATE, message.getRetainDate () );

        long insertid = db.insert ( MessagesDbHelper.TABLE_MESSAGES, null, values );

        values.put ( MessagesDbHelper.COLUMN_ID, insertid );

        message.setId ( insertid );
        return message;
    }

    public boolean MoveMessageToInbox (Message msg) {
        boolean response = false;
        try {
            String where = MessagesDbHelper.COLUMN_ID + "=" + msg.getId ();
            int result = db.delete ( MessagesDbHelper.TABLE_MESSAGES, where, null );
            response = ( result == 1 );
            return response;
        } catch (Exception e) {
            Log.i ( LOGTAG, e.toString () + "error happen in deleting" );
        }
        return response;
    }

    public List<Message> findAll () {


        List<Message> messages = new ArrayList<Message> ();

        Cursor cursor = db.query ( MessagesDbHelper.TABLE_MESSAGES, allColumns, null, null, null, null, null );

        Log.i ( LOGTAG, "Retured rows :" + cursor.getCount () );

        if (cursor.getCount () > 0) {
            while (cursor.moveToNext ()) {
                Message message = new Message ();
                message.setId ( cursor.getLong ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_ID ) ) );
                message.setAddress ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_ADDRESS ) ) );
                message.setBody ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_BODY ) ) );


                String msg_date = ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_RECEIVE_DATE ) ) );

//                Date date = new Date ( msg_date );
//                String formattedDate = new SimpleDateFormat ( "dd-MM-yyyy" ).format ( date );

                message.setReceiveDate ( msg_date );
                message.setRetainDate ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_RETAIN_DATE ) ) );
                messages.add ( message );
            }
        }
        return messages;
    }

    public boolean addToBlockList (String address, String name, String added_date) {
        ContentValues values = new ContentValues ();
        values.put ( MessagesDbHelper.COLUMN_ADDRESS, address );
        values.put ( MessagesDbHelper.COLUMN_NAME, name );
        values.put ( MessagesDbHelper.COLUMN_ADDED_DATE, added_date );

        long result = db.insert ( MessagesDbHelper.TABLE_BLOCK_LIST, null, values );

        return ( result != -1 );
    }

    public boolean removeFromBlockList (String _address) {

        String where = MessagesDbHelper.COLUMN_ADDRESS + "='" + _address + "'";
        int result = db.delete ( MessagesDbHelper.TABLE_BLOCK_LIST, where, null );
        return ( result == 1 );

    }

    public List<BlockMessage> getBlockList () {
//        List<String> list = new ArrayList<String> ();

        List<BlockMessage> list = new ArrayList<BlockMessage> ();

        String columns[] = { "address", MessagesDbHelper.COLUMN_ADDED_DATE, MessagesDbHelper.COLUMN_NAME };
        Cursor cursor = db.query ( MessagesDbHelper.TABLE_BLOCK_LIST, columns, null, null, null, null, null );
        Log.i ( LOGTAG, "Retured rows :" + cursor.getCount () );

        if (cursor.getCount () > 0) {
            while (cursor.moveToNext ()) {
                BlockMessage msg = new BlockMessage ();
                String address = ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_ADDRESS ) ) );
                String added_date = ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_ADDED_DATE ) ) );
                String name = ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_NAME ) ) );

                msg.setName ( name );
                msg.setDate ( added_date );
                msg.setNumber ( address );
                list.add ( msg );
                //list.add ( name + "\n" + address + "\n" + added_date );
            }
        }
        return list;
    }

}
