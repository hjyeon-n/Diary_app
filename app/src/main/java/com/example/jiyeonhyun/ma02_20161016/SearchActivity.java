package com.example.jiyeonhyun.ma02_20161016;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.example.jiyeonhyun.ma02_20161016.Adapter.SearchAdapter;
import com.example.jiyeonhyun.ma02_20161016.DB.DBHelper;
import com.example.jiyeonhyun.ma02_20161016.DTO.DiaryDTO;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    SearchAdapter adapter;
    ListView listView;
    ArrayList<DiaryDTO> DiaryList;
    DBHelper dbHelper;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DBHelper(this);
        listView = findViewById(R.id.searchList);
        DiaryList = new ArrayList<DiaryDTO>();

        adapter = new SearchAdapter(this, R.layout.activity_custom, DiaryList);

        listView.setAdapter(adapter);
        readAllDiary(); // 처음엔 모든 값을 읽어옴

        etSearch = (EditText)findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() { // 값을 읽어올 때마다 필터링
            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString();
                ((SearchAdapter)listView.getAdapter()).getFilter().filter(filterText) ;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        }) ;
    }

    private void readAllDiary() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME, null);

        while(cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_ID));
            String date = cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE));
            String weather = cursor.getString(cursor.getColumnIndex(DBHelper.COL_WHETHER));
            int feeling = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_FEELING));
            String contents = cursor.getString(cursor.getColumnIndex(DBHelper.COL_CONTENTS));
            String location = cursor.getString(cursor.getColumnIndex(DBHelper.COL_LOCATION));

            DiaryList.add ( new DiaryDTO(id, date, weather, feeling, contents, location) );
        }
        cursor.close();
        dbHelper.close();
    }

    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
