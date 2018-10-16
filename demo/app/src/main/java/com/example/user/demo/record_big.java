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

<<<<<<< HEAD
        show_tv.setTextSize(28);
=======
<<<<<<< HEAD
        show_tv.setTextSize(28);
=======
        show_tv.setTextSize(32);
>>>>>>> 0ac59c70c02d079402a385fbbe55d0f8390001a9
>>>>>>> 0ac44b83437ff6ea1801d5caaf1bebf4ddc9cd69
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

<<<<<<< HEAD
        end.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
=======
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
>>>>>>> 0ac44b83437ff6ea1801d5caaf1bebf4ddc9cd69
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
<<<<<<< HEAD
    public abstract class OnMultiClickListener implements View.OnClickListener{
        private static final int MIN_CLICK_DELAY_TIME = 1500;
        private long lastClickTime;
        public abstract void onMultiClick(View v);
        @Override
        public void onClick(View v) {
            long curClickTime = System.currentTimeMillis();
            if((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                lastClickTime = curClickTime;
                onMultiClick(v);
            }
        }
    }
=======
>>>>>>> 0ac44b83437ff6ea1801d5caaf1bebf4ddc9cd69
}
