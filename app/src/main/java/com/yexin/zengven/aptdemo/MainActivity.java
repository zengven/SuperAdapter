package com.yexin.zengven.aptdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnRecyclerSingle;
    private Button mBtnRecyclerMulti;
    private Button mBtnListSingle;
    private Button mBtnListMulti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnRecyclerSingle = (Button) findViewById(R.id.btn_recycler_single);
        mBtnRecyclerMulti = (Button) findViewById(R.id.btn_recycler_multi);
        mBtnListSingle = (Button) findViewById(R.id.btn_list_single);
        mBtnListMulti = (Button) findViewById(R.id.btn_list_multi);

        mBtnRecyclerSingle.setOnClickListener(this);
        mBtnRecyclerMulti.setOnClickListener(this);
        mBtnListSingle.setOnClickListener(this);
        mBtnListMulti.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.btn_recycler_single:
                startActivity(new Intent(getApplicationContext(), RecyclerActivity.class));
                break;

            case R.id.btn_recycler_multi:
                Intent intent = new Intent(getApplicationContext(), RecyclerActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;

            case R.id.btn_list_single:
                startActivity(new Intent(getApplicationContext(), ListActivity.class));
                break;

            case R.id.btn_list_multi:
                Intent intent1 = new Intent(getApplicationContext(), ListActivity.class);
                intent1.putExtra("type", 1);
                startActivity(intent1);
                break;
        }
    }
}
