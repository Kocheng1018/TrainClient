package com.example.user.demo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class time extends AppCompatActivity {
    Button b1,btn_send,timebtn,datebtn;
    public static TextView date,time;
    public static List<String> code = new ArrayList<>();
    int Year, Month, Day, hour, minute;
    public static Spinner start, end, block1, block2,block3;
    String start_select,end_select;
    NetworkInfo mNetworkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        final ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        date =  findViewById(R.id.txt_date);
        time = findViewById(R.id.txt_time);
        timebtn =  findViewById(R.id.timebtn);
        datebtn = findViewById(R.id.datebtn);
        btn_send = findViewById(R.id.btn_send);
        start = findViewById(R.id.start);
        end = findViewById(R.id.pre);
        block1 = findViewById(R.id.block1);
        block2 = findViewById(R.id.block2);
        block3 = findViewById(R.id.block3);
        final Calendar c = Calendar.getInstance();

        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        Date systemdate = new Date(System.currentTimeMillis());
        date.setText(simpleDateFormatDate.format(systemdate));

        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");
        Date systemtime = new Date(System.currentTimeMillis());
        time.setText(simpleDateFormatTime.format(systemtime));

        SharedPreferences Ttime = getSharedPreferences("time", MODE_PRIVATE);
        start_select = Ttime.getString("start_select","0");
        end_select = Ttime.getString("end_select","0");
        String bk1_select = Ttime.getString("bk1","0");
        String bk2_select = Ttime.getString("bk2","0");

        block1.setSelection(Integer.valueOf(bk1_select));
        block2.setSelection(Integer.valueOf(bk2_select));

        ArrayAdapter location = new ArrayAdapter(time.this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.traintype));
        block3.setAdapter(location);

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Year = c.get(Calendar.YEAR);
                Month = c.get(Calendar.MONTH);
                Day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(time.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear+1;
                        date.setText(year +"-"+format(monthOfYear)+"-"+ format(dayOfMonth));
                    }
                }, Year, Month, Day);
                datePickerDialog.show();
            }
        });

        timebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(time.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        time.setText(format(hourOfDay) + ":" + format(minute));
                    }
                },hour,minute, true);
                timePickerDialog.show();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if(start.getSelectedItem().toString().equals(end.getSelectedItem().toString())){
                    Toast tosat = Toast.makeText(time.this,"起迄站不可一樣!",Toast.LENGTH_SHORT);
                    tosat.show();
                }else{
                    SharedPreferences Ttime = getSharedPreferences("time", MODE_PRIVATE);
                    Ttime.edit().putString("start_select",start.getSelectedItem().toString()) .commit();
                    Ttime.edit().putString("end_select",end.getSelectedItem().toString()) .commit();
                    Ttime.edit().putString("bk1",String.valueOf(block1.getSelectedItemPosition())) .commit();
                    Ttime.edit().putString("bk2",String.valueOf(block2.getSelectedItemPosition())) .commit();
                    if(mNetworkInfo != null){
                        getTcode(start.getSelectedItem().toString());
                        getTcode(end.getSelectedItem().toString());
                        Intent intent = new Intent();   //intent實體化
                        intent.setClass(time.this,traintime.class);
                        startActivity(intent);    //startActivity觸發換頁
                        finish();
                    }else{
                        new AlertDialog.Builder(time.this)
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
            }
        });
        //車站設定(預設起站)
        block1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = block1.getSelectedItemPosition();
                int index = 0; //預設spinner位址
                ArrayAdapter location =  change(pos);
                start.setAdapter(location);
                for(int i = 0;i < location.getCount();i++){
                    if(location.getItem(i).equals(start_select)){
                        index = i;
                        break;
                    }
                }
                start.setSelection(index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //車站設定(預設終站)
        block2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = block2.getSelectedItemPosition();
                int index = 0; //預設spinner位址
                ArrayAdapter location =  change(pos);
                end.setAdapter(location);
                for(int i = 0;i < location.getCount();i++){
                    if(location.getItem(i).equals(end_select)){
                        index = i;
                        break;
                    }
                }
                end.setSelection(index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    //取得車站代碼
    public void getTcode(String Tname) {
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try {
            String datas = URLEncoder.encode("key", "UTF-8")
                    + "=" + URLEncoder.encode("changecode", "UTF-8");
            datas += "&" + URLEncoder.encode("Tname", "UTF-8")
                    + "=" + URLEncoder.encode(Tname, "UTF-8");
            String result = dbConnector.execute("action", datas).get();
            JSONArray records = new JSONArray(result);
            for(int i = 0;i < records.length();i++) {
                JSONObject record = records.getJSONObject(i);
                code.add(record.getString("code"));
            }
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
    //把spinner的資料對應進去
    public ArrayAdapter change(int pos){
        switch (pos) {
            case 0:
                ArrayAdapter location = new ArrayAdapter(time.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.north));
                return location;
            case 1:
                ArrayAdapter location1 = new ArrayAdapter(time.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.middle));
                return location1;
            case 2:
                ArrayAdapter location2 = new ArrayAdapter(time.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.south));
                return location2;
            case 3:
                ArrayAdapter location3 = new ArrayAdapter(time.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.east));
                return location3;
        }
        return null;
    }
    //時間日期格式
    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }
    @Override
    //返回鍵跳出退出訊息
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(time.this,fourbtn.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}
