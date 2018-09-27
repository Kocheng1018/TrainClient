package com.example.user.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public  class signup extends AppCompatActivity {
    private Button next;
    private EditText name, id, phone,email;
    private String sex = "0";
    private TextView haveac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        next = findViewById(R.id.next);
        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        haveac = findViewById(R.id.textView4);

        SharedPreferences member = getSharedPreferences("member", MODE_PRIVATE);
        name.setText(member.getString("name",""));
        id.setText(member.getString("id",""));
        phone.setText(member.getString("phone",""));
        email.setText(member.getString("email",""));

       haveac.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               openmainBack();
           }
       });
       next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCardId(id.getText().toString())){
                    if(phone.getText().toString().matches("") || email.getText().toString().matches("") || name.getText().toString().matches("")){
                        Toast.makeText(signup.this, "請輸入姓名,身分證字號,電話及緊急聯絡人", Toast.LENGTH_SHORT).show();
                 }else {
                        String sexV = id.getText().toString().substring(1,2);
                        if (sexV.equals(2)){
                            sex = "1";
                        }
                        SharedPreferences member = getSharedPreferences("member", MODE_PRIVATE);
                        member.edit()
                                .putString("sex", sex)
                                .putString("name", name.getText().toString())
                                .putString("id", id.getText().toString())
                                .putString("phone", phone.getText().toString())
                                .putString("email", email.getText().toString())
                                .commit();
                        openmain();
                    }
                }
                else {
                    Toast.makeText(signup.this, "身份證字號錯誤", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //轉換頁面
    public void openmain() {
        Intent intent = new Intent(this,signup4.class);
        startActivity(intent);
        finish();
    }

    //驗證身份證字號
    private boolean checkCardId(String id) {
        if (!id.matches("[A-Z][1-2][0-9]{8}")) {
            return false;
        }

        String newId = id.toUpperCase();
        //身分證第一碼代表數值
        int[] headNum = new int[]{
                1, 10, 19, 28, 37,
                46, 55, 64, 39, 73,
                82, 2, 11, 20, 48,
                29, 38, 47, 56, 65,
                74, 83, 21, 3, 12, 30};

        char[] headCharUpper = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z'
        };

        int index = Arrays.binarySearch(headCharUpper, newId.charAt(0));
        int base = 8;
        int total = 0;
        for (int i = 1; i < 10; i++) {
            int tmp = Integer.parseInt(Character.toString(newId.charAt(i))) * base;
            total += tmp;
            base--;
        }

        total += headNum[index];
        int remain = total % 10;
        int checkNum = (10 - remain) % 10;
        if (Integer.parseInt(Character.toString(newId.charAt(9))) != checkNum) {
            return false;
        }
        return true;
    }

    public void openmainBack() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();   //intent實體化
            intent.setClass(signup.this,MainActivity.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}

