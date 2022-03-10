package com.example.liwk.permissiontest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.liwk.permissiontest.R;

public class AskForPermission extends AppCompatActivity {

    private Button button;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedIntanceState);
        setContentView(R.layout.permission_main);

        button = findViewById(R.id.permit_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try {
                //    //检测是否有写的权限
                //    int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE");
                //    if (permission != PackageManager.PERMISSION_GRANTED) {
                //        // 没有写的权限，去申请写的权限，会弹出对话框
                //        ActivityCompat.requestPermissions(, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                //    }
                //} catch (Exception e) {
                //    e.printStackTrace();
                //}
            }
        });

        //if (Build.VERSION.SDK_INT >= 23) {
        //    int REQUEST_CODE_CONTACT = 101;
        //    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //    //验证是否许可权限
        //    for (String str : permissions) {
        //        if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
        //            //申请权限
        //            this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
        //            return;
        //        }
        //    }
        //}
    }
}

