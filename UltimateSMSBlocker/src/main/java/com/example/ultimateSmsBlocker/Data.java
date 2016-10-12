package com.example.ultimateSmsBlocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raza on 6/12/2016.
 */
public class Data
{

    private static final String LOGTAG = "message";

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    private String[] allColumns = {
            MessagesDbHelper.COLUMN_ID,
            MessagesDbHelper.COLUMN_ADDRESS,
            MessagesDbHelper.COLUMN_BODY,
            MessagesDbHelper.COLUMN_RECEIVE_DATE,
            MessagesDbHelper.COLUMN_RETAIN_DATE,
    };

    /**
     * get connection to database
     *
     * @param context
     */
    public Data (Context context) {
        dbHelper = new MessagesDbHelper ( context );
        db = dbHelper.getWritableDatabase ();
    }

    /**
     * get connection to database
     */
    public void open () {
        db = dbHelper.getWritableDatabase ();
        Log.i ( LOGTAG, "db opened" );

    }

    /**
     * close database connection
     */
    public void close () {
        dbHelper.close ();
        Log.i ( LOGTAG, "db closed" );
    }

    /**
     * create new message and insert into table messages
     * @param message
     * @return
     */
    public Message create (Message message) {
        try {
            ContentValues values = new ContentValues();
            values.put(MessagesDbHelper.COLUMN_ADDRESS, message.getAddress());
            values.put(MessagesDbHelper.COLUMN_BODY, message.getBody());
            values.put(MessagesDbHelper.COLUMN_RECEIVE_DATE, message.getReceiveDate());
            values.put(MessagesDbHelper.COLUMN_RETAIN_DATE, message.getRetainDate());
            int insertid = (int) db.insert(MessagesDbHelper.TABLE_MESSAGES, null, values);

            values.put(MessagesDbHelper.COLUMN_ID, insertid);

            message.setId(insertid);

        }catch (Exception e){
            Log.d(LOGTAG, "asdads");
        }
        return message;
    }

    /**
     * delete a blocked message from messages table
     *
     * @param msg
     * @return
     */
    public boolean deleteBlocked (Message msg) {
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

    /**
     * get all blocked messages from messages table and return as message list
     * @return
     */
    public List<Message> findAll () {


        List<Message> messages = new ArrayList<Message> ();

        Cursor cursor = db.query ( MessagesDbHelper.TABLE_MESSAGES, allColumns, null, null, null, null, null );

        Log.i ( LOGTAG, "Retured rows :" + cursor.getCount () );

        if (cursor.getCount () > 0) {
            while (cursor.moveToNext ()) {
                Message message = new Message ();
                message.setId ( cursor.getInt ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_ID ) ) );
                message.setAddress ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_ADDRESS ) ) );
                message.setBody ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_BODY ) ) );


                String msg_date = ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_RECEIVE_DATE ) ) );
                message.setReceiveDate ( msg_date );
                message.setRetainDate ( cursor.getString ( cursor.getColumnIndex ( MessagesDbHelper.COLUMN_RETAIN_DATE ) ) );
                messages.add ( message );
            }
        }
        return messages;
    }

    /**
     * add a number to blocklist table
     */
    public boolean addToBlockList (String address, String name, String added_date) {
        ContentValues values = new ContentValues ();
        values.put ( MessagesDbHelper.COLUMN_ADDRESS, address );
        values.put ( MessagesDbHelper.COLUMN_NAME, name );
        values.put ( MessagesDbHelper.COLUMN_ADDED_DATE, added_date );

        long result = db.insert ( MessagesDbHelper.TABLE_BLOCK_LIST, null, values );

        return ( result != -1 );
    }

    /**
     * ger a number and delete it from table of blocklist
     * @param _address
     * @return
     */
    public boolean removeFromBlockList (String _address) {

        String where = MessagesDbHelper.COLUMN_ADDRESS + "='" + _address + "'";
        int result = db.delete ( MessagesDbHelper.TABLE_BLOCK_LIST, where, null );
        return ( result == 1 );

    }

    /**
     * get all blocklist from Table blocklist and return it as list
     * @return
     */
    public List<BlockMessage> getBlockList () {

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
            }
        }
        return list;
    }

    /**
     * checks if blocklist table is empty
     */
    public boolean isBlockListEmpty(){
        List<BlockMessage> msgs = this.getBlockList();
        if (msgs.isEmpty()){
            return true;
        }
        return false;
    }
}
