package com.example.ultimateSmsBlocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Raza on 09-Oct-16.
 */
public class BlockListAdapter extends ArrayAdapter<BlockMessage> {

    public BlockListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BlockListAdapter(Context context, int resource, List<BlockMessage> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.blocklist, null);
        }

        BlockMessage p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.tv_number);
            TextView tt2 = (TextView) v.findViewById(R.id.tv_name);

            if (tt1 != null) {
                tt1.setText(p.getNumber());
            }

            if (tt2 != null) {
                tt2.setText(p.getName());
            }
        }

        return v;
    }

}