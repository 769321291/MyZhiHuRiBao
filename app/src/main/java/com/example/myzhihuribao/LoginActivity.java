package com.example.myzhihuribao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Button bt_load;
    private EditText et_account;
    private EditText et_password;
    private TextView textView;
    private SharedPreferencesUtil check;





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorMain));
        setContentView(R.layout.layout_login);

        initUI();

        et_account = findViewById(R.id.et_1);
        et_password = findViewById(R.id.et_2);
        bt_load = findViewById(R.id.load);
        textView = findViewById(R.id.to_register);
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textView.getPaint().setAntiAlias(true);
        bt_load.setOnClickListener(this);
        textView.setOnClickListener(this);

    }

    private void initUI() {
        check = SharedPreferencesUtil.getInstance(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        int flag =0;
        switch (view.getId()) {
            case R.id.to_register:
                Intent intent_register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent_register);
                break;
            case R.id.load:

                String account_query = et_account.getText().toString();
                String password_compare = et_password.getText().toString();
                if (account_query.length()==0)
                {flag=1; Toast.makeText(LoginActivity.this, "请输入用户名！", Toast.LENGTH_SHORT).show(); return; }
                else if (password_compare.length()==0)
                {flag=1; Toast.makeText(LoginActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show(); return; }
                else {
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(LoginActivity.this);
                    SQLiteDatabase database = dataBaseHelper.getReadableDatabase();


                    Cursor cursor = database.query("user", new String[]{"account", "password"}, "account=?", new String[]{account_query}, null, null, null);
                    if (cursor.moveToFirst()) {


                        @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));

                        cursor.close();
                        database.close();


                        if (password.equals(password_compare)) {
                            flag = 1;
                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_LONG).show();

                            DataBaseHelper dataBaseHelper_1 = new DataBaseHelper(LoginActivity.this);
                            SQLiteDatabase database_1 = dataBaseHelper_1.getReadableDatabase();


                            Cursor cursor_1 = database_1.query("user", new String[]{"account_id"}, "account=?", new String[]{account_query}, null, null, null);
                            if (cursor_1.moveToFirst()) {
                                Intent intent_load = new Intent(LoginActivity.this, MainActivity.class);

                                @SuppressLint("Range") String account_id = cursor_1.getString(cursor_1.getColumnIndex("account_id"));
                                cursor_1.close();
                                database_1.close();

                                startActivity(intent_load);

                                check.setLogin(true);
                                check.setAccountId(account_id);

                                finish();
                            }
                        }
                        else {
                            Toast.makeText(this, "你输入的账号或密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(flag == 0) {
                        Toast.makeText(this, "你输入的账号或密码不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
        }
    }
}

