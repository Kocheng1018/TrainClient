package com.example.user.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class fourbtn extends AppCompatActivity {
    private Button service,time,sos,gps,set,record;
    final static String CALL = "android.intent.action.CALL";
    String sosphone;
    String test[] = {"警察局 110","鐵路客服","緊急聯絡人電話"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourbtn2);
        service = findViewById(R.id.serverbtn);
        time = findViewById(R.id.time);
        sos = findViewById(R.id.sosbtn) ;
        gps = findViewById(R.id.gpsbtn);
        set = findViewById(R.id.setbtn);
        record = findViewById(R.id.recordbtn);
        getSosPhone();

        service.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(fourbtn.this,service.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        time.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(fourbtn.this,time.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        sos.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                new AlertDialog.Builder(fourbtn.this)
                        .setTitle("緊急聯絡人")
                        .setItems(test,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        Intent call = new Intent (CALL, Uri.parse("tel:"+ "0981693511"));
                                        startActivity(call);
                                        break;
                                    case 1:
                                        Intent call_train = new Intent (CALL, Uri.parse("tel:"+ "0981693511"));
                                        startActivity(call_train);
                                        break;
                                    case 2:
                                        Intent call_sos = new Intent (CALL, Uri.parse("tel:"+ sosphone));
                                        startActivity(call_sos);
                                        break;
                                }
                            }
                        }).show();
            }
        });
        gps.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(fourbtn.this,gps.class);
                startActivity(intent);    //startActivity觸發換頁
            }
        });
        set.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(fourbtn.this,set.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
        record.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent();   //intent實體化
                intent.setClass(fourbtn.this,record.class);
                startActivity(intent);    //startActivity觸發換頁
                finish();
            }
        });
    }
    //返回鍵跳出退出訊息
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(fourbtn.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要退出嗎?")
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    finish(); // 離開程式
                                    System.exit(0);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                }
                            }).show();
        }
        return true;
    }
    public void getSosPhone(){
        String account = getSharedPreferences("acc", MODE_PRIVATE)
                .getString("account", "");
        MainActivity.DBConnector dbConnector = new MainActivity.DBConnector();
        try{
            String datas = URLEncoder.encode("key","UTF-8")
                    + "=" + URLEncoder.encode("getsosphone","UTF-8");
            datas += "&" + URLEncoder.encode("member_no","UTF-8")
                    + "=" + URLEncoder.encode(account,"UTF-8");
            String result = dbConnector.execute("action",datas).get();
            JSONArray records = new JSONArray(result);
            JSONObject record = records.getJSONObject(0);
            sosphone = record.getString("sosphone");
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
    //防止重複按
    public abstract class OnMultiClickListener implements View.OnClickListener{
        private static final int MIN_CLICK_DELAY_TIME = 1500;
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
