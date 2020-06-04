package com.example.jiyeonhyun.ma02_20161016.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jiyeonhyun.ma02_20161016.DTO.DiaryDTO;
import com.example.jiyeonhyun.ma02_20161016.R;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter implements Filterable {

//    Filtering 처리가 가능하도록 인터페이스를 상속함

    static class ViewHolder {
        TextView tvDate;
        TextView tvContents;
        ImageView imgFeeling;
    }

    Filter listFilter;
    private Context context;
    private int layout;
    private ArrayList<DiaryDTO> myDiaryList;
    private ArrayList<DiaryDTO> filteredItemList;
    private LayoutInflater inflater;

    public SearchAdapter(Context context, int layout, ArrayList<DiaryDTO> myDiaryList) {
        this.context = context;
        this.layout = layout;
        this.myDiaryList = myDiaryList;
        filteredItemList = myDiaryList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter ;
    }

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int pos) {
        return filteredItemList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final int pos = position;
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layout, viewGroup, false);
            holder = new ViewHolder();
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.tvContents = convertView.findViewById(R.id.tvContents);
            holder.imgFeeling = convertView.findViewById(R.id.imgFeeling);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imgFeeling.setImageResource(filteredItemList.get(pos).getFeeling());
        holder.tvDate.setText(filteredItemList.get(pos).getDate());
        holder.tvContents.setText(filteredItemList.get(pos).getContents());

        return convertView;
    }

// 입력 값에 해당하는 컨텐츠만 가지고 오도록 필터링 처리 -> 입력 값이 없을 경우엔 전체 리스트가 뜸 원본 리스트 대신 다른 리스트를 사용해서 구현해야 함
    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = myDiaryList;
                results.count = myDiaryList.size() ;
            } else {
                ArrayList<DiaryDTO> itemList = new ArrayList<DiaryDTO>() ;

                for (DiaryDTO item : myDiaryList) {
                    if (item.getContents().contains(constraint.toString())) {
                        itemList.add(item) ;
                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // update listview by filtered data list.
            filteredItemList = (ArrayList<DiaryDTO>) results.values ;

            // notify
            if (results.count > 0) {
                Log.e("list","CHANGED!!");
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}



