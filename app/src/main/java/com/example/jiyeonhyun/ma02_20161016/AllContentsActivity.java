package com.example.jiyeonhyun.ma02_20161016;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.jiyeonhyun.ma02_20161016.Adapter.MyCursorAdapter;
import com.example.jiyeonhyun.ma02_20161016.DB.DBHelper;

public class AllContentsActivity extends AppCompatActivity {

    private ListView lvDiary = null;

    MyCursorAdapter adapter;
    Cursor cursor;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contents);

        helper = new DBHelper(this);

        lvDiary = (ListView) findViewById(R.id.diaryList);

        adapter = new MyCursorAdapter(this, R.layout.activity_custom, null);
        lvDiary.setAdapter(adapter);

        lvDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllContentsActivity.this, DetailActivity.class);

//                세부 항목을 보여주기 위해서 id를 Extra로 넘겨주고 startActivity
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        /* 롱클릭 시 항목 삭제 -> dialog로 삭제 확인 메시지 띄우고 삭제 */
        lvDiary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                new AlertDialog.Builder(AllContentsActivity.this)
                        .setTitle("일기 삭제")
                        .setMessage(" 일기를 삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String sql = "delete from " + DBHelper.TABLE_NAME + " where _id=" + id;
                                SQLiteDatabase db = helper.getWritableDatabase();
                                db.execSQL(sql);
                                helper.close();
                                onResume();
                                adapter.changeCursor(cursor);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* DB에서 데이터를 읽어와 Adapter에 설정 */
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME, null);

        adapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }
}
