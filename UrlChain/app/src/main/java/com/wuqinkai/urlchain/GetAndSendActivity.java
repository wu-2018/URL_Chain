package com.wuqinkai.urlchain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GetAndSendActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_and_send);

        //log all data in bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("hahaha", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        TextView shared_content = findViewById(R.id.shared_content);
        shared_content.setText(getData());

        ImageButton button_1 = findViewById(R.id.btn_crawler);
        ImageButton button_2 = findViewById(R.id.btn_qrcode);

        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);

    }

    public String getData() {
        String data = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (data == null) {
            data = getIntent().getDataString();
        }
        return data;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crawler:
                send2Termux(getData()+"1");
                break;
            case R.id.btn_qrcode:
                send2Termux(getData()+"2");
                break;
            default:
                break;
        }
        finish();
    }

    public void send2Termux(String s) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, s);
        shareIntent.setClassName("com.termux", "com.termux.filepicker.TermuxFileReceiverActivity");
        startActivity(shareIntent);
    }

}