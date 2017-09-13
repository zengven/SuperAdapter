package com.github.zengven.aptdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.Adapter;
import com.github.core.base.BaseRecyclerAdapter;
import com.github.core.binder.SuperAdapter;
import com.github.zengven.aptdemo.bean.NameBean;

import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Adapter(data = "NameBean",
            viewHolderClassName = {"NameHolder", "NameHolder"},
            layoutIds = {R.layout.simple_list_item_1, R.layout.simple_list_item_2})
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        SuperAdapter.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<NameBean> list = new ArrayList<>();
        list.add(new NameBean("小明"));
        list.add(new NameBean("小黑"));
        list.add(new NameBean("小白"));
        list.add(new NameBean("小白"));
        list.add(new NameBean("小李"));
        list.add(new NameBean("小样"));
        list.add(new NameBean("小明"));
        list.add(new NameBean("小黑"));
        list.add(new NameBean("小白"));
        list.add(new NameBean("小王"));
        list.add(new NameBean("小李"));
        list.add(new NameBean("小样"));
        list.add(new NameBean("小明"));
        list.add(new NameBean("小黑"));
        list.add(new NameBean("小白"));
        list.add(new NameBean("小王"));
        list.add(new NameBean("小李"));
        list.add(new NameBean("小样"));
        BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) mRecyclerView.getAdapter();
        adapter.setOnViewTypeListener(new BaseRecyclerAdapter.OnViewTypeListener<NameBean>() {

            @Override
            public int onViewType(NameBean nameBean) {
                if (nameBean.name.equals("小黑") || nameBean.name.equals("小样")) {
                    return R.layout.simple_list_item_2;
                }
                return R.layout.simple_list_item_1;
            }
        });
        adapter.addAll(list);
    }
}
