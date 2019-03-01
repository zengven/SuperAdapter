package com.yexin.zengven.aptdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.yexin.annotation.Adapter;
import com.yexin.core.base.BaseListAdapter;
import com.yexin.core.binder.SuperAdapter;
import com.yexin.zengven.aptdemo.bean.NameBean;
import com.yexin.zengven.aptdemo.holder.NameHolder;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    @Adapter(dataClass = NameBean.class,
            viewHolderClass = {NameHolder.class, NameHolder.class},
            layoutIds = {R.layout.simple_list_item_1, R.layout.simple_list_item_2})
    ListView mLvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        mLvName = (ListView) findViewById(R.id.lv_name);
        SuperAdapter.bind(this);
        BaseListAdapter listAdapter = (BaseListAdapter) mLvName.getAdapter();
        if (type != 0) {
            listAdapter.setOnViewTypeListener(new BaseListAdapter.OnViewTypeListener<NameBean>() {

                @Override
                public int onViewType(NameBean nameBean) {
                    if (nameBean.name.equals("小黑") || nameBean.name.equals("小样")) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
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
        listAdapter.addAll(list);
    }
}
