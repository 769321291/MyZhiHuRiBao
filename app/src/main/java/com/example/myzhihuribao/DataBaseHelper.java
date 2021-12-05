package com.example.myzhihuribao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context, "database", null, 4);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("TAG:", "创建数据库表！");

        String TABLE_USER = "create table user(account_id INTEGER PRIMARY KEY autoincrement,account text" + ",password text,name text,picture text);";
        sqLiteDatabase.execSQL(TABLE_USER);


        String TABLE_COLLECT = "create table collect(collect_id INTEGER PRIMARY KEY autoincrement,account_id text,id text,url text,image text,title text,hint text)";
        sqLiteDatabase.execSQL(TABLE_COLLECT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists user");
        onCreate(sqLiteDatabase);
    }
}
