package com.example.jiyeonhyun.ma02_20161016;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.jiyeonhyun.ma02_20161016.DB.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    DBHelper helper;
    EditText etContents;
    EditText etWeather;
    EditText etDate;
    EditText etLocation;
    ImageView ImgFeeling;
    ImageView ImgView;
    EditText etMovie;
    Cursor cursor;
    SQLiteDatabase db;
    String color, type, url;
    int selectedIndex;
    long id;

    public final static String TAG = "detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        helper = new DBHelper(this);

        etContents = findViewById(R.id.etContents);
        etWeather = findViewById(R.id.etWeather);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        ImgFeeling = findViewById(R.id.etFeeling);
        etMovie = findViewById(R.id.etMovie);
        ImgView = findViewById(R.id.imgView);

        db = helper.getWritableDatabase();
        Intent intent = getIntent();

//        전체 항목 보기에서 넘어온 id를 가지고 해당 항목들을 EditText에 채워줌
        id = intent.getLongExtra("id", 0);

        cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME + " WHERE _id = '" + id + "';", null);

//        내가 원하는 항목 윗부분을 가리키고 있기 때문에 moveToNext로 맞춰줘야 됨
        while (cursor.moveToNext()) {

            etContents.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_CONTENTS)));
            etWeather.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_WHETHER)));
            etDate.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE)));
            etLocation.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_LOCATION)));
            ImgFeeling.setImageResource(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_FEELING)));
            etMovie.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_MOVIE)));
            id = cursor.getLong(cursor.getColumnIndex(DBHelper.COL_ID));

            if (cursor.getString(cursor.getColumnIndex(DBHelper.COL_IMG)) != null) {
                url = cursor.getString(cursor.getColumnIndex(DBHelper.COL_IMG));
                Log.e("url", url);
                Bitmap bp = BitmapFactory.decodeFile(url);
                ImgView.setImageBitmap(bp);
            }

            Log.e("_id", String.valueOf(id));

            if (cursor.getString(cursor.getColumnIndex(DBHelper.COL_COLOR)) != null) { // 글씨 색 설정
                color = cursor.getString(cursor.getColumnIndex(DBHelper.COL_COLOR));
                if (color.equals("red")) {
                    etContents.setTextColor(Color.RED);
                } else if (color.equals("blue")) {
                    etContents.setTextColor(Color.BLUE);
                } else {
                    etContents.setTextColor(Color.BLACK);
                }
            }

            if (cursor.getString(cursor.getColumnIndex(DBHelper.COL_TYPE)) != null) { // 글씨 타입 설정
                type = cursor.getString(cursor.getColumnIndex(DBHelper.COL_TYPE));
                if (type.equals("italic")) {
                    etContents.setTypeface(null, Typeface.ITALIC);
                } else if (type.equals("bold")) {
                    etContents.setTypeface(null, Typeface.BOLD);
                } else {
                    etContents.setTypeface(null, Typeface.NORMAL);
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return: // 메인으로 돌아가기
                finish();
                break;
            case R.id.btn_modify: // 수정
                String contents = etContents.getText().toString();
                ContentValues row= new ContentValues();
                row.put("contents", contents);
                row.put("color", color);
                row.put("type", type);
                String whereClause= "_id=?";
                String[] whereArgs= new String[]{String.valueOf(id)};
                db.update(DBHelper.TABLE_NAME, row, whereClause, whereArgs);
                finish();
                break;
        }
    }

    //    옵션 메뉴
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.textcolor: // 항목 추가
                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);

                builder.setTitle("색깔을 선택하세요");
                builder.setSingleChoiceItems(R.array.color, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selectedIndex = i;
                    }
                });
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectedIndex) {
                            case 0:
                                etContents.setTextColor(Color.RED);
                                color = "red";
                                break;
                            case 1:
                                etContents.setTextColor(Color.BLUE);
                                color = "blue";
                                break;
                            case 2:
                                etContents.setTextColor(Color.BLACK);
                                color = "black";
                                break;
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.setCancelable(true);
                builder.show();
                break;
            case R.id.texttype:
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailActivity.this);

                builder2.setTitle("타입을 선택하세요");
                builder2.setSingleChoiceItems(R.array.type, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selectedIndex = i;
                    }
                });
                builder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectedIndex) {
                            case 0:
                                etContents.setTypeface(null, Typeface.NORMAL);
                                type = "normal";
                                break;
                            case 1:
                                etContents.setTypeface(null, Typeface.ITALIC);
                                type = "italic";
                                break;
                            case 2:
                                etContents.setTypeface(null, Typeface.BOLD);
                                type = "bold";
                                break;
                        }
                    }
                });
                builder2.setNegativeButton("취소", null);
                builder2.setCancelable(true);
                builder2.show();
                break;
            case R.id.now:
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String getTime = sdf.format(date);
                String text = etContents.getText().toString();
                etContents.setText(text + " " + getTime);
                break;
        }
        return true;
    }
}
