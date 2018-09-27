package com.example.user.demo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class service extends AppCompatActivity {

    Button next,datebtn,timebtn;
    public static Spinner start, end, block1, block2;
    public static TextView time,date;
    TextView cancel;
    public static List<String> service_code = new ArrayList<>();
    int Year, Month, Day, hour, minute;
    String start_select,end_select;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        next = findViewById(R.id.next);
        start = findViewById(R.id.start);
        end = findViewById(R.id.pre);
        datebtn = findViewById(R.id.datebtn);
        timebtn = findViewById(R.id.timebtn);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        block1 = findViewById(R.id.block1);
        block2 = findViewById(R.id.block2);
        cancel = findViewById(R.id.cancel);

        final Calendar c = Calendar.getInstance();

        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        Date systemdate = new Date(System.currentTimeMillis());
        date.setText(simpleDateFormatDate.format(systemdate));

        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");
        Date systemtime = new Date(System.currentTimeMillis());
        time.setText(simpleDateFormatTime.format(systemtime));

        SharedPreferences service_Tcheck = getSharedPreferences("service_Tcheck", MODE_PRIVATE);
        start_select = service_Tcheck.getString("start_select","0");
        end_select = service_Tcheck.getString("end_select","0");
        String bk1_select = service_Tcheck.getString("bk1","0");
        String bk2_select = service_Tcheck.getString("bk2","0");

        block1.setSelection(Integer.valueOf(bk1_select));
        block2.setSelection(Integer.valueOf(bk2_select));

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Year = c.get(Calendar.YEAR);
                Month = c.get(Calendar.MONTH);
                Day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(service.this, new DatePickerDialog.OnDateSetListener() {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(service.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        time.setText(format(hourOfDay) + ":" + format(minute));
                    }
                },hour,minute, true);
                timePickerDialog.show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start.getSelectedItem().toString().equals(end.getSelectedItem().toString())) {
                    Toast tosat = Toast.makeText(service.this,"起迄站不可一樣!",Toast.LENGTH_SHORT);
                    tosat.show();
                }else {
                    SharedPreferences service_Tcheck = getSharedPreferences("service_Tcheck", MODE_PRIVATE);
                    service_Tcheck.edit().putString("start_select",start.getSelectedItem().toString()) .commit();
                    service_Tcheck.edit().putString("end_select",end.getSelectedItem().toString()).commit();
                    service_Tcheck.edit().putString("bk1",String.valueOf(block1.getSelectedItemPosition())) .commit();
                    service_Tcheck.edit().putString("bk2",String.valueOf(block2.getSelectedItemPosition())) .commit();
                    putData();
                    getTcode(start.getSelectedItem().toString());
                    getTcode(end.getSelectedItem().toString());
                    Intent intent = new Intent();   //intent實體化
                    intent.setClass(service.this,service_trainNo.class);
                    startActivity(intent);    //startActivity觸發換頁
                    finish();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(service.this,fourbtn.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        block1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = block1.getSelectedItemPosition();
                ArrayAdapter location =  change(pos);
                start.setAdapter(location);
                for(int i = 0;i < location.getCount();i++){
                    if(location.getItem(i).equals(start_select)){
                        start.setSelection(i);
                        break;
                    } else{
                        start.setSelection(0);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        block2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = block2.getSelectedItemPosition();
                ArrayAdapter location =  change(pos);
                end.setAdapter(location);
                for(int i = 0;i < location.getCount();i++){
                    if(location.getItem(i).equals(end_select)){
                        end.setSelection(i);
                        break;
                    }else{
                        end.setSelection(0);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    //把spinner的資料對應進去
    public ArrayAdapter change(int pos){
        switch (pos) {
            case 0:
                ArrayAdapter location = new ArrayAdapter(service.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.north));
                return location;

            case 1:
                ArrayAdapter location1 = new ArrayAdapter(service.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.middle));
                return location1;

            case 2:
                ArrayAdapter location2 = new ArrayAdapter(service.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.south));
                return location2;

            case 3:
                ArrayAdapter location3 = new ArrayAdapter(service.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.east));
                return location3;
        }
        return null;
    }
    public void putData(){
        SharedPreferences service = getSharedPreferences("service", MODE_PRIVATE);
        service.edit()
                .putString("start", start.getSelectedItem().toString())
                .putString("end", end.getSelectedItem().toString())
                .putString("date", date.getText().toString())
                .putString("time", time.getText().toString())
                .commit();
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
                service_code.add(record.getString("code"));
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
    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(service.this,fourbtn.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}