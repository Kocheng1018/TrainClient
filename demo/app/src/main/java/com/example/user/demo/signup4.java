package com.example.user.demo;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class signup4 extends AppCompatActivity {
    private EditText account, password;
    private Button previous, next, accCheck;
    NetworkInfo mNetworkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);
        final ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        accCheck = findViewById(R.id.accCheck);

        SharedPreferences member = getSharedPreferences("member", MODE_PRIVATE);
        account.setText(member.getString("account",""));

        accCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    try {
                        if (accountCheck() == 1) {
                            Toast tosat = Toast.makeText(signup4.this, "帳號可以使用!", Toast.LENGTH_SHORT);
                            tosat.show();
                        } else if (accountCheck() == 0) {
                            Toast tosat = Toast.makeText(signup4.this, "帳號長度為6~12!", Toast.LENGTH_SHORT);
                            tosat.show();
                        } else if (accountCheck() == 2) {
                            Toast tosat = Toast.makeText(signup4.this, "此帳號已有使用者!", Toast.LENGTH_SHORT);
                            tosat.show();
                        } else {
                            Toast tosat = Toast.makeText(signup4.this, "ERROR!", Toast.LENGTH_SHORT);
                            tosat.show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }else{
                    new AlertDialog.Builder(signup4.this)
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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().matches("") || account.getText().toString().matches("")) {
                    Toast tosat = Toast.makeText(signup4.this, "請輸入帳號或密碼!", Toast.LENGTH_SHORT);
                    tosat.show();
                }else if(account.getText().length() < 6){
                    Toast.makeText(signup4.this, "帳號長度為6~12", Toast.LENGTH_SHORT).show();
                }else {
                    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                    if (mNetworkInfo != null) {
                        try {
                            if (accountCheck() == 1) {
                                if (password.getText().toString().matches("")) {
                                    Toast.makeText(signup4.this, "請輸入密碼", Toast.LENGTH_SHORT).show();
                                } else if (password.getText().length() < 6) {
                                    Toast.makeText(signup4.this, "密碼長度為6~12", Toast.LENGTH_SHORT).show();
                                } else {
                                    SharedPreferences member = getSharedPreferences("member", MODE_PRIVATE);
                                    member.edit()
                                            .putString("account", account.getText().toString())
                                            .putString("password", password.getText().toString())
                                            .commit();
                                    openmain();
                                }
                            } else if (accountCheck() != 1) {
                                Toast.makeText(signup4.this, "此帳號無法使用!", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(signup4.this, "ERROR!", Toast.LENGTH_SHORT);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else{
                        new AlertDialog.Builder(signup4.this)
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

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmainBack();
            }
        });

    }

    //帳號驗證
    public int accountCheck() throws JSONException, UnsupportedEncodingException, ExecutionException, InterruptedException {
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        String datas = URLEncoder.encode("key", "UTF-8")
                + "=" + URLEncoder.encode("check", "UTF-8");
        datas += "&" + URLEncoder.encode("account", "UTF-8")
                + "=" + URLEncoder.encode(account.getText().toString(), "UTF-8");
        String result = dbConnector.execute("action", datas).get();
        JSONObject record = new JSONObject(result);
        if(record.getString("code").equals("0") && account.getText().length() >= 6){
            return 1;
        }else if(account.getText().length() < 6){
            return 0;
        }else if(record.getString("code").equals("1")){
            return 2;
        }else {
            return 0;
        }
    }

    //轉換頁面
    public void openmain() {
        Intent intent = new Intent(this,signupFinish.class);
        startActivity(intent);
        finish();
    }

    //轉換頁面上一頁
    public void openmainBack() {
        Intent intent = new Intent(this,signup.class);
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
            intent.setClass(signup4.this,signup.class);
            startActivity(intent);    //startActivity觸發換頁
            finish();
        }
        return true;
    }
}
