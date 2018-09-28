package com.example.user.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class traintime extends AppCompatActivity {
    ListView list;
    Button change,R_serch,end;
    TextView tv,title;
    ArrayAdapter adapter;
    String Tstart,Tend;
    List<String> TrainType = new ArrayList<>(); //車種
    List<String> TrainNo = new ArrayList<>();   //車次
    List<String> StartTime = new ArrayList<>(); //起始
    List<String> EndTime = new ArrayList<>();   //到達
    List<String> WheelchairFlag = new ArrayList<>(); //是否有殘障座位
    List<String> show = new ArrayList<>();      //顯示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traintime);

        list = findViewById(R.id.Tlist);
        change = findViewById(R.id.change);
        R_serch = findViewById(R.id.R_seach);
        tv = findViewById(R.id.tv);
        title = findViewById(R.id.title);
        end = findViewById(R.id.pre);

        title.setTextSize(18);
        Tstart = time.start.getSelectedItem().toString();
        Tend = time.end.getSelectedItem().toString();
        tv.setText(Tstart + " 到 " +  Tend);
        tv.setTextSize(24);
        getTime();
        //判定是否有資料
        if(TrainNo.size() == 0){
            new AlertDialog.Builder(traintime.this).setTitle("確認視窗").setMessage("查無資料!")
                .setPositiveButton("確定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            Intent intent = new Intent();   //intent實體化
                            intent.setClass(traintime.this,time.class);
                            startActivity(intent);    //startActivity觸發換頁
                            time.code.clear();
                            finish();
                        }
                    }).show().setCancelable(false);
        }
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp;
                temp = time.code.get(0);
                time.code.set(0, time.code.get(1));
                time.code.set(1, temp);
                temp = Tstart;
                Tstart = Tend;
                Tend = temp;
                clear_list();
                SharedPreferences Ttime = getSharedPreferences("time", MODE_PRIVATE);
                Ttime.edit().putString("temp",Ttime.getString("start_select","0")) .commit();
                Ttime.edit().putString("start_select",Ttime.getString("end_select","0")) .commit();
                Ttime.edit().putString("end_select",Ttime.getString("temp","0")) .commit();
                tv.setText(Tstart + " 到 " + Tend);
                getTime();
            }
        });
        R_serch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear_list();
                getTime();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.code.clear();
                clear_list();
                Intent intent = new Intent();   //intent實體化
                intent.setClass(traintime.this,fourbtn.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
    }
    //取得臺鐵資料
    public void getTime() {
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try {
            String datas = URLEncoder.encode("first", "UTF-8")
                    + "=" + URLEncoder.encode(time.code.get(0), "UTF-8");
            datas += "&" + URLEncoder.encode("final", "UTF-8")
                    + "=" + URLEncoder.encode(time.code.get(1), "UTF-8");
            datas += "&" + URLEncoder.encode("date", "UTF-8")
                    + "=" + URLEncoder.encode(time.date.getText().toString(), "UTF-8");
            datas += "&" + URLEncoder.encode("time", "UTF-8")
                    + "=" + URLEncoder.encode(time.time.getText().toString(), "UTF-8");
            String result = dbConnector.execute("SearchTrain", datas).get();
            JSONArray records = new JSONArray(result);
            for(int i = 0;i < records.length();i++) {
                JSONObject record = records.getJSONObject(i);
                JSONObject type = record.getJSONObject("DailyTrainInfo");
                JSONObject type2 = type.getJSONObject("TrainTypeName");
                //是否有殘障座位
                if(type.getString("WheelchairFlag").equals("1")){
                    WheelchairFlag.add("有");
                }else {
                    WheelchairFlag.add("－");
                }
                //車種轉名
                TrainType.add(type2.getString("Zh_tw"));
                if(TrainType.get(i).equals("自強(推拉式自強號且無自行車車廂)") || TrainType.get(i).equals("自強")){
                    TrainType.set(i,"自強號");
                }else if (TrainType.get(i).equals("自強(普悠瑪)")){
                    TrainType.set(i,"普悠瑪");
                }else if(TrainType.get(i).equals("自強(太魯閣)")){
                    TrainType.set(i,"太魯閣");
                }else if(TrainType.get(i).equals("") || TrainType.get(i).equals("莒光(無身障座位)") || TrainType.get(i).equals("莒光(無身障座位 ,有自行車車廂)")){
                    TrainType.set(i,"莒光號");
                }else if(TrainType.get(i).equals("復興")){
                    TrainType.set(i,"復興號");
                }

                JSONObject no = record.getJSONObject("DailyTrainInfo");
                TrainNo.add(format(no.getString("TrainNo")));
                JSONObject startt = record.getJSONObject("OriginStopTime");
                StartTime.add(startt.getString("DepartureTime"));
                JSONObject endt = record.getJSONObject("DestinationStopTime");
                EndTime.add(endt.getString("ArrivalTime"));
            }
            list_show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    //秀資料
    public void list_show() throws ParseException {
        String temp;
        //排序
        for(int i = 0;i < StartTime.size();i++) {
            for(int j = 0;j < StartTime.size();j++) {
                if(StartTime.get(i).compareTo(StartTime.get(j)) < 0){
                    temp = StartTime.get(i);
                    StartTime.set(i, StartTime.get(j));
                    StartTime.set(j,temp);

                    temp = TrainType.get(i);
                    TrainType.set(i, TrainType.get(j));
                    TrainType.set(j,temp);

                    temp = TrainNo.get(i);
                    TrainNo.set(i, TrainNo.get(j));
                    TrainNo.set(j,temp);

                    temp = EndTime.get(i);
                    EndTime.set(i, EndTime.get(j));
                    EndTime.set(j,temp);

                    temp = WheelchairFlag.get(i);
                    WheelchairFlag.set(i, WheelchairFlag.get(j));
                    WheelchairFlag.set(j,temp);
                }
            }
        }
        for(int i = 0;i < StartTime.size();i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date dt1 = sdf.parse(StartTime.get(i).toString());
            Date dt2 = sdf.parse(EndTime.get(i).toString());
            long time1 = dt1.getTime();
            long time2 = dt2.getTime();
            long timeP = (time2 - time1) / 1000 / 60;
            if(timeP < 0){
                timeP += 1440;
            }
            show.add(TrainType.get(i) + "    " + TrainNo.get(i) + "    " + StartTime.get(i) + "    " + EndTime.get(i) + "    " +
                    time_format(timeP) + "分" + "    " + WheelchairFlag.get(i));
        }
        adapter = new ArrayAdapter(traintime.this, android.R.layout.simple_list_item_1,show);
        list.setAdapter(adapter);
    }
    //清除所有資料
    public void clear_list(){
        TrainType.clear();
        TrainNo.clear();
        StartTime.clear();
        EndTime.clear();
        WheelchairFlag.clear();
        show.clear();
        adapter.notifyDataSetChanged();
    }
    //車次格式
    public String format(String s) {
        if(s.length() == 3){
            s = s + "\t\t";
        }else if(s.length() == 2){
            s = s + "\t\t\t\t";
        }else if(s.length() == 1){
            s = s + "\t\t\t\t";
        }
        return s;
    }
    //show內的時間格式
    public String time_format(long x) {
        String s = "" + x;
        if (s.length() == 1) {
            s = "\t\t\t\t" + s;
        }else if(s.length() == 2){
            s = "\t\t" + s;
        }
        return s;
    }
    @Override
    //返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            time.code.clear();
            clear_list();
            Intent intent = new Intent();   //intent實體化
            intent.setClass(traintime.this,time.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}