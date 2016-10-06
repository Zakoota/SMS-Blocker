package com.example.ultimateSmsBlocker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Contacts3 extends Activity {

    private static final String TAG = Contacts3.class.getSimpleName ();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    Data dataSource;
    private Uri uriContact;
    private String contactID;     // contacts unique ID


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        startActivityForResult ( new Intent ( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI ), REQUEST_CODE_PICK_CONTACTS );

        dataSource = new Data ( this );
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        try {

            if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
                Log.d ( TAG, "Response: " + data.toString () );
                uriContact = data.getData ();


                String added_date = "";
                String name = "";
                String address = "";

                name = retrieveContactName ();
                address = retrieveContactNumber ();

                Calendar c = Calendar.getInstance ();
                SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yyyy" );
                added_date = df.format ( c.getTime () );


                boolean isAdded = dataSource.addToBlockList ( address, name, added_date );
                if (isAdded)
                    if(name.length()>0){
                        Toast.makeText ( getApplicationContext (), name+" added to block list", Toast.LENGTH_LONG ).show ();
                    }else{
                        Toast.makeText(getApplicationContext(), address+" added to block list", Toast.LENGTH_LONG).show();
                    }
                else {
                    if(name.length()>0){
                        Toast.makeText(getApplicationContext(), name + " is already blocked", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),address+" is already blocked", Toast.LENGTH_LONG).show();
                    }
                }
                finish ();
            }else{
                finish();
            }
        } catch (Exception e) {
            Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
            finish ();
        }
    }


    private String retrieveContactNumber () {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver ().query ( uriContact,
                new String[]{ ContactsContract.Contacts._ID },
                null, null, null );

        if (cursorID.moveToFirst ()) {

            contactID = cursorID.getString ( cursorID.getColumnIndex ( ContactsContract.Contacts._ID ) );
        }

        cursorID.close ();

        Log.d ( TAG, "Contact ID: " + contactID );

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver ().query ( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ ContactsContract.CommonDataKinds.Phone.NUMBER },

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{ contactID },
                null );

        if (cursorPhone.moveToFirst ()) {
            contactNumber = cursorPhone.getString ( cursorPhone.getColumnIndex ( ContactsContract.CommonDataKinds.Phone.NUMBER ) );
        }

        cursorPhone.close ();

        Log.d ( TAG, "Contact Phone Number: " + contactNumber );

        return contactNumber;
    }

    private String retrieveContactName () {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver ().query ( uriContact, null, null, null, null );

        if (cursor.moveToFirst ()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString ( cursor.getColumnIndex ( ContactsContract.Contacts.DISPLAY_NAME ) );
        }

        cursor.close ();

        Log.d ( TAG, "Contact Name: " + contactName );

        return contactName;

    }
}
