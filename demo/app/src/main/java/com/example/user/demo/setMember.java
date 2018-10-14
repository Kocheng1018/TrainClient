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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class setMember extends AppCompatActivity {
    EditText name,phone,sosphone;
    Button back,send;
    NetworkInfo mNetworkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_member);
        final ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        sosphone = findViewById(R.id.sosphone);
        back = findViewById(R.id.back);
        send = findViewById(R.id.send);
        getOldData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(setMember.this,set.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().length() != 10) {
                    Toast.makeText(getApplicationContext(), "手機格式錯誤!", Toast.LENGTH_SHORT).show();
                }else if(sosphone.getText().length() != 10){
                    Toast.makeText(getApplicationContext(), "緊急聯絡人電話格式錯誤!", Toast.LENGTH_SHORT).show();
                }else{
                    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                    if(mNetworkInfo != null) {
                        sendNewData();
                    }else{
                        new AlertDialog.Builder(setMember.this)
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
    }
    public void getOldData(){
        String account = getSharedPreferences("acc", MODE_PRIVATE)
                .getString("account", "");
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try{
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("getolddata","UTF-8");
            datas += "&" + URLEncoder.encode("member_no","UTF-8")
                    + "=" + URLEncoder.encode(account,"UTF-8");
            String result = dbConnector.execute("action",datas).get();
            JSONArray records = new JSONArray(result);
            JSONObject record = records.getJSONObject(0);
            name.setText(record.getString("name"));
            phone.setText(record.getString("phone"));
            sosphone.setText(record.getString("sosphone"));
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
    public void sendNewData(){
        String account = getSharedPreferences("acc", MODE_PRIVATE)
                .getString("account", "");
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try{
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("sendnewdata","UTF-8");
            datas += "&" + URLEncoder.encode("member_no","UTF-8")
                    + "=" + URLEncoder.encode(account,"UTF-8");
            datas += "&" + URLEncoder.encode("newname","UTF-8")
                    + "=" + URLEncoder.encode(name.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("newphone","UTF-8")
                    + "=" + URLEncoder.encode(phone.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("newSOSphone","UTF-8")
                    + "=" + URLEncoder.encode(sosphone.getText().toString(),"UTF-8");
            String result = dbConnector.execute("action",datas).get();
            JSONObject record = new JSONObject(result);

            if(record.getString("code").equals("1")){
                Toast tosat = Toast.makeText(setMember.this,"修改會員資料成功!",Toast.LENGTH_SHORT);
                tosat.show();
                Intent intent = new Intent();   //intent實體化
                intent.setClass(setMember.this,fourbtn.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }else{
                Toast tosat = Toast.makeText(setMember.this,"修改失敗!",Toast.LENGTH_SHORT);
                tosat.show();
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
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    @Override
    //返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(setMember.this,set.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}
