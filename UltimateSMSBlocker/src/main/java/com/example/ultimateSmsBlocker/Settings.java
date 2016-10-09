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
import android.widget.ToggleButton;

/**
 * Created by Raza on 7/4/2016.
 */
public class Settings extends Activity
{

    ToggleButton tgl2;
    ToggleButton tgl;
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

        Boolean toggle2 = settings.getBoolean ( "delete_unknown", false );
        Boolean toggle = settings.getBoolean("notify_toggle", true);

        editor = settings.edit ();

        tgl = (ToggleButton) findViewById(R.id.btn_tgl);
        tgl.setChecked(toggle);
        tgl.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener ()
        {
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
            {
                Boolean v = tgl.isChecked ();

                editor.putBoolean ( "notify_toggle", v );
                editor.commit ();
            }
        } );


        tgl2 = (ToggleButton) findViewById ( R.id.btn_tgl2 );

        tgl2.setChecked ( toggle2 );

        tgl2.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener ()
        {
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
            {
                Boolean v = tgl2.isChecked ();

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
                        Toast.makeText ( getApplicationContext (), "Invalid number of days, setting max value of 1000 days", Toast.LENGTH_LONG ).show ();
                    } else if (days < 0)
                    {
                        Toast.makeText ( getApplicationContext (), "Invalid Input", Toast.LENGTH_LONG ).show ();
                    }
                    if (days > 0)
                    {
                        Toast.makeText ( getApplicationContext (), "Auto delete set to " + days + " days", Toast.LENGTH_LONG ).show ();
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
        tgl2.setChecked ( temp_check );
    }
}
