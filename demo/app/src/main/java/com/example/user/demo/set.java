package com.example.user.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class set extends AppCompatActivity {
    Button logout,setmember,setpwd,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        logout = findViewById(R.id.logout);
        setmember = findViewById(R.id.setmember);
        setpwd = findViewById(R.id.setpwd);
        back = findViewById(R.id.back);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(set.this)
                        .setTitle("確認視窗")
                        .setMessage("確定要登出嗎?")
                        .setPositiveButton("確定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {
                                        openmain();
                                        SharedPreferences acc = getSharedPreferences("acc", MODE_PRIVATE);
                                        acc.edit().clear().commit();
                                        SharedPreferences service_Tcheck = getSharedPreferences("service_Tcheck", MODE_PRIVATE);
                                        service_Tcheck.edit().clear().commit();
                                        SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                                        service.edit().clear().commit();
                                        SharedPreferences time = getSharedPreferences("time", MODE_PRIVATE);
                                        time.edit().clear().commit();
                                        finish();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which){
                                    }
                                }).show();
            }
        });
        setmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(set.this,setMember.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        setpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(set.this,setPwd.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(set.this,fourbtn.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
    }
    public void openmain() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(set.this,fourbtn.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}
