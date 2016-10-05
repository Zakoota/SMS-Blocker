
package com.example.ultimateSmsBlocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Raza on 6/24/2016.
 */
public class AddUnknown extends Activity {

    EditText et;
    View et_view;
    Data dataSource;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        dataSource = new Data ( getApplicationContext () );

        dataSource.open ();

        // inflate slideshow_name_edittext.xml to create an EditText
        et_view = getLayoutInflater ().inflate ( R.layout.add_unknown, null );
        et =
                (EditText) et_view.findViewById ( R.id.et_unknown );

        AlertDialog.Builder builder_1 = new AlertDialog.Builder ( AddUnknown.this )
                .setView ( et_view )
                .setTitle ( "Add Unknow Number : " );


        builder_1.setPositiveButton ( "Set", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {


                try {
                    String _input = "";
                    _input = et.getText ().toString ().trim ();

                    if (_input.length () >= 10) {
                        dataSource.open ();
                        String added_date = "";
                        String name = "unknown No.";
                        String address = "";
                        address = _input;

                        Calendar c = Calendar.getInstance ();
                        SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yyyy" );
                        added_date = df.format ( c.getTime () );

                        boolean isAdded = dataSource.addToBlockList ( address, name, added_date );
                        if (isAdded) {
                            Toast.makeText(getApplicationContext(), address + " added to block list", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(), address + " is already blocked", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText ( getApplicationContext (), "Invalid Number", Toast.LENGTH_LONG ).show ();

                    }
                } catch (Exception e) {
                    Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();

                }

            }
        } );
        builder_1.setCancelable ( true );
        builder_1.setCancelable ( true );
        builder_1.show ();
    }
}
