package com.example.jiyeonhyun.ma02_20161016.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jiyeonhyun.ma02_20161016.R;

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = "DiaryDBHelper";

    private final static String DB_NAME = "diary_db";

    public final static String COL_ID = "_id";
    public final static String TABLE_NAME = "diary_table";
    public final static String PW_TABLE = "pw_table";
    public final static String COL_PW = "pwd";
    public final static String COL_IMG = "img";
    public final static String COL_DATE = "date";
    public final static String COL_WHETHER = "whether";
    public final static String COL_FEELING = "feeling";
    public final static String COL_CONTENTS = "contents";
    public final static String COL_LOCATION = "location";
    public final static String COL_MOVIE = "movie";
    public final static String COL_MONTH = "month";
    public final static String COL_COLOR = "color";
    public final static String COL_TYPE = "type";
    public final static String COL_YEAR = "year";
    public final static String COL_DAY = "day";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String pwdSql = "create table " + PW_TABLE + " ( _id integer primary key autoincrement," + COL_PW + " TEXT);";
        String createSql = "create table " + TABLE_NAME + " ( _id integer primary key autoincrement,"
                + COL_DATE + " TEXT, " + COL_WHETHER + " TEXT, " + COL_FEELING + " Integer, " + COL_IMG + " TEXT, " + COL_CONTENTS + " TEXT, " + COL_YEAR + " TEXT, " + COL_MONTH + " TEXT, " + COL_DAY + " TEXT, " + COL_MOVIE + " TEXT, " + COL_COLOR + " TEXT, " + COL_TYPE + " TEXT, " +  COL_LOCATION + " TEXT);";

        Log.d(TAG, createSql);
        Log.d(TAG, pwdSql);

        db.execSQL(createSql);
        db.execSQL(pwdSql);

        /*db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '2018/03/02', '애매하게 추움', " + R.mipmap.battery_50 + ", " + R.mipmap.sad + ", '개강이라니 말도 안 된다', '2018', '3', '2', '아이언맨', 'black', 'normal', '학교');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '2018/05/12', '슬슬 더움', " + R.mipmap.battery_25 + ", " + R.mipmap.olly + ", '다음 날이 종강이었으면 좋겠다', '2018', '5', '12', '토르', 'blue', 'italic', '학교');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '2018/12/25', '쌀쌀함', " + R.mipmap.battery_100 + ", " + R.mipmap.christmas + ", '크리스마스보다 종강이라서 행복해', '2018', '12', '25', '나 홀로 집에', 'red', 'bold', '집');");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
