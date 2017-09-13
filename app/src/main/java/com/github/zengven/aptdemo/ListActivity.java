package com.github.zengven.aptdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.github.Adapter;
import com.github.core.base.BaseListAdapter;
import com.github.core.binder.SuperAdapter;
import com.github.zengven.aptdemo.bean.NameBean;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    @Adapter(data = "NameBean",
            viewHolderClassName = {"NameListHolder", "NameListHolder"},
            layoutIds = {R.layout.simple_list_item_1, R.layout.simple_list_item_2})
    ListView mLvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mLvName = (ListView) findViewById(R.id.lv_name);
        SuperAdapter.bind(this);
        BaseListAdapter listAdapter = (BaseListAdapter) mLvName.getAdapter();
        listAdapter.setOnViewTypeListener(new BaseListAdapter.OnViewTypeListener<NameBean>() {

            @Override
            public int onViewType(NameBean nameBean) {
                if (nameBean.name.equals("小黑") || nameBean.name.equals("小样")) {
                    return 1;
                }
                return 0;
            }
        });
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
