package com.example.ultimateSmsBlocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Raza on 10-Oct-16.
 */

public class MessageListAdapter extends ArrayAdapter<Message> {

    public MessageListAdapter(Context context, int resource, List<Message> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.messagelist, null);
        }

        Message p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.tvmsg_add);
            TextView tt2 = (TextView) v.findViewById(R.id.tvmsg_rec);
            TextView tt3 = (TextView) v.findViewById(R.id.tvmsg_exp);
            TextView tt4 = (TextView) v.findViewById(R.id.tvmsg_bod);

            if (tt1 != null) {
                tt1.setText(p.getAddress());
            }

            if (tt2 != null) {
                tt2.setText(Utils.dateFromString(p.getReceiveDate()));
            }

            if (tt3 != null) {
                tt3.setText(Utils.dateFromString(p.getRetainDate()));
            }
            if (tt4 != null) {
                tt4.setText(p.getBody());
            }
        }

        return v;
    }
}
