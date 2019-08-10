package com.example.comp.timetableapp;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.R.attr.data;

/**
 * Created by Mayank on 07-10-2017.
 */


    public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {

        private ArrayList<DataModel> dataSet;
        Context mContext;

        // View lookup cache
        private static class ViewHolder {
            TextView subject_name;
            TextView time;
            TextView venue_name;
        }

        public CustomAdapter(ArrayList<DataModel> data, Context context) {
            super(context, R.layout.time_table_list_view_layout, data);
            this.dataSet = data;
            this.mContext = context;

        }

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            Object object = getItem(position);
            DataModel dataModel = (DataModel) object;
        }

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            DataModel dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.time_table_list_view_layout, parent, false);
                viewHolder.subject_name = (TextView) convertView.findViewById(R.id.subject_name);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.venue_name = (TextView) convertView.findViewById(R.id.venue_name);

                result = convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }
            lastPosition = position;

            viewHolder.subject_name.setText(dataModel.getName());
            viewHolder.time.setText("Time: "+dataModel.getType());
            viewHolder.venue_name.setText("Venue: "+dataModel.getVersion_number());

            return convertView;
        }
    }
