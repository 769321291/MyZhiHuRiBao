package com.example.myzhihuribao;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener{
    private String base;
    private String real;
    private String id;
    private String url;
    private String hint;
    private String image;
    private String title;
    private String count_collect="0";
    private String count_like ="0";
    private RelativeLayout back;
    private RelativeLayout like;
    private RelativeLayout comment;
    private RelativeLayout collect;
    private Intent intent_comment;
    private ImageView set_liked;
    private ImageView set_collected;
    private TextView num_like;
    private TextView num_comment;
    private String long_comments;
    private String short_comments;
    private String popularity;
    int num_p;
    private String account_id;
    private SharedPreferencesUtil check;
    Intent backToMain;
    ImageButton back_tomain;


    @RequiresApi(api = Build.VERSION_CODES.M)
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

        Intent intent = getIntent();


        check= SharedPreferencesUtil.getInstance(getApplicationContext());
        account_id = check.getAccountId();
        id = intent.getStringExtra("id");
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        hint = intent.getStringExtra("hint");
        image = intent.getStringExtra("image");
        WebView webView = findViewById(R.id.webview);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        back = (RelativeLayout) findViewById(R.id.back);
        like = (RelativeLayout) findViewById(R.id.like);
        comment = (RelativeLayout) findViewById(R.id.comment);
        collect = (RelativeLayout) findViewById(R.id.collect);
        set_liked = (ImageView) findViewById(R.id.imageView_like);
        set_collected = (ImageView) findViewById(R.id.imageView_collect);
        num_like = (TextView) findViewById(R.id.textView_like);
        num_comment = (TextView) findViewById(R.id.textView_comment);
        back_tomain = (ImageButton)findViewById(R.id.back_tomain);
        base = "https://news-at.zhihu.com/api/3/story-extra/";



        intent_comment = new Intent(NewsActivity.this, CommentsActivity.class);
        backToMain = new Intent(NewsActivity.this,MainActivity.class);
        collect.setOnClickListener(this);
        comment.setOnClickListener(this);
        like.setOnClickListener(this);
        back_tomain.setOnClickListener(this);



        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    real = base + id;
                    URL url = new URL(real);
//                    URL url = new URL(real);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponse(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();


    }


    public void showResponse(final String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            long_comments = jsonObject.getString("long_comments");
            short_comments = jsonObject.getString("short_comments");
            popularity = jsonObject.getString("popularity");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    num_like.setText(popularity);
                    num_comment.setText(Integer.toString(Integer.valueOf(short_comments) + Integer.valueOf(long_comments)));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.collect:
                if (check.isLogin()) {
                    if (count_collect.equals("0")) {
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(NewsActivity.this);
                        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
                        database.execSQL("insert into collect(account_id,id,url,image,title,hint) values('" + account_id + "','" + id + "','" + url + "','" + image + "','" + title + "','" + hint + "');");
                        database.close();
                        Toast.makeText(this, "收藏成功！", Toast.LENGTH_SHORT).show();
                        set_collected.setImageDrawable(getResources().getDrawable(R.drawable.collected));
                        count_collect = "1";
                        break;
                    } else {
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(NewsActivity.this);
                        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
                        database.delete("collect","account_id=?",new String[]{"'"+account_id+"'"});
                        Log.i("TAG:", "删除成功");
                        database.close();
                        Log.i("TAG:", "删除成功2");
                        set_collected.setImageDrawable(getResources().getDrawable(R.drawable.collect));
                        count_collect = "0";
                        break;
                    }
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.comment:
                Bundle bundle = new Bundle();
                bundle.putString("long_comments", long_comments);
                bundle.putString("short_comments", short_comments);
                bundle.putString("id", id);
                intent_comment.putExtras(bundle);
                startActivity(NewsActivity.this.intent_comment);
                break;
            case R.id.like:
                if (count_like.equals("0")) {
                    set_liked.setImageDrawable(getResources().getDrawable(R.drawable.liked));
                    num_like.setText(Integer.toString(Integer.valueOf(popularity) + 1));
                    Toast.makeText(this, "点赞成功！", Toast.LENGTH_SHORT).show();
                    count_like = "1";
                    break;
                }else {
                    set_liked.setImageDrawable(getResources().getDrawable(R.drawable.likes));
                    num_like.setText(Integer.toString(Integer.valueOf(popularity)));
                    count_like = "0";
                    break;
                }
            case R.id.back_tomain:
                startActivity(NewsActivity.this.backToMain);
        }

    }

}