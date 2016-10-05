package com.example.ultimateSmsBlocker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Raza on 7/4/2016.
 */
public class Settings extends Activity
{

    CheckBox cb;
    Button btn;
    EditText et;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.settings );

        settings = getApplicationContext ().getSharedPreferences ( "settings",
                this.MODE_PRIVATE );

        Boolean temp_check = settings.getBoolean ( "delete_unknown", false );

        editor = settings.edit ();

        cb = (CheckBox) findViewById ( R.id.cb );

        cb.setChecked ( temp_check );

        cb.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener ()
        {
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
            {
                Boolean v = cb.isChecked ();

                editor.putBoolean ( "delete_unknown", v );
                editor.commit ();
            }
        } );


        et = (EditText) findViewById ( R.id.et_days );

        btn = (Button) findViewById ( R.id.btn_save );

        btn.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick (View v)
            {
                int days = -1;
                try
                {
                    if (et.getText ().length () > 0)
                    {
                        try
                        {
                            days = Integer.parseInt ( et.getText ().toString () );
                        } catch (Exception e)
                        {
                            Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
                        }
                    }

                    if (days > 1000)
                    {
                        days = 1000;
                        Toast.makeText ( getApplicationContext (), "1000 days set , its max limit", Toast.LENGTH_LONG ).show ();
                    } else if (days < 0)
                    {
                        Toast.makeText ( getApplicationContext (), "Invalid Input", Toast.LENGTH_LONG ).show ();
                    }
                    if (days > 0)
                    {
                        Toast.makeText ( getApplicationContext (), "Auto delete set to " + days + "days", Toast.LENGTH_LONG ).show ();
                        editor = settings.edit ();
                        editor.putInt ( "retain_days", days );
                        editor.commit ();
                    }

                } catch (Exception e)
                {
                    Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
                }
            }
        } );

    } // oncreate

    @Override
    protected void onResume ()
    {
        super.onResume ();
        settings = getApplicationContext ().getSharedPreferences ( "settings",
                this.MODE_PRIVATE );

        Boolean temp_check = settings.getBoolean ( "delete_unknown", false );
        cb.setChecked ( temp_check );
    }
}
