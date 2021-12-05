package com.example.myzhihuribao;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MyselfActivity extends AppCompatActivity {


    private ImageButton back_button;
    private RelativeLayout collects_button;
    private RelativeLayout picture_button;
    private RelativeLayout name_button;
    private RelativeLayout quit_button;
    private Intent back;
    private Intent go_collects;
    private Intent change_picture;
    private Intent quit;
    private EditText edit;
    private String account_id;
    private SharedPreferencesUtil check;
    private String name_show;
    private TextView name;
    private RoundImageView my_pictures;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_myself);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorMain));


    }

    @SuppressLint("Range")
    protected void onResume() {
        super.onResume();
        name = (TextView) findViewById(R.id.name);
        back_button = (ImageButton) findViewById(R.id.back);
        collects_button = (RelativeLayout) findViewById(R.id.collect);
        name_button = (RelativeLayout) findViewById(R.id.change_name);
        quit_button = (RelativeLayout) findViewById(R.id.exit);
        my_pictures = (RoundImageView) findViewById(R.id.change_picture);

        check = SharedPreferencesUtil.getInstance(getApplicationContext());
        account_id = check.getAccountId();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MyselfActivity.this);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();

        quit = new Intent(MyselfActivity.this, LoginActivity.class);
        back = new Intent(MyselfActivity.this, MainActivity.class);
        go_collects = new Intent(MyselfActivity.this, CollectActivity.class);
        change_picture = new Intent(MyselfActivity.this, ChangepicActivity.class);

        account_id = check.getAccountId();
        Cursor cursor = database.query("user", new String[]{"name", "picture"}, "account_id = ?", new String[]{account_id}, null, null, null);
        if (cursor.moveToFirst()) {
            name_show = cursor.getString(cursor.getColumnIndex("name"));
            name.setText(name_show + "");
        }
        ;


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MyselfActivity.this.back);
                finish();
            }
        });
        collects_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MyselfActivity.this.go_collects);
            }
        });
        my_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MyselfActivity.this.change_picture);
            }
        });

        name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(MyselfActivity.this);
                final View view = factory.inflate(R.layout.layout_name, null);
                edit = view.findViewById(R.id.name);

                new AlertDialog.Builder(MyselfActivity.this)
                        .setTitle("请输入新的昵称")//提示框标题
                        .setView(view)
                        .setPositiveButton("确定",//提示框的两个按钮
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //事件
                                        if (edit.getText().toString().length() == 0) {
                                            Toast.makeText(MyselfActivity.this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else if (edit.getText().toString().length() > 8) {
                                            Toast.makeText(MyselfActivity.this, "昵称最多输入8个字符！", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            DataBaseHelper dataBaseHelper = new DataBaseHelper(MyselfActivity.this);
                                            SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
                                            ContentValues values = new ContentValues();
                                            values.put("name", edit.getText().toString());
                                            database.update("user", values, "account_id=?", new String[]{account_id});
                                            database.close();
                                            onResume();
                                        }


                                    }
                                }).setNegativeButton("取消", null).create().show();

            }
        });
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MyselfActivity.this.quit);
                finish();
            }
        });

    }

}
