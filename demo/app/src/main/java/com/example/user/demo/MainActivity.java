package com.example.user.demo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{
    private Button sign; //登入鍵
    private Button signup;
    private EditText username,password; //帳號密碼
    private CheckBox remember;
    NetworkInfo mNetworkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        sign = findViewById(R.id.sign);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        remember = findViewById(R.id.remember);
        signup = findViewById(R.id.create);
        username.requestFocus();

        //檢查網路
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        SharedPreferences acc = getSharedPreferences("acc", MODE_PRIVATE);
        String account = acc.getString("account", "");
        String pwd = acc.getString("password", "");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
                )
        {
            Toast.makeText(getApplicationContext(),"沒有權限  請手動開啟定位權限",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE}, 100);
        }

        if(account != "" && pwd != ""){
            username.setText(account);
            password.setText(pwd);
            if(mNetworkInfo != null){
                login();
            }else{
                new AlertDialog.Builder(MainActivity.this)
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
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if(mNetworkInfo != null){
                    SharedPreferences acc = getSharedPreferences("acc", MODE_PRIVATE);
                    acc.edit().putString("account", username.getText().toString()) .commit();
                    if(remember.isChecked()){
                        acc.edit().putString("password", password.getText().toString()).commit();
                    }else {
                        acc.edit().putString("password", "").commit();
                    }
                    login();
                }else{
                    new AlertDialog.Builder(MainActivity.this)
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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(MainActivity.this, signup.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });

    }

    //連線
    public static class DBConnector extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            String file = params[0];
            String datas = params[1];
            String result = post(file, datas);
            return result;
        }
    }
    //Send Post
    public static String post(String file,String datas){
        String result = "";
        HttpURLConnection urlConnection = null;
        final StringBuilder builder = new StringBuilder();

        try{
            URL url = new URL("http://163.17.136.194/lunchparty/train/" + file + ".php");
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(datas);
            wr.flush();

            //從database開啟stream
            InputStream is = urlConnection.getInputStream();
            int d = -1;
            byte[] data= new byte[256];
            while((d = is.read(data)) > -1){
                builder.append(new String(data,0,d));
            }
            result = builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //登入
    public void login(){
        DBConnector dbConnector = new DBConnector();
        try{
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("login","UTF-8");
            datas += "&" + URLEncoder.encode("account","UTF-8")
                    + "=" + URLEncoder.encode(username.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("password","UTF-8")
                    + "=" + URLEncoder.encode(password.getText().toString(),"UTF-8");

            String result = dbConnector.execute("action",datas).get();
            JSONObject record = new JSONObject(result);

            if(record.getString("code").equals("1")){
                Toast tosat = Toast.makeText(MainActivity.this,"登入成功!",Toast.LENGTH_SHORT);
                tosat.show();
                openmain();
                finish();
            }else{
                Toast tosat = Toast.makeText(MainActivity.this,"帳號或密碼錯誤!",Toast.LENGTH_SHORT);
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
    //轉換頁面
    public void openmain() {
        Intent intent = new Intent(this,fourbtn.class);
        startActivity(intent);
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
