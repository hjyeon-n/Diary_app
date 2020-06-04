package com.example.jiyeonhyun.ma02_20161016;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;

import com.example.jiyeonhyun.ma02_20161016.Adapter.MyCursorAdapter;
import com.example.jiyeonhyun.ma02_20161016.DB.DBHelper;

public class CollectActivity extends AppCompatActivity {

    Button clickMonth;
    DBHelper helper;
    Cursor cursor;

    private ListView lvDiary = null;
    MyCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        helper = new DBHelper(this);
        lvDiary = (ListView) findViewById(R.id.monthList);

        clickMonth = (Button)findViewById(R.id.clickMonth);

        adapter = new MyCursorAdapter(this, R.layout.activity_custom, null);
        lvDiary.setAdapter(adapter);
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.clickMonth: // 월별로 모아보기
                PopupMenu popup = new PopupMenu(this, v);
                MenuInflater inflater = popup.getMenuInflater();
                Menu menu = popup.getMenu();
                inflater.inflate(R.menu.menu_popup, menu);
                final SQLiteDatabase db = helper.getReadableDatabase();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.Jan:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 1", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Feb:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 2", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Mar:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 3", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Apr:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 4", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.May:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 5", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Jun:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 6", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Jul:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 7", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Aug:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 8", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Sep:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 9", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Oct:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 10", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Nov:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 11", null);
                                adapter.changeCursor(cursor);
                                break;
                            case R.id.Dec:
                                cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " where month = 12", null);
                                adapter.changeCursor(cursor);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
                break;
            case R.id.btn_cal: // 캘린더뷰로 이동하기
                intent = new Intent(this, CalendarActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
