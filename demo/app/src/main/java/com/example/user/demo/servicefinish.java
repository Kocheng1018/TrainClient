package com.example.user.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class servicefinish extends AppCompatActivity {

    Button apply,back;
    TextView detail,cancel;
    String wheel, crutch, board, travelhelp, notice, seat, date,
            trainNo,time, code, account, start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicefinish);

        apply = findViewById(R.id.apply);
        detail = findViewById(R.id.detail);
        back = findViewById(R.id.back);
        cancel = findViewById(R.id.cancel);

        showdata();

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
                SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                service.edit().clear().commit();
                openmain();
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backmain();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backtomain();
                SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                service.edit().clear().commit();
                finish();
            }
        });
    }

    public void showdata(){
        account = getSharedPreferences("acc", MODE_PRIVATE)
                .getString("account", "");
        start = getSharedPreferences("service", MODE_PRIVATE)
                .getString("start", "");
        end = getSharedPreferences("service", MODE_PRIVATE)
                .getString("end", "");
        wheel = getSharedPreferences("service", MODE_PRIVATE)
                .getString("wheel", "");
        crutch = getSharedPreferences("service", MODE_PRIVATE)
                .getString("crutch", "");
        board = getSharedPreferences("service", MODE_PRIVATE)
                .getString("board", "");
        travelhelp = getSharedPreferences("service", MODE_PRIVATE)
                .getString("travelhelp", "");
        notice = getSharedPreferences("service", MODE_PRIVATE)
                .getString("notice", "");
        seat = getSharedPreferences("service", MODE_PRIVATE)
                .getString("seat", "");
        date = getSharedPreferences("service", MODE_PRIVATE)
                .getString("date", "");
        time = getSharedPreferences("service", MODE_PRIVATE)
                .getString("time", "");
        trainNo = getSharedPreferences("service", MODE_PRIVATE)
                .getString("TrainNo", "");

        detail.setText("您所選服務為:" + "\n");
        detail.append(wheel + " ");
        detail.append(crutch + " ");
        detail.append(board + " ");
        detail.append(travelhelp + " ");
        detail.append(notice + " ");
        detail.append(seat + "\n");
        detail.append("日期:" + date + "\n");
        detail.append("時間:" + time + "\n");
        detail.append("起站:" + start + "\n");
        detail.append("迄站:" + end + "\n");
        detail.append("搭乘車次:" + trainNo + "\n");
    }
    private void update(){
        wheel = change(wheel);
        crutch = change(crutch);
        board = change(board);
        travelhelp = change(travelhelp);
        notice = change(notice);
        seat = change(seat);

        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try {
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("service","UTF-8");
            datas += "&" + URLEncoder.encode("account","UTF-8")
                    + "=" + URLEncoder.encode(account.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("date","UTF-8")
                    + "=" + URLEncoder.encode(date.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("time","UTF-8")
                    + "=" + URLEncoder.encode(time.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("trainNo","UTF-8")
                    + "=" + URLEncoder.encode(trainNo.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("start","UTF-8")
                    + "=" + URLEncoder.encode(start.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("end","UTF-8")
                    + "=" + URLEncoder.encode(end.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("wheel","UTF-8")
                    + "=" + URLEncoder.encode(wheel.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("crutch","UTF-8")
                    + "=" + URLEncoder.encode(crutch.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("board","UTF-8")
                    + "=" + URLEncoder.encode(board.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("travelhelp","UTF-8")
                    + "=" + URLEncoder.encode(travelhelp.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("notice","UTF-8")
                    + "=" + URLEncoder.encode(notice.toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("seat","UTF-8")
                    + "=" + URLEncoder.encode(seat.toString(),"UTF-8");

            String result = dbConnector.execute("action", datas).get();
            JSONObject record = new JSONObject(result);

            if(record.getString("code").equals("1")){
                Toast tosat = Toast.makeText(servicefinish.this,"成功!",Toast.LENGTH_SHORT);
                tosat.show();
            }else{
                Toast tosat = Toast.makeText(servicefinish.this,"失敗!",Toast.LENGTH_SHORT);
                tosat.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private String change(String a){
        if(String.valueOf(a) == "")
            a = "0";
        else
            a = "1";
        return a;
    }
    public void openmain() {
        Intent intent = new Intent(this,fourbtn.class);
        startActivity(intent);
    }
    public void backmain(){
        Intent intent = new Intent(this,service1.class);
        startActivity(intent);
    }
    public void backtomain() {
        Intent intent = new Intent(this,fourbtn.class);
        startActivity(intent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(servicefinish.this,service1.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}
