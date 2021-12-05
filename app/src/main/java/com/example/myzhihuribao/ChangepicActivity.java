package com.example.myzhihuribao;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ChangepicActivity extends AppCompatActivity implements View.OnClickListener {
    RoundImageView ivAvatar;
    RoundImageView main_pic;
    RoundImageView myself_pic;
    Button photoButton;
    Button picButton;
    Intent backToMyself;
    ImageButton back;

    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "user_photo.png";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。（生成bitmap貌似有时要报错？可试下把大小弄小点）
    private static int output_X = 480;
    private static int output_Y = 480;

    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepic);
        ivAvatar = (RoundImageView) findViewById(R.id.ivAvatar);
        main_pic = (RoundImageView) findViewById(R.id.imageView) ;
        myself_pic =(RoundImageView) findViewById(R.id.change_picture);

        photoButton = (Button) findViewById(R.id.btShot);
        picButton = (Button) findViewById(R.id.btAlbum);
        back = (ImageButton)findViewById(R.id.back);
        photoButton.setOnClickListener(this);
        picButton.setOnClickListener(this);
        back.setOnClickListener(this);
        backToMyself = new Intent(ChangepicActivity.this,MyselfActivity.class);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //拍照
            case R.id.btShot:
                choseHeadImageFromCameraCapture();
                break;
            //图片选取
            case R.id.btAlbum:
                choseHeadImageFromGallery();
                break;

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ChangepicActivity.this.backToMyself);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;

            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(this,"没有SDCard!",Toast.LENGTH_LONG).show();
                }

                break;

            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            ivAvatar.setImageBitmap(photo);
//            myself_pic.setImageBitmap(photo);
            main_pic.setImageBitmap(photo);
        }
    }

    // 将图片转换成base64编码
    public String getBase64(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //压缩的质量为60%
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, out);
        //生成base64字符
        String base = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
        return base;
    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK);
        // 设置文件类型
        intentFromGallery.setType("image/*");
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent("android.media.action.IMAGE_CAPTURE");

        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(Environment
                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }

        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    public boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }
}
