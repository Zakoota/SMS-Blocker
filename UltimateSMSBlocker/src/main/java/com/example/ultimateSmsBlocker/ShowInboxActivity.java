package com.example.ultimateSmsBlocker;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShowInboxActivity extends ListActivity
{

    MyTask task;
    List<String> tmp;
    String address;
    List<String> list;
    List<SMSData> smsList;
    ArrayAdapter adapter;
    String name;
    String added_date;
    ListView listView;
    Data dataSource;
    CheckedTextView ctv;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );

        ctv = (CheckedTextView) findViewById ( R.id.checked_tv );

        list = new ArrayList<String> ();

        smsList = new ArrayList<SMSData> ();

        adapter = new ArrayAdapter<String> ( getApplicationContext (), R.layout.checked_tv
                , list );
        setListAdapter ( adapter );


        task = new MyTask ();
        task.execute ( "s" );

        listView = getListView ();

        listView.setChoiceMode ( ListView.CHOICE_MODE_MULTIPLE );

    }

    boolean tryParseLong (String value)
    {
        try
        {
            Long.parseLong ( value );
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    @Override
    public void onBackPressed ()
    {
        super.onBackPressed ();
        task.cancel ( true );
        finish ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        super.onCreateOptionsMenu ( menu );
        MenuInflater inflater = getMenuInflater ();
        inflater.inflate ( R.menu.inbox, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {

        if (item.getItemId () == R.id.btn_del)
        {

            listView = getListView ();

            int len = listView.getCount ();
            SparseBooleanArray checked = listView.getCheckedItemPositions ();

            List<Integer> checkedItems = new ArrayList<> ();

            String line = "";

            for (int i = 0; i < len; i++)

            {
                if (checked.get ( i ))
                {
                    checkedItems.add ( i );
                    line += i;
                }
            }

            for (Integer i :
                    checkedItems)
            {
                SMSData sms = (SMSData) smsList.get ( i );

                try
                {
                    address = sms.getNumber ();
                    name = sms.getName ();

                    if (address.contains ( "+" ))
                    {
                        address = address.substring ( 1 );
                    }
                    address = address.replaceAll("\\s+","");
                    address = address.replaceAll("-","");

                    Calendar c = Calendar.getInstance ();
                    SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yyyy" );
                    added_date = df.format ( c.getTime () );

                    if (name == null)
                    {
                        name = "unknown No.";
                    }

                    dataSource = new Data ( getApplicationContext () );

                    dataSource.open ();

                    boolean isAdded = dataSource.addToBlockList ( address, name, added_date );
                    if (isAdded)
                    {
                        Toast.makeText ( getApplicationContext (), "Blocked", Toast.LENGTH_LONG ).show ();
                    } else
                    {
                        Toast.makeText ( getApplicationContext (), "Already blocked", Toast.LENGTH_LONG ).show ();
                    }
                    dataSource.close ();

                } catch (Exception e)
                {
                    Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
                }
            }
        }
        return super.onOptionsItemSelected ( item );
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();
        listView.clearChoices ();
    }

    class MyTask extends android.os.AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute ()
        {
            super.onPreExecute ();
        }

        @Override
        protected void onPostExecute (String values)
        {
            adapter.notifyDataSetChanged ();
            listView.setItemsCanFocus ( false );


        }

        @Override
        protected void onCancelled ()
        {
            super.onCancelled ();
            finish ();
        }

        @Override
        protected void onProgressUpdate (String... values)
        {
            list.add ( values[ 0 ] );
            adapter.notifyDataSetChanged ();
        }

        @Override
        protected String doInBackground (String... params)
        {


            try
            {


                tmp = new ArrayList<String> ();

                Uri uri = Uri.parse ( "content://sms/inbox" );
                Cursor c = getContentResolver ().query ( uri, null, null, null, null );
                startManagingCursor ( c );

                // Read the sms data and store it in the list
                if (c.moveToNext ())
                {

                    for (int i = 0; i < c.getCount (); i++)
                    {
                        SMSData sms = new SMSData ();
                        sms.setBody ( c.getString ( c.getColumnIndexOrThrow ( "body" ) ).toString () );

                        String num = ( c.getString ( c.getColumnIndexOrThrow ( "address" ) ).toString () );
                        String date = null;
                        int date_index;
                        try
                        {

                            date_index = ( c.getColumnIndexOrThrow ( "date" ) );

                            date = c.getString ( date_index );
                        } catch (Exception e)
                        {
//                    Toast.makeText ( ShowInboxActivity.this, e.toString (), Toast.LENGTH_LONG ).show ();
                        }
                        sms.setNumber ( num );
                        try
                        {
                            if (tryParseLong ( date ))
                            {
                                sms.setDate ( Long.parseLong ( date ) );
                            }

                            Uri lookupUri = Uri.withAppendedPath ( ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode ( num ) );
                            Cursor cc = getContentResolver ().query ( lookupUri, new String[]{ ContactsContract.Data.DISPLAY_NAME }, null, null, null );
                            try
                            {
                                cc.moveToFirst ();
                                String displayName = cc.getString ( 0 );
                                sms.setName ( displayName );

                            } catch (Exception e)
                            {
//                    Toast.makeText ( ShowInboxActivity.this, e.toString (), Toast.LENGTH_LONG ).show ();
//                    break;
                            }


                        } catch (Exception e)
                        {
                            //Toast.makeText ( getApplicationContext (), "falied to parse", Toast.LENGTH_LONG ).show ();
                        }

//

                        if (!( tmp.contains ( num ) ))
                        {
                            smsList.add ( sms );
                            publishProgress ( sms.toString () );
                        } else
                        {

                        }

                        tmp.add ( num );

                        c.moveToNext ();


                    }
                }
//                c.close ();


            } catch (Exception e)
            {
                Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
            }


            return ( "" );
        }

    } // end of class
}
