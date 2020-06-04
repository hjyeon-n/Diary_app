package com.example.jiyeonhyun.ma02_20161016;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.jiyeonhyun.ma02_20161016.Adapter.SearchAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* 기능별로 메뉴화하였음 */
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_all:
                intent = new Intent(this, AllContentsActivity.class);
                break;
            case R.id.btn_new:
                intent = new Intent(this, InsertContentsActivity.class);
                break;
            case R.id.btn_collect:
                intent = new Intent(this, CollectActivity.class);
                break;
            case R.id.btn_search:
                intent = new Intent(this, SearchActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
    }

//      옵션 메뉴
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //    옵션 메뉴에서 아이템을 선택했을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.introduce: // 개발자 소개
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("개발자 정보");
                dialog.setMessage("MA 02분반 20161016 현지연");
                dialog.setPositiveButton("확인",null);
                dialog.setCancelable(true);
                dialog.show();
                break;
            case R.id.finish: // 앱종료 대화상자
                AlertDialog.Builder dialog_f = new AlertDialog.Builder(MainActivity.this);
                dialog_f.setTitle("앱 종료");
                dialog_f.setMessage("앱을 종료하시겠습니까?");
                dialog_f.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                dialog_f.setNegativeButton("취소", null);
                dialog_f.setCancelable(true);
                dialog_f.show();
                break;
        }
        return true;
    }
}

