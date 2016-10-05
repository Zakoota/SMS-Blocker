package com.example.ultimateSmsBlocker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MessagesDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_MESSAGES = "messages";

    public static final String TABLE_BLOCK_LIST = "block_list";

    public static final String COLUMN_NAME = "_name";

    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_RECEIVE_DATE = "receive_date";
    public static final String COLUMN_ADDED_DATE = "added_date";
    public static final String COLUMN_RETAIN_DATE = "retain_date";

    private static final String DATABASE_NAME = "messages.db";

    private static final int DATABASE_VERSION = 1;
    private static final String LOGTAG = "USMS";


    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_MESSAGES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ADDRESS + " TEXT, " +
                    COLUMN_BODY + " TEXT, " +
                    COLUMN_RECEIVE_DATE + " TEXT, " +
                    COLUMN_RETAIN_DATE + " TEXT " +
                    ")";

    private static final String TABLE_CREATE_2 =
            "CREATE TABLE " + TABLE_BLOCK_LIST + " (" +
                    COLUMN_ADDRESS + " TEXT PRIMARY KEY, " +
                    COLUMN_ADDED_DATE + " TEXT, " +
                    COLUMN_NAME + " TEXT " +
                    ")";


    public MessagesDbHelper (Context context) {
        super ( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate (SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL ( TABLE_CREATE );
        sqLiteDatabase.execSQL ( TABLE_CREATE_2 );
        Log.i ( LOGTAG, "table has been created" );

    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL ( "DROP TABLE IF EXISTS " + TABLE_MESSAGES );
        sqLiteDatabase.execSQL ( "DROP TABLE IF EXISTS " + TABLE_CREATE_2 );
        Log.i ( LOGTAG, "db updated" );
        onCreate ( sqLiteDatabase );
    }
}
