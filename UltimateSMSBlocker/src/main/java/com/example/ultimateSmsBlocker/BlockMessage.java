package com.example.ultimateSmsBlocker;


import android.util.Log;
import java.util.Comparator;

/**
 * Created by Raza on 6/21/2016.
 */
public class BlockMessage implements Comparable<BlockMessage>
{

    public static final Comparator<BlockMessage> NameComparator = new Comparator<BlockMessage> ()
    {

        public int compare (BlockMessage o1, BlockMessage o2)
        {
            return o1.name.compareTo ( o2.name );
        }

    };

    public static final Comparator<BlockMessage> NumberComparator = new Comparator<BlockMessage> ()
    {

        public int compare (BlockMessage o1, BlockMessage o2)
        {

            String _o1 = o1.number;
            String _o2 = o2.number;

            if (!( _o1.matches ( "[0-9]+" ) ))
            {
                _o1 = "999999";
            }
            if (!( _o2.matches ( "[0-9]+" ) ))
            {
                _o2 = "999999";
            }

            if (_o1.length () > 6)
            {
                _o1 = _o1.substring ( 0, 6 );
            }
            if (_o2.length () > 6)
            {
                _o2 = _o2.substring ( 0, 6 );
            }

            if (_o1.length () < 5)
            {
                _o2 = _o1.substring ( 0, _o1.length () );
            }
            if (_o2.length () < 5)
            {
                _o1 = _o1.substring ( 0, _o1.length () );
            }

            Log.i ( "vtest", _o1 + " and " + _o2 );

            Long temp = Long.parseLong ( _o1 ) - Long.parseLong ( _o2 );
            Log.i ( "int test", temp.toString () );
            return Integer.parseInt ( temp.toString () );

        }
    };

    public static final Comparator<BlockMessage> dateComparator = new Comparator<BlockMessage> ()
    {


        public int compare (BlockMessage o1, BlockMessage o2)
        {
            return o1.getDate ().compareTo ( o2.getDate () );
        }

    };
    String name;
    String date;
    String number;
    int id;

    public BlockMessage (String name, String date, String number, int id)
    {
        this.name = name;
        this.date = date;
        this.number = number;
        this.id = id;
    }


    public BlockMessage ()
    {
        this.name = "";
        this.date = "";

        this.id = 0;

    }

    public String getNumber ()
    {
        return number;
    }

    public void setNumber (String number)
    {
        this.number = number;
    }

    public int getId ()
    {
        return id;
    }

    public void setId (int id)
    {

        this.id = id;
    }

    @Override
    public String toString ()
    {
        return
                name + "\n" +
                        number + "\n" +
                        date + "\n";
    }

    public String getName ()
    {

        return name;
    }

    public void setName (String name)
    {

        this.name = name;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    @Override
    public int compareTo (BlockMessage o)
    {
        return this.id - o.id;
    }
}
