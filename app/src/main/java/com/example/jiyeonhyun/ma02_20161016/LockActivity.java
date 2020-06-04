package com.example.jiyeonhyun.ma02_20161016;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jiyeonhyun.ma02_20161016.DB.DBHelper;

public class LockActivity extends AppCompatActivity {

    EditText etPw;
    Button btnUpadate;
    Button btnCreate;

    Cursor cursor;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        helper = new DBHelper(this);

        etPw = findViewById(R.id.etPW);
        btnUpadate = findViewById(R.id.btnUpdate);
        btnCreate = findViewById(R.id.btnCreate);
    }

    public void onClick(View v) {
        final SQLiteDatabase db = helper.getReadableDatabase();
        final AlertDialog.Builder builder = new AlertDialog.Builder(LockActivity.this);
        final LinearLayout newPWLayout = (LinearLayout) View.inflate(this, R.layout.create_pwd, null);
        final LinearLayout updatePWLayout = (LinearLayout) View.inflate(this, R.layout.update_pwd, null);

        switch (v.getId()) {
            case R.id.btnUpdate: // 암호 변경
                builder.setTitle("암호 변경");
                builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder pwbuilder = new AlertDialog.Builder(LockActivity.this);
                        String before = "";
                        cursor = db.rawQuery("SELECT * FROM " + DBHelper.PW_TABLE, null);
                        while (cursor.moveToNext()) {
                            before = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PW));
                            Log.e("sql", before);
                        }
                        EditText originalPwd = (EditText)updatePWLayout.findViewById(R.id.originalPwd);
                        EditText newPwd = (EditText)updatePWLayout.findViewById(R.id.newPwd);
                        EditText checkPwd = (EditText)updatePWLayout.findViewById(R.id.checkPwd);

                        if (before == null) { // DB에 암호가 없는 경우
                            pwbuilder.setTitle("먼저 암호를 생성하십시오");
                            pwbuilder.setPositiveButton("확인", null);
                            pwbuilder.show();
                        }
                        else {
                            if (before.equals(originalPwd.getText().toString())) { // 기존 암호와 테이블에 저장된 암호값 비교
                                if (newPwd.getText().toString().equals(checkPwd.getText().toString())) { // 새 비밀번호와 다시 입력한 값끼리의 비교 성공
                                    db.execSQL("UPDATE " + DBHelper.PW_TABLE + " SET " + DBHelper.COL_PW + "='" + newPwd.getText().toString() + "';");
                                    Toast.makeText(LockActivity.this, "암호가 변경되었습니다", Toast.LENGTH_SHORT).show();
                                }
                                else { // 재입력값과 입력값이 일치 하지 않았을 경우
                                    pwbuilder.setTitle("암호가 일치하지 않습니다.");
                                    pwbuilder.setPositiveButton("확인", null);
                                    pwbuilder.show();
                                }
                            }
                            else {
                                pwbuilder.setTitle("기존 암호와 다릅니다.");
                                pwbuilder.setPositiveButton("확인", null);
                                pwbuilder.show();
                            }
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.setView(updatePWLayout);
                builder.show();
                break;
            case R.id.btnCreate: // 암호 설정
                cursor = db.rawQuery("SELECT * FROM " + DBHelper.PW_TABLE, null);
                String pwd = "";
                while (cursor.moveToNext()) {
                    pwd = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PW));
                }
                if (pwd.equals("")) {
                    builder.setTitle("암호 설정");
                    builder.setPositiveButton("생성", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText createPW = (EditText)newPWLayout.findViewById(R.id.createPW);
                            EditText checkPW = (EditText)newPWLayout.findViewById(R.id.checkPW);
                            if (createPW.getText().toString().equals(checkPW.getText().toString())) {
                                if (createPW.getText().toString().equals("")) {
                                    Toast.makeText(LockActivity.this, "암호를 입력하고 생성하세요", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.execSQL("INSERT INTO " + DBHelper.PW_TABLE + " VALUES (null, '" + createPW.getText().toString() + "');");
                                    Toast.makeText(LockActivity.this, "암호가 생성되었습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                AlertDialog.Builder warning = new AlertDialog.Builder(LockActivity.this);
                                warning.setTitle("비밀번호가 일치하지 않습니다.");
                                warning.setPositiveButton("확인", null);
                                warning.show();
                            }
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.setView(newPWLayout);
                    builder.show();
                }
                else {
                    builder.setTitle("이미 비밀번호가 존재합니다.");
                    builder.setPositiveButton("확인", null);
                    builder.show();
                }
                break;
            case R.id.btnOk:
                String check = "";
                if (etPw.getText().toString().equals("")) { // 입력 값이 없을 경우
                    builder.setTitle("암호를 입력하세요");
                    builder.setPositiveButton("확인", null);
                    builder.show();
                }
                else {
                    cursor = db.rawQuery("SELECT * FROM " + DBHelper.PW_TABLE, null);
                    while (cursor.moveToNext()) {
                        check = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PW));
                    }
                    if (check.equals("")) {
                        builder.setTitle("암호를 생성하세요");
                        builder.setPositiveButton("확인", null);
                        builder.show();
                    } else {
                        if (check.equals(etPw.getText().toString())) {
                            Intent intent = new Intent(LockActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            builder.setTitle("암호가 일치하지 않습니다.");
                            builder.setPositiveButton("확인", null);
                            builder.show();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
        if (helper != null) helper.close();
    }
}
