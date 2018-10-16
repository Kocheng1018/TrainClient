package com.example.user.demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class traintime extends AppCompatActivity {
    ListView list;
    Button change,R_serch,end;
    TextView tv,title;
    SimpleAdapter adapter;
    String Tstart,Tend;
    String traintype = time.block3.getSelectedItem().toString();
    List<Map<String, Object>> show = new ArrayList();      //顯示
    NetworkInfo mNetworkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traintime);
        final ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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

        list_show();

        //判定是否有資料
        if(show.size() == 0){
            new AlertDialog.Builder(traintime.this)
                    .setTitle("確認視窗")
                    .setMessage("查無資料!")
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
        change.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if(mNetworkInfo != null){
                    Toast.makeText(getApplicationContext(),"起迄互換完成!",Toast.LENGTH_SHORT).show();
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
                    list_show();
                }else{
                    new AlertDialog.Builder(traintime.this)
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
        R_serch.setOnClickListener(new  OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if(mNetworkInfo != null){
                    clear_list();
                    Toast.makeText(getApplicationContext(),"重新查詢完成!",Toast.LENGTH_SHORT).show();
                    list_show();
                }else {
                    new AlertDialog.Builder(traintime.this)
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
        end.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
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
    private List<Map<String, Object>> getTime() {
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
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
                Map map = new HashMap();
                JSONObject record = records.getJSONObject(i);
                JSONObject type = record.getJSONObject("DailyTrainInfo");
                JSONObject type2 = type.getJSONObject("TrainTypeName");
                String temp = type2.getString("Zh_tw");
                switch (traintype){
                    case "全部車種":
                        break;
                    case "對號車種":
                        if(temp.equals("區間車") || temp.equals("區間快") || temp.equals("復興號") ) {
                            continue;
                        }
                        break;
                    case "非對號車種":
                        if(temp.equals("自強(推拉式自強號且無自行車車廂)") || temp.equals("自強") || temp.equals("自強(普悠瑪)") || temp.equals("自強(太魯閣)")
                                ||temp.equals("") || temp.equals("莒光(無身障座位)") || temp.equals("莒光(無身障座位 ,有自行車車廂)") || temp.equals("莒光(有身障座位)")) {
                            continue;
                        }
                        break;
                }

                //是否有殘障座位
                if(type.getString("WheelchairFlag").equals("1")){
                    map.put("img", R.drawable.chair);
                }else {
                    map.put("img", "");
                }

                //車種
                if(temp.equals("自強(推拉式自強號且無自行車車廂)") || temp.equals("自強")){
                    map.put("TrainType","自強號");
                }else if (temp.equals("自強(普悠瑪)")){
                    map.put("TrainType", "普悠瑪");
                }else if (temp.equals("區間車")){
                    map.put("TrainType", "區間車");
                }else if (temp.equals("區間快")){
                    map.put("TrainType", "區間快");
                }else if(temp.equals("自強(太魯閣)")){
                    map.put("TrainType", "太魯閣");
                }else if(temp.equals("") || temp.equals("莒光(無身障座位)") || temp.equals("莒光(有身障座位)") || temp.equals("莒光(無身障座位 ,有自行車車廂)")){
                    map.put("TrainType", "莒光號");
                }else if(temp.equals("復興")){
                    map.put("TrainType", "復興號");
                }

                JSONObject no = record.getJSONObject("DailyTrainInfo");
                map.put("TrainNo",no.getString("TrainNo"));
                JSONObject startt = record.getJSONObject("OriginStopTime");
                Date dt1 = sdf.parse(startt.getString("DepartureTime"));
                map.put("Start",startt.getString("DepartureTime"));
                JSONObject endt = record.getJSONObject("DestinationStopTime");
                Date dt2 = sdf.parse(endt.getString("ArrivalTime"));
                map.put("End",endt.getString("ArrivalTime"));

                //計算花多少時間
                long time1 = dt1.getTime();
                long time2 = dt2.getTime();
                long timeP = (time2 - time1) / 1000 / 60;
                if(timeP < 0){
                    timeP += 1440;
                }
                map.put("Cost",(timeP / 60)  +"小時" +  format_time((int) (timeP % 60)) +"分");
                show.add(map);
                sort(show);
            }
            return show;
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
        return show;
    }
    //秀資料
    public void list_show(){
        adapter = new SimpleAdapter(this, getTime(),
                R.layout.view2content,
                new String[] {"TrainNo","TrainType","Start","Cost","End","img"},
                new int[] {R.id.TrainNo,R.id.TrainType,R.id.Start,R.id.Cost,R.id.End,R.id.img});
        list.setAdapter(adapter);
    }
    //清除所有資料
    public void clear_list(){
        show.clear();
        adapter.notifyDataSetChanged();
    }
    //日期時間格式
    private String format_time(int x) {
        String s = "" + x;
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }
    //排序資料
    private static void sort(List<Map<String, Object>> data) {
        Collections.sort(data, new Comparator<Map>() {
            public int compare(Map o1, Map o2) {
                String a = (String) o1.get("Start");
                String b = (String) o2.get("Start");
                // 升序               
                return a.compareTo(b);
                // 降序               
                // return b.compareTo(a);           
            }
        });
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
    public abstract class OnMultiClickListener implements View.OnClickListener{
        private static final int MIN_CLICK_DELAY_TIME = 2000;
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