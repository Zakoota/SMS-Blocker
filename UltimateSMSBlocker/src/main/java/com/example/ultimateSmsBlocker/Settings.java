package com.example.ultimateSmsBlocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static com.example.ultimateSmsBlocker.R.id.btn_rb1;
import static com.example.ultimateSmsBlocker.R.id.btn_rb2;
import static com.example.ultimateSmsBlocker.R.id.btn_rb3;
import static com.example.ultimateSmsBlocker.R.id.btn_rb4;
import static com.example.ultimateSmsBlocker.R.id.btn_rb5;

/**
 * Created by Raza on 7/4/2016.
 */
public class Settings extends Activity
{

    private ToggleButton tgl;
    private Button btn;
    private TextView ev;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private View view;
    private EditText et;
    private RadioGroup rg1;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.settings );

        settings = getApplicationContext ().getSharedPreferences ( "settings",
                this.MODE_PRIVATE );

        editor = settings.edit ();

                Boolean toggle = settings.getBoolean("notify_toggle", true);

        /*
         * Notification toggle button
         */
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

        /*
         * TextView for displaying retain_days
         */
        ev = (TextView) findViewById(R.id.tv_days);
        ev.setText(String.valueOf(settings.getInt("retain_days",0)));

        /*
         * RadioGroup code
         */
        rg1 = (RadioGroup) findViewById(R.id.rg1);
        rgSetter(settings.getInt("rb_set", 1));
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                           @Override
                                           public void onCheckedChanged(RadioGroup group, int checkedId) {
                                               rgSetter(rgGetter());
                                           }
                                       });


        /*
         * Button for displaying dialog and change retain_days
         */
        btn = (Button) findViewById(R.id.btn_set);
        btn.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick (View v)
            {
                LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE );
                view = inflater.inflate ( R.layout.days_dialog, null );
                et = (EditText) view.findViewById ( R.id.et_days );
                AlertDialog.Builder inputDialog = new AlertDialog.Builder ( Settings.this );
                inputDialog.setView ( view );
                inputDialog.setTitle ( "Enter days" );
                inputDialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int days = -1;
                        try{
                            if (et.getText ().length () > 0){
                                try{
                                    days = Integer.parseInt ( et.getText ().toString () );
                                }catch(Exception e){
                                    Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
                                }
                            }
                            if (days > 1000)
                            {
                                days = 1000;
                                Toast.makeText ( getApplicationContext (), "Invalid number of days, setting max value of 1000 days", Toast.LENGTH_LONG ).show ();
                            }else if (days < 0){
                                Toast.makeText ( getApplicationContext (), "Invalid Input", Toast.LENGTH_LONG ).show ();
                            }
                            if (days > 0)
                            {
                                Toast.makeText ( getApplicationContext (), "Auto delete set to " + days + " days", Toast.LENGTH_LONG ).show ();
                                editor = settings.edit ();
                                editor.putInt ( "retain_days", days );
                                editor.commit ();
                                ev.setText(String.valueOf(settings.getInt("retain_days",0))+" days");
                            }
                        }catch (Exception e){
                            Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
                        }
                    }
                });
                inputDialog.show();
            }
        } );
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();
        settings = getApplicationContext ().getSharedPreferences ( "settings",
                this.MODE_PRIVATE );
        ev.setText(String.valueOf(settings.getInt("retain_days",0))+" days");
        tgl.setChecked( settings.getBoolean("notify_toggle", false));
        rgSetter(settings.getInt("rb_set", 1));
    }

    /*
     * Radio Group ID getter in simple int
     */
    private int rgGetter(){
        int rbChecked = rg1.getCheckedRadioButtonId();
        int btn=0;
        switch (rbChecked){
            case btn_rb1:{
                btn = 1;
                break;
            }
            case btn_rb2:{
                btn = 2;
                break;
            }
            case btn_rb3:{
                btn = 3;
                break;
            }
            case btn_rb4:{
                btn = 4;
                break;
            }
            case btn_rb5:{
                btn = 5;
                break;
            }
        }
        return btn;
    }

    /*
     * RadioGroup setter in Simple int
     */
    private boolean rgSetter(int i){
        editor = settings.edit();
        boolean isSet = false;
        switch (i){
            case 1:{
                rg1.check(btn_rb1);
                editor.putInt("rb_set", 1);
                isSet = true;
                break;
            }
            case 2:{
                rg1.check(btn_rb2);
                editor.putInt("rb_set", 2);
                isSet = true;
                break;
            }
            case 3:{
                rg1.check(btn_rb3);
                editor.putInt("rb_set", 3);
                isSet = true;
                break;
            }
            case 4:{
                rg1.check(btn_rb4);
                editor.putInt("rb_set", 4);
                isSet = true;
                break;
            }
            case 5:{
                rg1.check(btn_rb5);
                editor.putInt("rb_set", 5);
                isSet = true;
                break;
            }default:{
                isSet = false;
                break;
            }
        }
        editor.commit();
        return isSet;
    }
}
