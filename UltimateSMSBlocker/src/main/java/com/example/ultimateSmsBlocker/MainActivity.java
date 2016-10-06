package com.example.ultimateSmsBlocker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MainActivity extends ListActivity
{

    private static final String LOGTAG = "USMS";
    private static final int FILE_ID = 1;
    Data dataSource;
    List<BlockMessage> list;
    ArrayAdapter<BlockMessage> adapter;

    SharedPreferences settings;
    View view;
    EditText et_series_1;
    EditText et_series_2;
    List<Message> messages_list;
    ListView listView;




    @Override
    protected void onRestart ()
    {
        super.onRestart ();
        dataSource.open ();
        list = dataSource.getBlockList ();

        listView = getListView ();
        refresh ();
    }


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );

        listView = getListView ();
        // checking if any message to delete from block messages
        dataSource = new Data ( getApplicationContext () );
        dataSource.open ();
        messages_list = dataSource.findAll ();

        int i = 0;
        int total = 0;
        for (Message msg : messages_list)
        {
            try
            {
                SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yyyy" );

                Calendar today_cal = Calendar.getInstance ();
                Calendar retain_cal = Calendar.getInstance ();

                Date check_date = today_cal.getTime ();

                String temp = Utils.dateFromLong ( Long.parseLong ( msg.getRetainDate () ) );

                retain_cal.setTimeInMillis ( Long.parseLong ( msg.getRetainDate () ) );

                if (( today_cal.after ( retain_cal ) ) && ( !today_cal.equals ( retain_cal ) ))
                {
                    total++;
                    dataSource.open ();
                    dataSource.MoveMessageToInbox ( msg );
                }

            } catch (Exception e)
            {
                Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
            }
            i++;
        }
        if (total > 0)
        {
            Toast.makeText ( getApplicationContext (), total + " messages delete from app", Toast.LENGTH_LONG ).show ();
        }
        settings = getApplicationContext ().getSharedPreferences ( "settings",
                this.MODE_PRIVATE );

        int days = settings.getInt ( "retain_days", 0 );

        if (days > 0)
        {
        }


        // checking if any message to delete from block messages


        // series layout
        dataSource.open ();
        List<BlockMessage> blockList = dataSource.getBlockList ();
        list = dataSource.getBlockList ();
        adapter = new ArrayAdapter<BlockMessage> ( getApplicationContext (), R.layout.tv, list );
        setListAdapter ( adapter );

    }

    @Override
    protected void onResume ()
    {
        dataSource.open ();
        list = dataSource.getBlockList ();
//        adapter = new ArrayAdapter<BlockMessage> ( getApplicationContext (), R.layout.tv, list );
        super.onResume ();
        refresh ();
    }


    @Override
    protected void onPause ()
    {
        super.onPause ();
        dataSource.close ();
    }

    @Override
    protected void onStop ()
    {
        super.onStop ();
        dataSource.close ();
    }


    public boolean onCreateOptionsMenu (Menu menu)
    {
        super.onCreateOptionsMenu ( menu );
        MenuInflater inflater = getMenuInflater ();
        inflater.inflate ( R.menu.menu_main, menu );
        return true;
    } // end method onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // create a new Intent to launch the AddEditContact Activity
        switch(item.getItemId()) {

            //case for menu add
            case R.id.add:{
                Intent intent = new Intent ( getApplicationContext (), ShowInboxActivity.class );
                startActivity ( intent );
                break;
            }

            //case for menu add number
            case R.id.add_number:{
                final AlertDialog.Builder builder = new AlertDialog.Builder ( this )
                        .setTitle ( "Choose Option to Add Number" );
                builder.setNegativeButton ( "Contact", new DialogInterface.OnClickListener ()
                {
                    @Override
                    public void onClick (DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent ( getApplicationContext (), Contacts3.class );
                        startActivity ( intent );
                    }
                } );
                builder.setPositiveButton ( "Unknown", new DialogInterface.OnClickListener ()
                {
                    @Override
                    public void onClick (final DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent ( getApplicationContext (), AddUnknown.class );
                        startActivity ( intent );
                    }

                } );


                builder.show ();
                break;
            }

            //case for series menu
            case R.id.series:{
                // create an input dialog to get slideshow name from user
                // series layout
                // get a reference to the LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE );
                // inflate slideshow_name_edittext.xml to create an EditText
                view = inflater.inflate ( R.layout.series, null );
                et_series_1 = (EditText) view.findViewById ( R.id.et_series_1 );
                et_series_2 = (EditText) view.findViewById ( R.id.et_series_2 );


                AlertDialog.Builder inputDialog = new AlertDialog.Builder ( this );
                inputDialog.setView ( view ); // set the dialog's custom View
                inputDialog.setTitle ( "Enter series range" );

                try {
                    inputDialog.setPositiveButton("Add Series", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                String input = et_series_1.getText().toString().trim();

                                String input_2 = et_series_2.getText().toString().trim();

                                long start = Long.parseLong(input);
                                long end = Long.parseLong(input_2);

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String added_date = df.format(c.getTime());

                                for (long i = start; i <= end; i++) {
                                    String address = i + "";
                                    String name = "unknown No.";

                                    dataSource.addToBlockList(address, name, added_date);
                                }
                                refresh();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                    inputDialog.show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                break;
            }

            //case for menu import
            case R.id._import:{
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose file"), FILE_ID);
                break;
            }


            //case for menu export
            case R.id.export:{
                final List<BlockMessage> block_list = dataSource.getBlockList ();

                final String list[] = { ".Txt", ".Csv", ".Xml" };

                AlertDialog.Builder inputDialog = new AlertDialog.Builder ( MainActivity.this );

                inputDialog.setTitle ( "Choose Type of File for Export" )
                        .setItems ( list, new DialogInterface.OnClickListener ()
                        {
                            public void onClick (DialogInterface dialog, int which)
                            {

                                if (which == 0)
                                {
                                    try
                                    {
                                        String baseDir = Environment.getExternalStorageDirectory ().getAbsolutePath ();

                                        String file_path = baseDir + "/" + "exported.txt";

                                        File f = new File ( file_path );

                                        FileOutputStream fos = new FileOutputStream ( f );

                                        String lines = "";
                                        for (BlockMessage element :
                                                block_list)
                                        {
                                            lines += element.getNumber () + "\n";
                                        }

                                        fos.write ( lines.getBytes () );


                                        Toast.makeText ( getApplicationContext (), "File Exported with with path " + file_path, Toast.LENGTH_LONG ).show ();
                                    } catch (Exception e)
                                    {
                                        Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
                                    }
                                } else if (which == 1)
                                {
                                    try
                                    {
                                        String baseDir = Environment.getExternalStorageDirectory ().getAbsolutePath ();

                                        String file_path = baseDir + "/" + "exported.csv";

                                        File f = new File ( file_path );

                                        FileOutputStream fos = new FileOutputStream ( f );

                                        List<BlockMessage> blockMessages = dataSource.getBlockList ();

                                        String lines = "";

                                        for (BlockMessage item : blockMessages)
                                        {
                                            lines += item.getNumber () + ",";
                                        }

                                        lines = lines.substring ( 0, lines.length () - 1 );

                                        fos.write ( lines.getBytes () );

                                        Toast.makeText ( getApplicationContext (), "File Exported with with path " + file_path, Toast.LENGTH_LONG ).show ();
                                    } catch (Exception e)
                                    {
                                        Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
                                    }
                                } else if (which == 2)
                                {
                                    try
                                    {
                                        String baseDir = Environment.getExternalStorageDirectory ().getAbsolutePath ();

                                        String file_path = baseDir + "/" + "exported.xml";

                                        File f = new File ( file_path );

                                        FileOutputStream fos = new FileOutputStream ( f );

                                        // root element
                                        Element DateElement = new Element ( "Data" );
                                        Document doc = new Document ( DateElement );

                                        // supercars element
                                        Element blockList = new Element ( "BlockList" );

                                        for (BlockMessage element :
                                                block_list)
                                        {
                                            // supercars element
                                            Element carElement = new Element ( "Number" );
                                            carElement.setText ( element.getNumber () );
                                            blockList.addContent ( carElement );
                                        }

                                        doc.getRootElement ().addContent ( blockList );

                                        XMLOutputter xmlOutput = new XMLOutputter ();

                                        // display xml
                                        xmlOutput.setFormat ( Format.getCompactFormat () );
                                        xmlOutput.output ( doc, fos );

                                        Toast.makeText ( getApplicationContext (), "File Exported with with path " + file_path, Toast.LENGTH_LONG ).show ();
                                    } catch (Exception e)
                                    {
                                        Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
                                    }
                                }

                                Toast.makeText ( getApplicationContext (), which + " is selected", Toast.LENGTH_LONG ).show ();
                            }
                        } );

                inputDialog.show ();
                break;
            }

            //case for menu sort by date
            case R.id.sort_by_date:{
                Collections.sort ( list, BlockMessage.dateComparator );

                adapter = new ArrayAdapter<BlockMessage> ( getApplicationContext (), R.layout.tv, list );
                setListAdapter ( adapter );
                break;
            }

            //case for menu sort by name
            case R.id.sort_by_name:{
                refresh ();
                Collections.sort ( list, BlockMessage.NameComparator );

                adapter = new ArrayAdapter<BlockMessage> ( getApplicationContext (), R.layout.tv, list );
                setListAdapter ( adapter );
                break;
            }

            //case for menu sort by number
            case R.id.sort_by_number:{
                refresh ();

                Collections.sort ( list, BlockMessage.NumberComparator );
                adapter = new ArrayAdapter<BlockMessage> ( getApplicationContext (), R.layout.tv, list );
                setListAdapter ( adapter );
                break;
            }

        }
        return super.onOptionsItemSelected ( item ); // call super's method
    } // end method onOptionsItemSelected

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id)
    {
        super.onListItemClick ( l, v, position, id );

        try
        {
            final BlockMessage selected = (BlockMessage) l.getItemAtPosition ( position );
            String __selected = selected.toString ();
            String temp[] = __selected.split ( "\n" );

            final String _selected = temp[ 1 ].trim ();

            final AlertDialog.Builder inputDialog = new AlertDialog.Builder ( this )
                    .setTitle ( "Remove from block list " );
            inputDialog.setNegativeButton ( "Yes", new DialogInterface.OnClickListener ()
            {
                @Override
                public void onClick (DialogInterface dialog, int which)
                {
                    dataSource.open ();
                    dataSource.removeFromBlockList ( _selected );
                    refresh ();
                }
            } );
            inputDialog.setPositiveButton ( "No", new DialogInterface.OnClickListener ()
            {
                @Override
                public void onClick (DialogInterface dialog, int which)
                {

                }
            } );
            inputDialog.setCancelable ( true );
            inputDialog.show ();
            refresh ();
        } catch (Exception e)
        {
            Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
        }
    }

    private void refresh ()
    {
        dataSource.open ();
        list = dataSource.getBlockList ();
        adapter = new ArrayAdapter<BlockMessage> ( getApplicationContext (), R.layout.tv, list );
        setListAdapter ( adapter );
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult ( requestCode, resultCode, data );

        Uri uri = data.getData ();
        String selected = uri.getPath ();

        String baseDir = Environment.getExternalStorageDirectory ().getAbsolutePath ();

        String fileName = selected.substring ( ( selected.lastIndexOf ( "/" ) + 1 ) );

        String file = baseDir +"/"+ fileName;

        File f = new File ( file );

        String file_type = "";
        String fileExt = fileName.substring ( ( fileName.lastIndexOf ( "." ) + 1 ) );

        switch (fileExt){
            case "txt": {
                file_type = ".txt";
                break;
            }
            case "csv":{
                file_type = ".csv";
                break;
            }
            case "xml":{
                file_type = ".xml";
                break;
            }
        } //switch end

        if (file_type.equals ( ".txt" ))
        {
            try
            {
                FileInputStream fileInputStream = new FileInputStream ( f );
                BufferedInputStream bufferedInputStream = new BufferedInputStream ( fileInputStream );

                StringBuffer stringBuffer = new StringBuffer ();

                while (bufferedInputStream.available () != 0)
                {
                    char c = (char) bufferedInputStream.read ();
                    stringBuffer.append ( c );
                }
                List<String> list_txt = new ArrayList<String> ();
                list_txt = Arrays.asList ( stringBuffer.toString ().split ( "\n" ) );

                Calendar c = Calendar.getInstance ();
                SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yyyy" );
                String added_date = df.format ( c.getTime () );

                dataSource.open ();

                for (String item :
                        list_txt)
                {
                    String address = item;
                    String name = "unknown No.";
                    dataSource.addToBlockList ( address, name, added_date );
                }
                Toast.makeText ( getApplicationContext (), "read from file \n" + stringBuffer.toString (), Toast.LENGTH_LONG ).show ();
            } catch (Exception e)
            {
            }
        } else if (file_type.equals ( ".csv" ))
        {
            Toast.makeText ( getApplicationContext (), "Reading .csv file", Toast.LENGTH_LONG ).show ();
            try
            {
                FileInputStream fileInputStream = new FileInputStream ( f );
                BufferedInputStream bufferedInputStream = new BufferedInputStream ( fileInputStream );
                StringBuffer stringBuffer = new StringBuffer ();
                while (bufferedInputStream.available () != 0)
                {
                    char c = (char) bufferedInputStream.read ();
                    stringBuffer.append ( c );
                }
                List<String> csv_list = new ArrayList<String> ();
                csv_list = Arrays.asList ( stringBuffer.toString ().split ( "," ) );

                Calendar c = Calendar.getInstance ();
                SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yyyy" );
                String added_date = df.format ( c.getTime () );

                dataSource.open ();

                for (String item :
                        csv_list)
                {
                    String address = item;
                    String name = "unknown No.";
                    dataSource.addToBlockList ( address, name, added_date );
                }

                String lines = "";
                for (String item :
                        csv_list)
                {
                    lines += item + " , ";
                }
                Toast.makeText ( getApplicationContext (), "read from file \n" + lines, Toast.LENGTH_LONG ).show ();
            } catch (Exception e)
            {
                Toast.makeText ( getApplicationContext (), e.toString (), Toast.LENGTH_LONG ).show ();
            }

        } else if (file_type.equals ( ".xml" ))
        {
            SAXBuilder builder = new SAXBuilder ();
            File xmlFile = new File ( file );

            try
            {
                Document document = (Document) builder.build ( xmlFile );
                Element rootNode = document.getRootElement ();
                List list = rootNode.getChildren ( "BlockList" );

                String lines = "";

                for (int i = 0; i < list.size (); i++)
                {

                    Element node = (Element) list.get ( i );

                    List _list = node.getChildren ( "Number" );

                    List<String> xml_list = new ArrayList<String> ();

                    for (Object object : _list)
                    {
                        Element _node = (Element) object;
                        lines += ( _node.getText () );
                        xml_list.add ( _node.getText ().trim () );
                    }

                    Calendar c = Calendar.getInstance ();
                    SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yyyy" );
                    String added_date = df.format ( c.getTime () );

                    dataSource.open ();

                    for (String item :
                            xml_list)
                    {
                        String address = item;
                        String name = "unknown No.";
                        dataSource.addToBlockList ( address, name, added_date );
                    }

                    Toast.makeText ( getApplicationContext (), lines, Toast.LENGTH_LONG ).show ();
                }
            } catch (IOException io)
            {
                Toast.makeText ( getApplicationContext (), io.toString (), Toast.LENGTH_LONG ).show ();
            } catch (JDOMException jdomex)
            {
                Toast.makeText ( getApplicationContext (), jdomex.toString (), Toast.LENGTH_LONG ).show ();
            }
        }
    }
}
