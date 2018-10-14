package com.example.user.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class setPwd extends AppCompatActivity {
    TextView txt_title,title1,title2,title3;
    EditText edtxt1,edtxt2,edtxt3;
    Button btn_back,btn_upd;
    String oldPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);

        txt_title = findViewById(R.id.txt_title);
        title1 = findViewById(R.id.title1);
        title2 = findViewById(R.id.title2);
        title3 = findViewById(R.id.title3);
        edtxt1 = findViewById(R.id.edtxt1);
        edtxt2 = findViewById(R.id.edtxt2);
        edtxt3 = findViewById(R.id.edtxt3);
        btn_back = findViewById(R.id.btn_back);
        btn_upd = findViewById(R.id.btn_upd);

        txt_title.setTextSize(24);
        title1.setTextSize(16);
        title2.setTextSize(16);
        title3.setTextSize(16);
        getOldPwd();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(setPwd.this,set.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        btn_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(edtxt1.getText().toString().equals(oldPwd)) || edtxt1.getText().toString().equals("")){
                    Toast tosat = Toast.makeText(setPwd.this,"舊密碼輸入錯誤!",Toast.LENGTH_SHORT);
                    tosat.show();
                }else{
                    if(!(edtxt2.getText().toString().equals(edtxt3.getText().toString())) || edtxt2.getText().toString().equals("") || edtxt3.getText().toString().equals("")){
                        Toast tosat = Toast.makeText(setPwd.this,"兩次新密碼輸入不同!",Toast.LENGTH_SHORT);
                        tosat.show();
                    }else {
                        if(edtxt1.getText().toString().equals(edtxt2.getText().toString())){
                            Toast tosat = Toast.makeText(setPwd.this,"新密碼與舊密碼相同!",Toast.LENGTH_SHORT);
                            tosat.show();
                        }else{
                            sendNewPwd();
                        }
                    }
                }
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(setPwd.this,set.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
    //取得舊密碼
    public void getOldPwd(){
        String account = getSharedPreferences("acc", MODE_PRIVATE)
                .getString("account", "");
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try{
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("getoldpwd","UTF-8");
            datas += "&" + URLEncoder.encode("member_no","UTF-8")
                    + "=" + URLEncoder.encode(account,"UTF-8");
            String result = dbConnector.execute("action",datas).get();
            JSONArray records = new JSONArray(result);
            JSONObject record = records.getJSONObject(0);
            oldPwd = record.getString("password");
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
    public void sendNewPwd(){
        String account = getSharedPreferences("acc", MODE_PRIVATE)
                .getString("account", "");
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try{
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("sendnewpwd","UTF-8");
            datas += "&" + URLEncoder.encode("member_no","UTF-8")
                    + "=" + URLEncoder.encode(account,"UTF-8");
            datas += "&" + URLEncoder.encode("newpwd","UTF-8")
                    + "=" + URLEncoder.encode(edtxt2.getText().toString(),"UTF-8");
            String result = dbConnector.execute("action",datas).get();
            JSONObject record = new JSONObject(result);

            if(record.getString("code").equals("1")){
                Toast tosat = Toast.makeText(setPwd.this,"修改成功，下次開啟時將重新登入!",Toast.LENGTH_SHORT);
                tosat.show();
                SharedPreferences acc = getSharedPreferences("acc", MODE_PRIVATE);
                acc.edit().clear().commit();
                Intent intent = new Intent();   //intent實體化
                intent.setClass(setPwd.this,fourbtn.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }else{
                Toast tosat = Toast.makeText(setPwd.this,"修改失敗!",Toast.LENGTH_SHORT);
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
}
