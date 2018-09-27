package com.example.user.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class service1 extends AppCompatActivity {
    ImageButton wheelpic, crutchpic, boardpic, travelhelppic, noticepic, seatpic;
    Switch wheel, crutch, board, travelhelp, notice, seat;
    Button next,back;
    TextView cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service1);

        wheelpic = findViewById(R.id.wheelpic);
        crutchpic = findViewById(R.id.crutchpic);
        boardpic = findViewById(R.id.boardpic);
        travelhelppic = findViewById(R.id.travelhelppic);
        noticepic = findViewById(R.id.noticepic);
        seatpic = findViewById(R.id.seatpic);
        wheel = findViewById(R.id.wheel);
        crutch = findViewById(R.id.crutch);
        board = findViewById(R.id.board);
        travelhelp = findViewById(R.id.travelhelp);
        notice = findViewById(R.id.notice);
        seat = findViewById(R.id.seat);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        cancel = findViewById(R.id.cancel);

        SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
        int wheel_check = Integer.valueOf(service.getString("wheel_check","0"));
        int crutch_check = Integer.valueOf(service.getString("crutch_check","0"));
        int board_check = Integer.valueOf(service.getString("board_check","0"));
        int travelhelp_check = Integer.valueOf(service.getString("travelhelp_check","0"));
        int notice_check = Integer.valueOf(service.getString("notice_check","0"));
        int seat_check = Integer.valueOf(service.getString("seat_check","0"));
        if(wheel_check == 1) {
            choose(wheel);
        }
        if(crutch_check == 1) {
            choose(crutch);
        }
        if(board_check == 1) {
            choose(board);
        }
        if(travelhelp_check == 1) {
            choose(travelhelp);
        }
        if(notice_check == 1) {
            choose(notice);
        }if(seat_check == 1) {
            choose(seat);
        }
        wheelpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(wheel);
            }
        });
        crutchpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(crutch);
            }
        });
        boardpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(board);
            }
        });
        travelhelppic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(travelhelp);
            }
        });
        noticepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(notice);
            }
        });
        seatpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(seat);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backtomain();
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(service1.this,service_trainNo.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(wheel.isChecked()) {
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("wheel", "輪椅服務").commit();
                    service.edit().putString("wheel_check", "1").commit();
                }else{
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("wheel", "").commit();
                    service.edit().putString("wheel_check", "0").commit();
                }

                if(crutch.isChecked()) {
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("crutch", "拐杖服務").commit();
                    service.edit().putString("crutch_check", "1").commit();
                }else{
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("crutch", "").commit();
                    service.edit().putString("crutch_check", "0").commit();
                }

                if(board.isChecked()) {
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("board", "棧板服務").commit();
                    service.edit().putString("board_check", "1").commit();
                }else{
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("board", "").commit();
                    service.edit().putString("board_check", "0").commit();
                }

                if(travelhelp.isChecked()) {
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("travelhelp", "乘車幫助").commit();
                    service.edit().putString("travelhelp_check", "1").commit();
                }else{
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("travelhelp", "").commit();
                    service.edit().putString("travelhelp_check", "0").commit();
                }

                if(notice.isChecked()) {
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("notice", "下車提醒").commit();
                    service.edit().putString("notice_check", "1").commit();
                }else{
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("notice", "").commit();
                    service.edit().putString("notice_check", "0").commit();
                }

                if(seat.isChecked()) {
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("seat", "博愛座位").commit();
                    service.edit().putString("seat_check", "1").commit();
                }else{
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().putString("seat", "").commit();
                    service.edit().putString("seat_check", "0").commit();
                }
                if(!(wheel.isChecked()) && !(crutch.isChecked()) && !(board.isChecked()) && !(travelhelp.isChecked()) && !(notice.isChecked()) && !(seat.isChecked())){
                    Toast tosat = Toast.makeText(service1.this,"未選取任何服務!",Toast.LENGTH_SHORT);
                    tosat.show();
                }else {
                    openmain();
                    finish();
                }
            }
        });
    }

    //連接checkbox和switch
    private void choose(Switch sw){
        if(sw.isChecked()){
            sw.setChecked(false);
        }
        else {
            sw.setChecked(true);
        }
    }
    //轉換頁面
    public void openmain(){
        Intent intent = new Intent(this,servicefinish.class);
        startActivity(intent);
    }
    public void backtomain() {
        Intent intent = new Intent(this,fourbtn.class);
        startActivity(intent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(service1.this,service_trainNo.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}
