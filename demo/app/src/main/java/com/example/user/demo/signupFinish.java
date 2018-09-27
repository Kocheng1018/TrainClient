package com.example.user.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class signupFinish extends AppCompatActivity {

    private TextView detail, account, password, name, sex, id, phone, email;
    private Button previous, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_finish);

        detail = findViewById(R.id.detail);
        previous = findViewById(R.id.previous);
        register = findViewById(R.id.register);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        sex = findViewById(R.id.sex);
        id = findViewById(R.id.id);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);


        showdata();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
                openMain();

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmainBack();
            }
        });
    }
    //取得傳遞過來的資料
    public void showdata(){
        String name1 = getSharedPreferences("member", MODE_PRIVATE)
                .getString("name", "");
        String sex1 = getSharedPreferences("member", MODE_PRIVATE)
                .getString("sex", "");
        String id1 = getSharedPreferences("member", MODE_PRIVATE)
                .getString("id", "");
        String phone1 = getSharedPreferences("member", MODE_PRIVATE)
                .getString("phone", "");
        String account1 = getSharedPreferences("member", MODE_PRIVATE)
                .getString("account", "");
        String password1 = getSharedPreferences("member", MODE_PRIVATE)
                .getString("password", "");
        String email1 = getSharedPreferences("member", MODE_PRIVATE)
                .getString("email", "");

        account.setText(account1);
        password.setText(password1);
        name.setText(name1);
        if(sex1.equals(1)){
            sex.setText("女");
        }else{
            sex.setText("男");
        }
//        sex.setText(sex1);
        id.setText(id1);
        phone.setText(phone1);
        email.setText(email1);
    }

    public void signup(){
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try{
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("signup","UTF-8");
            datas += "&" + URLEncoder.encode("account","UTF-8")
                    + "=" + URLEncoder.encode(account.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("password","UTF-8")
                    + "=" + URLEncoder.encode(password.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("name","UTF-8")
                    + "=" + URLEncoder.encode(name.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("id","UTF-8")
                    + "=" + URLEncoder.encode(id.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("sex","UTF-8")
                    + "=" + URLEncoder.encode(sex.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("phone","UTF-8")
                    + "=" + URLEncoder.encode(phone.getText().toString(),"UTF-8");
            datas += "&" + URLEncoder.encode("sosphone","UTF-8")
                    + "=" + URLEncoder.encode(email.getText().toString(),"UTF-8");

            String result = dbConnector.execute("action", datas).get();
            JSONObject record = new JSONObject(result);

            if(record.getString("code").equals("1")){
                Toast tosat = Toast.makeText(signupFinish.this,"註冊成功!",Toast.LENGTH_SHORT);
                tosat.show();
                SharedPreferences member = getSharedPreferences("member", MODE_PRIVATE);
                member.edit().clear().commit();
            }else{
                Toast tosat = Toast.makeText(signupFinish.this,"註冊失敗!",Toast.LENGTH_SHORT);
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

    //轉換頁面上一頁
    public void openmainBack() {
        Intent intent = new Intent(this,signup4.class);
        startActivity(intent);
        finish();
    }

    public void openMain(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(signupFinish.this,signup4.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }

}