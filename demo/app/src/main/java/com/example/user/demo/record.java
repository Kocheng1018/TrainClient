package com.example.user.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class record extends AppCompatActivity {
    TextView title,show_tv;
    ListView record_list;
    Button btnback,delbtn;
    int check_index;
    ArrayAdapter adapter;
    List<String> sNo = new ArrayList<>();
    List<String> sdate = new ArrayList<>();
    List<String> stime = new ArrayList<>();
    List<String> sstart = new ArrayList<>();
    List<String> scode = new ArrayList<>();
    List<String> send = new ArrayList<>();
    List<String> swheel = new ArrayList<>();
    List<String> scrutch = new ArrayList<>();
    List<String> sboard = new ArrayList<>();
    List<String> shelp = new ArrayList<>();
    List<String> snotice = new ArrayList<>();
    List<String> sseat = new ArrayList<>();
    List<String> show = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        title = findViewById(R.id.title);
        show_tv = findViewById(R.id.show_tv);
        btnback = findViewById(R.id.btnback);
        delbtn = findViewById(R.id.delbtn);
        record_list = findViewById(R.id.record_list);
        title.setTextSize(20);
        recordData();

        record_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView arg0, View view, int arg2, long arg3) {
                check_index = arg2;
                show_tv.setTextSize(16);
                show_tv.setText("日期 : " + sdate.get(check_index) + "\n"+
                        "時間 :" + stime.get(check_index) + "\n" +
                        "起始站 : " + sstart.get(check_index)+ "\n"+
                        "終點站 : " + send.get(check_index)+ "\n"+
                        "搭乘車次 : " + scode.get(check_index)+ "\n"+
                        "申請服務 : \n");
                if(swheel.get(check_index).equals("1")){
                    show_tv.append("\t\t\t\t輪椅服務\n");
                }
                if(scrutch.get(check_index).equals("1")){
                    show_tv.append("\t\t\t\t拐杖服務\n");
                }
                if(sboard.get(check_index).equals("1")){
                    show_tv.append("\t\t\t\t棧板服務\n");
                }
                if(shelp.get(check_index).equals("1")){
                    show_tv.append("\t\t\t\t乘車幫助\n");
                }
                if(snotice.get(check_index).equals("1")){
                    show_tv.append("\t\t\t\t下車提醒\n");
                }
                if(sseat.get(check_index).equals("1")){
                    show_tv.append("\t\t\t\t博愛座位置\n");
                }
            }
        });
        delbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(check_index != -1){
                    Del();
                }else{
                    show_tv.setText("未選取某筆紀錄!");
                }
                check_index = -1;
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(record.this,fourbtn.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });

    }
    //取得紀錄
    public void recordData(){
        String account = getSharedPreferences("acc", MODE_PRIVATE)
                .getString("account", "");
        String temp_status;
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try{
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("record","UTF-8");
            datas += "&" + URLEncoder.encode("member_record","UTF-8")
                    + "=" + URLEncoder.encode(account,"UTF-8");
            String result = dbConnector.execute("action",datas).get();
            JSONArray records = new JSONArray(result);
            for(int i = 0;i < records.length();i++){
                JSONObject record = records.getJSONObject(i);
                temp_status = record.getString("status");
                if(temp_status.equals("0")) {
                    sNo.add(record.getString("num"));
                    sdate.add(record.getString("date"));
                    stime.add(record.getString("time"));
                    scode.add(record.getString("train_code"));
                    sstart.add(record.getString("start"));
                    send.add(record.getString("end"));
                    swheel.add(record.getString("wheel"));
                    scrutch.add(record.getString("crutch"));
                    sboard.add(record.getString("board"));
                    shelp.add(record.getString("travelhelp"));
                    snotice.add(record.getString("notice"));
                    sseat.add(record.getString("seat"));
                }
            }
            for(int i = 0;i < sNo.size();i++){
                show.add("編號 : " + sNo.get(i) + "\t\t\t\t日期 : " + sdate.get(i) + "\t\t\t\t時間 : " + stime.get(i));
            }
            adapter = new ArrayAdapter(record.this, android.R.layout.simple_list_item_1,show);
            record_list.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //刪除紀錄
    public void Del(){
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try {
            String datas = URLEncoder.encode("key", "UTF8")
                    + "=" + URLEncoder.encode("del_record", "UTF8");
            datas +=  "&" + URLEncoder.encode("record_no", "UTF8")
                    + "=" + URLEncoder.encode(sNo.get(check_index), "UTF8");
            String result = dbConnector.execute("action", datas).get();
            JSONObject record = new JSONObject(result);
            if (record.getString("code").equals("1")) {
                sNo.remove(check_index);
                sdate.remove(check_index);
                stime.remove(check_index);
                scode.remove(check_index);
                sstart.remove(check_index);
                send.remove(check_index);
                swheel.remove(check_index);
                scrutch.remove(check_index);
                sboard.remove(check_index);
                shelp.remove(check_index);
                snotice.remove(check_index);
                sseat.remove(check_index);
                show.remove(check_index);
                adapter.notifyDataSetChanged();
                show_tv.setText("已刪除!");
            } else {
                Toast tosat = Toast.makeText(record.this, "刪除失敗!", Toast.LENGTH_SHORT);
                tosat.show();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(record.this,fourbtn.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}
