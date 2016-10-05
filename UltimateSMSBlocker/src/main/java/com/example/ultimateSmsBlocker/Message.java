package com.example.ultimateSmsBlocker;

/**
 * Created by user on 6/12/2016.
 */
public class Message {

    long id;
    private String address;
    private String body;
    private String receiveDate;
    private String retainDate;

    public Message () {
    }

    public Message (String address, String body, String receiveDate, String retainDate, long id) {
        this.address = address;
        this.body = body;
        this.receiveDate = receiveDate;
        this.retainDate = retainDate;
        this.id = id;
    }

    public String getAddress () {
        return address;
    }

    public void setAddress (String address) {
        this.address = address;
    }

    public String getBody () {
        return body;
    }

    public void setBody (String body) {
        this.body = body;
    }

    public String getReceiveDate () {
        return receiveDate;
    }

    public void setReceiveDate (String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getRetainDate () {
        return retainDate;
    }

    public void setRetainDate (String retainDate) {
        this.retainDate = retainDate;
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    @Override
    public String toString () {

        return "\nAddress : " + address
                + "\nBody : " + body
                + "\nreceive date : " + Utils.dateFromLong ( Long.parseLong ( receiveDate ) )
                + "\nretain date : " + Utils.dateFromLong ( Long.parseLong ( retainDate ) );
    }
}
