package com.example.user.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class record_big extends AppCompatActivity {
    TextView show_tv;
    Button end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_big);

        show_tv = findViewById(R.id.show_tv);
        end = findViewById(R.id.end);

        show_tv.setTextSize(32);
        show_tv.setText("日期 : " + record.sdate.get(record.check_index) + "\n"+
                "時間 :" + record.stime.get(record.check_index) + "\n" +
                "起始站 : " + record.sstart.get(record.check_index)+ "\n"+
                "終點站 : " + record.send.get(record.check_index)+ "\n"+
                "搭乘車次 : " + record.scode.get(record.check_index)+ "\n"+
                "申請服務 : \n");
        if(record.swheel.get(record.check_index).equals("1")){
            show_tv.append("\t\t\t\t輪椅服務\n");
        }
        if(record.scrutch.get(record.check_index).equals("1")){
            show_tv.append("\t\t\t\t拐杖服務\n");
        }
        if(record.sboard.get(record.check_index).equals("1")){
            show_tv.append("\t\t\t\t棧板服務\n");
        }
        if(record.shelp.get(record.check_index).equals("1")){
            show_tv.append("\t\t\t\t乘車幫助\n");
        }
        if(record.snotice.get(record.check_index).equals("1")){
            show_tv.append("\t\t\t\t下車提醒\n");
        }
        if(record.sseat.get(record.check_index).equals("1")){
            show_tv.append("\t\t\t\t博愛座位置\n");
        }

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.check_index = -1;
                Intent intent = new Intent();   //intent實體化
                intent.setClass(record_big.this,record.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
    }

    @Override
    //返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            record.check_index = -1;
            Intent intent = new Intent();   //intent實體化
            intent.setClass(record_big.this,record.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}
