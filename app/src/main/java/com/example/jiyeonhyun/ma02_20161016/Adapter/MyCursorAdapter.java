package com.example.jiyeonhyun.ma02_20161016.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jiyeonhyun.ma02_20161016.DB.DBHelper;
import com.example.jiyeonhyun.ma02_20161016.R;

public class MyCursorAdapter extends CursorAdapter {

    LayoutInflater inflater;
    Cursor cursor;

    public MyCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemLayout = inflater.inflate(R.layout.activity_custom, parent, false);
        return listItemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvContents = (TextView)view.findViewById(R.id.tvContents);
        ImageView imgFeeling = (ImageView)view.findViewById(R.id.imgFeeling);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);

        tvContents.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_CONTENTS)));
        imgFeeling.setImageResource(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_FEELING)));
        tvDate.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE)));
    }
}
