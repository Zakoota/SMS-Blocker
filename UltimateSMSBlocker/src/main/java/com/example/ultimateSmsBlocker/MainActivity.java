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

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

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
import java.io.FileReader;
import java.io.FileWriter;
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


    /**
     * on restart get blocklist and populate it
     */
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
        dataSource = new Data ( getApplicationContext () );
        dataSource.open ();
        messages_list = dataSource.findAll ();
        settings = getApplicationContext ().getSharedPreferences ( "settings", MODE_PRIVATE );

        /**
         * check and setDefault retain_days if not found
         */
        checkFirstTime();

        /**
         * check for expiring messages in table and delete them
         */
        checkForOldMessages();

        /**
         * get blocklist table and populate it using custom adapter
         */
        dataSource.open ();
        list = dataSource.getBlockList ();
        adapter = new BlockListAdapter ( getApplicationContext (), R.layout.blocklist, list );
        setListAdapter ( adapter );

    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        dataSource.open ();
        list = dataSource.getBlockList ();
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
        switch(item.getItemId()) {
            /**
             * case for add menu item
             */
            case R.id.add:{
                Intent intent = new Intent ( getApplicationContext (), ShowInboxActivity.class );
                startActivity ( intent );
                break;
            }
            /**
             * case for menu item add number
             */
            case R.id.add_number:{
                /**
                 * show a dialog with two buttons Contact and Unknown, and call an intent accordingly
                 */
                final AlertDialog.Builder builder = new AlertDialog.Builder ( this ).setTitle ( "Choose Option to Add Number" );
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
            /**
             * case for item menu series
             */
            case R.id.series:{
                /**
                 * inflate and get reference to both editTexts
                 */
                LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE );
                view = inflater.inflate ( R.layout.series, null );
                et_series_1 = (EditText) view.findViewById ( R.id.et_series_1 );
                et_series_2 = (EditText) view.findViewById ( R.id.et_series_2 );

                /**
                 * show dialog
                 */
                AlertDialog.Builder inputDialog = new AlertDialog.Builder ( this );
                inputDialog.setView ( view ); // set the dialog's custom View
                inputDialog.setTitle ( "Enter series range" );
                inputDialog.setPositiveButton("Add Series", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                String input = et_series_1.getText().toString().trim();
                                String input_2 = et_series_2.getText().toString().trim();
                                String inputHead = "";

                                /**
                                 * get header from input and plug it to series
                                 */
                                try {
                                    int outerI;
                                    int innerJ;
                                    for (outerI = 0; outerI < input.length(); outerI++) {
                                        for (innerJ = 49; innerJ <= 57; innerJ++) {
                                            if (input.charAt(outerI) == innerJ) {
                                                break;
                                            }
                                        }
                                        if (input.charAt(outerI) == innerJ) {
                                            break;
                                        }
                                    }
                                    inputHead = input.substring(0, outerI);
                                    }catch(Exception e){}//empty
                                /**
                                 * try block for loop that makes series from provided start and end number and
                                 * add it to blocklist
                                 */
                                try{
                                    long start = Long.parseLong(input);
                                    long end = Long.parseLong(input_2);
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                    String added_date = df.format(c.getTime());

                                    for (long i = start; i <= end; i++) {
                                        String address = inputHead + i + "";
                                        String name = "unknown No.";

                                        dataSource.addToBlockList(address, name, added_date);
                                    }
                                    Toast.makeText(MainActivity.this, "Series from:"+input+" to:"+input_2+"\nadded to blocklist", Toast.LENGTH_LONG).show();
                                }catch (Exception e){ }
                                refresh();
                            }catch (Exception e) { }
                        }
                    });
                    inputDialog.show();
                break;
            }//end of series case

            /**
             * case for menu item import
             */
            case R.id._import:{
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose file"), FILE_ID);
                break;
            }


            /**
             * case for menu item export
             */
            case R.id.export: {
                if (!dataSource.isBlockListEmpty()) {
                    final String list[] = {"Text file", "CSV file", "XML file"};
                    AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);
                    inputDialog.setTitle("Choose Type of File for Export")
                            .setItems(list, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    fileWriter(which);
                                }});
                    inputDialog.show();
                    break;
                }else {
                    AlertDialog.Builder emptyExportAlert = new AlertDialog.Builder(this)
                            .setTitle("Error!")
                            .setMessage("Cannot export empty block list.")
                            .setPositiveButton("OK",null);
                    emptyExportAlert.show();
                }
            }

            /**
             * case for menu item sort by date
             */
            case R.id.sort_by_date:{
                refresh();
                Collections.sort ( list, BlockMessage.dateComparator );
                adapter = new BlockListAdapter ( getApplicationContext (), R.layout.blocklist, list );
                setListAdapter ( adapter );
                break;
            }

            /**
             * case for menu item sort by name
             */
            case R.id.sort_by_name:{
                refresh ();
                Collections.sort ( list, BlockMessage.NameComparator );
                adapter = new BlockListAdapter ( getApplicationContext (), R.layout.blocklist, list );
                setListAdapter ( adapter );
                break;
            }

            /**
             * case for menu item sort by number
             */
            case R.id.sort_by_number:{
                refresh ();
                Collections.sort ( list, BlockMessage.NumberComparator );
                adapter = new BlockListAdapter ( getApplicationContext (), R.layout.blocklist, list );
                setListAdapter ( adapter );
                break;
            }

        }
        return super.onOptionsItemSelected ( item ); // call super's method
    } // end method onOptionsItemSelected

    /**
     * Block list item click method
     */
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
                    .setTitle ( "Remove from block list" )
                    .setMessage("Do you want to remove "+_selected+" from block list?");
            inputDialog.setNegativeButton ( "Yes", new DialogInterface.OnClickListener ()
            {
                @Override
                public void onClick (DialogInterface dialog, int which)
                {
                    dataSource.open ();
                    dataSource.removeFromBlockList ( _selected );
                    Toast.makeText(MainActivity.this,_selected+"\nremoved from blocklist", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e){ }
    }

    private void refresh ()
    {
        dataSource.open ();
        list = dataSource.getBlockList ();
        adapter = new BlockListAdapter ( getApplicationContext (), R.layout.blocklist, list );
        setListAdapter ( adapter );
    }

    /**
     * import intent returns file path and rest happens here
     */
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            super.onActivityResult(requestCode, resultCode, data);
            Uri uri = data.getData();
            String selected = uri.getPath();
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = selected.substring((selected.lastIndexOf("/") + 1));
            String file = baseDir + "/" + fileName;
            String file_type = "";
            String fileExt = fileName.substring((fileName.lastIndexOf(".") + 1));
            //switch statement for assigning file extension to fileType var
            switch (fileExt) {
                case "txt": {
                    file_type = ".txt";
                    break;
                }
                case "csv": {
                    file_type = ".csv";
                    break;
                }
                case "xml": {
                    file_type = ".xml";
                    break;
                }
            }
            fileReader(file_type, file, fileName);
        }else if(resultCode == RESULT_CANCELED) {
            //nothing
        }else{
            Toast.makeText(getApplicationContext(), "ERROR: File not selected", Toast.LENGTH_LONG).show();
        }
    }//onActivity intent end

/**
* Custom methods
*/

    /**
     * checking for old messages and delete them
     */
    private void checkForOldMessages(){
        int total = 0;
        for (Message msg : messages_list)
        {
            try
            {
                Calendar today_cal = Calendar.getInstance ();
                Calendar retain_cal = Calendar.getInstance ();

                retain_cal.setTimeInMillis ( Long.parseLong ( msg.getRetainDate () ) );

                if (( today_cal.after ( retain_cal ) ) && ( !today_cal.equals ( retain_cal ) )){
                    total++;
                    dataSource.open ();
                    dataSource.deleteBlocked ( msg );
                }

            }catch (Exception e){ }
        }
        if (total > 0)
        {
            Toast.makeText ( getApplicationContext (), total + " messages delete from app", Toast.LENGTH_LONG ).show ();
        }
    }

    /**
     * check if retain_days are not set and set them to default
     */
    private void checkFirstTime(){
        SharedPreferences.Editor editor;
        if(settings.getInt("retain_days", -1)< 0) {
            try {
                editor = settings.edit();
                editor.putInt("retain_days", 30);
                editor.commit();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                        .setTitle("Set to default!")
                        .setMessage("Expiry date for blocked messages is set to default 30 days.\nChange it to desired amount from Settings tab")
                        .setPositiveButton("OK",null);
                alertDialogBuilder.show();
            } catch (Exception e) { }
        }
    }

    /**
     * Filepath manipulator that appends number to file name if one already exists
     */
    private String getFilePath(String ext){
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();
        String file_path = baseDir + "/" + "BLOCKLIST_BACKUP_" + formatter.format(now) + ext;

        File f = new File(file_path);

        if (f.exists()) {
            int i = 0;
            do {
                i++;
                file_path = baseDir + "/" + "BLOCKLIST_BACKUP_" + formatter.format(now) + "_(" + i + ")" + ext;
                f = new File(file_path);
            } while (f.exists());
        }
        return file_path;
    }

    /**
     * file exporter methods, takes menu id(1-3) and writes file
     * @param which
     */
    private void fileWriter(int which){
        List<BlockMessage> block_list = dataSource.getBlockList();
        switch (which) {
            case 0: {
                try{
                    String file_path = getFilePath(".txt");
                    File f = new File(file_path);
                    FileOutputStream fos = new FileOutputStream(f);
                    String lines = "";
                    for (BlockMessage element :
                            block_list) {
                        lines += element.getNumber() + "\n";
                    }
                    fos.write(lines.getBytes());
                    Toast.makeText(getApplicationContext(), "Text file exported to path " + file_path, Toast.LENGTH_SHORT).show();
                } catch (Exception e) { }
                break;
            }
            case 1: {
                try{
                    String file_path = getFilePath(".csv");
                    CSVWriter writer = new CSVWriter(new FileWriter(file_path));

                    String[] lines=new String[block_list.size()];
                    int i=0;
                    for (BlockMessage item : block_list) {
                        lines[i]=item.getNumber();
                        i++;
                    }
                    writer.writeNext(lines);
                    writer.close();

                    Toast.makeText(getApplication(), "CSV file exported to "+file_path, Toast.LENGTH_LONG).show();
                } catch (Exception e) { }
                break;
            }
            case 2: {
                try{
                    String file_path = getFilePath(".xml");
                    FileOutputStream fos = new FileOutputStream(file_path);

                    // root element
                    Element DateElement = new Element("Data");
                    Document doc = new Document(DateElement);

                    // supercars element
                    Element blockList = new Element("BlockList");

                    for (BlockMessage element :
                            block_list) {
                        // supercars element
                        Element carElement = new Element("Number");
                        carElement.setText(element.getNumber());
                        blockList.addContent(carElement);
                    }

                    doc.getRootElement().addContent(blockList);

                    XMLOutputter xmlOutput = new XMLOutputter();

                    // display xml
                    xmlOutput.setFormat(Format.getCompactFormat());
                    xmlOutput.output(doc, fos);

                    Toast.makeText(getApplicationContext(), "XML file exported to path " + file_path, Toast.LENGTH_SHORT).show();
                } catch (Exception e) { }
                break;
            }
        }
    }

    /**
     * file importer method that takes file extension, file path, and file name then imports it to blocklist
     */
    private void fileReader(String file_type, String file, String fileName){
        switch (file_type) {
            //txt filetype case and method
            case ".txt": {
                try {
                    File f = new File(file);
                    FileInputStream fileInputStream = new FileInputStream(f);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                    StringBuffer stringBuffer = new StringBuffer();

                    while (bufferedInputStream.available() != 0) {
                        char c = (char) bufferedInputStream.read();
                        stringBuffer.append(c);
                    }
                    List<String> list_txt;
                    list_txt = Arrays.asList(stringBuffer.toString().split("\n"));

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String added_date = df.format(c.getTime());

                    dataSource.open();

                    for (String item :
                            list_txt) {
                        String address = item;
                        String name = "unknown No.";
                        dataSource.addToBlockList(address, name, added_date);
                    }
                    Toast.makeText(getApplicationContext(), "Blocklist imported from file: "+fileName, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    break;
                }
                break;
            }//txt case end

            //csv filetype method and case
            case ".csv": {
                try{
                    CSVReader reader = new CSVReader(new FileReader(file));
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String added_date = df.format(c.getTime());
                    dataSource.open();
                    String[] rawAddress = reader.readNext();
                    for(int i=0;rawAddress.length>i;i++){
                        String address = rawAddress[i];
                        String name = "unknown No.";
                        dataSource.addToBlockList(address, name, added_date);
                    }
                    reader.close();
                    dataSource.close();
                    Toast.makeText(getApplicationContext(), "Blocklist imported from file: "+fileName, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    break;
                }
                break;
            }//csv case end

            //xml filetype method and case
            case ".xml": {
                SAXBuilder builder = new SAXBuilder();
                File xmlFile = new File(file);

                try {
                    Document document = builder.build(xmlFile);
                    Element rootNode = document.getRootElement();
                    List list = rootNode.getChildren("BlockList");

                    String lines = "";

                    for (int i = 0; i < list.size(); i++) {

                        Element node = (Element) list.get(i);

                        List _list = node.getChildren("Number");

                        List<String> xml_list = new ArrayList<String>();

                        for (Object object : _list) {
                            Element _node = (Element) object;
                            lines += (_node.getText());
                            xml_list.add(_node.getText());
                        }

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String added_date = df.format(c.getTime());

                        dataSource.open();

                        for (String item :
                                xml_list) {
                            String address = item;
                            String name = "unknown No.";
                            dataSource.addToBlockList(address, name, added_date);
                        }

                        Toast.makeText(getApplicationContext(), "Blocklist imported from file: "+fileName, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException io) {
                    Toast.makeText(getApplicationContext(), io.toString(), Toast.LENGTH_LONG).show();
                    break;
                } catch (JDOMException jdomex) {
                    Toast.makeText(getApplicationContext(), jdomex.toString(), Toast.LENGTH_LONG).show();
                    break;
                }
                break;
            }//xml case end

            default:{
                Toast.makeText(getApplicationContext(), "ERROR: Invalid File selected",Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}//Class MainActivity end
