package com.example.jiyeonhyun.ma02_20161016.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jiyeonhyun.ma02_20161016.DTO.MovieDTO;
import com.example.jiyeonhyun.ma02_20161016.R;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MovieAdapter extends BaseAdapter {
    public static final String TAG = "MovieAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<MovieDTO> list;
    private ViewHolder viewHodler = null;

    public MovieAdapter(Context context, int resource, ArrayList<MovieDTO> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView with position : " + position);
        View view = convertView;

        if (view == null) {
            viewHodler = new ViewHolder();
            view = inflater.inflate(layout, parent, false);
            viewHodler.tvTitle = (TextView)view.findViewById(R.id.tvTitle);
            viewHodler.tvPubDate = (TextView)view.findViewById(R.id.tvPubDate);
            viewHodler.tvDirector = (TextView)view.findViewById(R.id.tvDirector);
            view.setTag(viewHodler);
        } else {
            viewHodler = (ViewHolder)view.getTag();
        }

        MovieDTO dto = list.get(position); // dto 객체를 가지고 오게 됨

        viewHodler.tvTitle.setText(dto.getTitle());
        viewHodler.tvPubDate.setText(dto.getPubDate());
        viewHodler.tvDirector.setText(dto.getDirector());

        return view;
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public MovieDTO getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvPubDate = null;
        public TextView tvDirector = null;
        public ImageView ivImage = null;
    }

    public void setList(ArrayList<MovieDTO> list) {
        this.list = list;
    }
}
