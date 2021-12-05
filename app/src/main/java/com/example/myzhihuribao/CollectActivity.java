package com.example.myzhihuribao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String account_id;
    private ImageView back;
    private SharedPreferencesUtil check;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_collects);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorMain));

        initUI();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DataBaseHelper dataBaseHelper = new DataBaseHelper(CollectActivity.this);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
        List<Map<String, Object>> list = new ArrayList<>();

        Cursor cursor = database.query("collect", new String[]{"id", "url", "image", "title", "hint"}, "account_id=?", new String[]{account_id}, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
            Log.i("TAG:", id);
            @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
            Log.i("TAG:", url);
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            Log.i("TAG:", title);
            @SuppressLint("Range") String image = cursor.getString(cursor.getColumnIndex("image"));
            Log.i("TAG:", image);
            @SuppressLint("Range") String hint = cursor.getString(cursor.getColumnIndex("hint"));
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", id);
            map1.put("url", url);
            map1.put("title", title);
            map1.put("image", image);
            map1.put("hint", hint);
            map1.put("type", 1);
            list.add(map1);
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id1 = cursor.getString(cursor.getColumnIndex("id"));
                Log.i("TAG:", id1);
                @SuppressLint("Range") String url1 = cursor.getString(cursor.getColumnIndex("url"));
                @SuppressLint("Range") String title1 = cursor.getString(cursor.getColumnIndex("title"));
                Log.i("TAG:", title1);
                @SuppressLint("Range") String image1 = cursor.getString(cursor.getColumnIndex("image"));
                @SuppressLint("Range") String hint1 = cursor.getString(cursor.getColumnIndex("hint"));
                Map<String, Object> map = new HashMap<>();
                map.put("id", id1);
                map.put("url", url1);
                map.put("title", title1);
                map.put("image", image1);
                map.put("hint", hint1);
                map.put("type", 1);
                list.add(map);
            }
        }
        cursor.close();
        database.close();
        recyclerView.setLayoutManager(new LinearLayoutManager(CollectActivity.this));
        recyclerView.setAdapter(new CollectAdapter(CollectActivity.this, list));

    }

    private void initUI() {
        check = SharedPreferencesUtil.getInstance(getApplicationContext());
        account_id = check.getAccountId();
        recyclerView = findViewById(R.id.recyclerView_myCollectList);
        back = findViewById(R.id.imageView_back);


    }


    public void onClick(View v) {

    }
}