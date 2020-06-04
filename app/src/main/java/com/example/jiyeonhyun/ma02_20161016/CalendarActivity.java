package com.example.jiyeonhyun.ma02_20161016;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jiyeonhyun.ma02_20161016.Adapter.MyCursorAdapter;
import com.example.jiyeonhyun.ma02_20161016.DB.DBHelper;

public class CalendarActivity extends AppCompatActivity {

    DBHelper helper;
    Cursor cursor;

    private ListView lvDiary = null;
    MyCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendar = (CalendarView)findViewById(R.id.calendarView);
        helper = new DBHelper(this);
        lvDiary = (ListView) findViewById(R.id.listView);

        adapter = new MyCursorAdapter(this, R.layout.activity_custom, null);
        lvDiary.setAdapter(adapter);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                SQLiteDatabase db = helper.getReadableDatabase();

//                입력된 달력 일자를 받아와 쿼리를 통해서 해당 일자의 일기를 출력함
                String selection = "year = ? and month = ? and day = ?";
                String[] selectArgs = new String[] {String.valueOf(year), String.valueOf(month + 1), String.valueOf(dayOfMonth)};

                cursor = db.query(DBHelper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);

                adapter.changeCursor(cursor);
            }
        });
    }
}
