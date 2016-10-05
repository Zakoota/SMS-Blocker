package com.example.ultimateSmsBlocker;

import android.util.Log;

/**
 * This class represents SMS.
 *
 * @author Raza
 */
public class SMSData
{

    // Number from witch the sms was send
    private String number;
    // SMS text body
    private String body;

    private String name;

    private int totalCount;

    private Long date;

    public String getNumber ()
    {
        return number;
    }

    public void setNumber (String number)
    {
        this.number = number;
    }

    public String getBody ()
    {
        return body;
    }

    public void setBody (String body)
    {
        this.body = body;
    }


    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Long getDate ()
    {
        return date;
    }

    public void setDate (Long v)
    {
        this.date = v;
    }

    public int getTotalCount ()
    {
        return totalCount;
    }

    public void setTotalCount (int totalCount)
    {
        this.totalCount = totalCount;
    }

    @Override
    public String toString ()
    {
        String part = "";

        try
        {
            if (name == null)
            {
                if (getBody ().length () > 40)
                {
                    part = getBody ().substring ( 0, 40 ) + "...";
                } else if (getBody ().length () > 0)
                {
                    part = getBody ().substring ( 0, getBody ().length () - 1 );
                }
                return number + "\n" + part;
            } else
            {
                if (getBody ().length () > 40)
                {
                    part = getBody ().substring ( 0, 40 ) + "...";
                } else if (getBody ().length () > 0)
                {
                    part = getBody ().substring ( 0, getBody ().length () - 1 );
                }
                return name + "\n" + part;
            }
        } catch (Exception e)
        {
            Log.i ( "inbox", e.toString () );
        }
        return part;
    }

}
