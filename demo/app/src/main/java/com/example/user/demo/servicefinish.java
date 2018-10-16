package com.example.user.demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    NetworkInfo mNetworkInfo;
    ImageView image[] = new ImageView[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicefinish);
        final ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        apply = findViewById(R.id.apply);
        detail = findViewById(R.id.detail);
        back = findViewById(R.id.back);
        cancel = findViewById(R.id.cancel);

        image[0] = findViewById(R.id.image1);
        image[1] = findViewById(R.id.image2);
        image[2] = findViewById(R.id.image3);
        image[3] = findViewById(R.id.image4);
        image[4] = findViewById(R.id.image5);
        image[5] = findViewById(R.id.image6);

        showdata();

        apply.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if(mNetworkInfo != null) {
                    update();
                    SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
                    service.edit().clear().commit();
                    openmain();
                    finish();
                }else{
                    new AlertDialog.Builder(servicefinish.this)
                            .setTitle("網路偵測")
                            .setMessage("請檢查網路連線!")
                            .setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,int which) {
                                        }
                                    }).show();
                }
            }
        });

        back.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                backmain();
                finish();
            }
        });
        cancel.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
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
        String imgdata[] = {wheel,crutch,board,travelhelp,notice,seat};
        /*detail.setText("您所選服務為:" + "\n");
        detail.append(wheel + " ");
        detail.append(crutch + " ");
        detail.append(board + " ");
        detail.append(travelhelp + " ");
        detail.append(notice + " ");
        detail.append(seat + "\n");*/
        detail.append("日期:" + date + "\n");
        detail.append("時間:" + time + "\n");
        detail.append("起站:" + start + "\n");
        detail.append("迄站:" + end + "\n");
        detail.append("搭乘車次:" + trainNo + "\n");

        int count = 0;
        for(int i = 0;i < image.length;i++){
            if(imgdata[i].equals("輪椅服務")) {
                image[count].setImageResource(R.drawable.wheelchair);
                count++;
                continue;
            }else if(imgdata[i].equals("乘車幫助")){
                image[count].setImageResource(R.drawable.travelhelppic);
                count++;
                continue;
            }else if(imgdata[i].equals("拐杖服務")){
                image[count].setImageResource(R.drawable.crutch);
                count++;
                continue;
            }else if(imgdata[i].equals("博愛座位")){
                image[count].setImageResource(R.drawable.priority_seat_4);
                count++;
                continue;
            }else if(imgdata[i].equals("棧板服務")){
                image[count].setImageResource(R.drawable.boardpic);
                count++;
                continue;
            }else if(imgdata[i].equals("下車提醒")){
                image[count].setImageResource(R.drawable.remind);
                count++;
                continue;
            }
        }
        for(int i = count;i < image.length;i++){
            image[i].setVisibility(View.INVISIBLE);
        }
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
}
