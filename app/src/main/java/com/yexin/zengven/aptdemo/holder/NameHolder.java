package com.yexin.zengven.aptdemo.holder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yexin.core.base.BaseViewHolder;
import com.yexin.zengven.aptdemo.R;
import com.yexin.zengven.aptdemo.bean.NameBean;

/**
 * author: zengven
 * date: 2017/8/22 16:18
 * desc: TODO
 */

public class NameHolder extends BaseViewHolder<NameBean> {

    public NameHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(final NameBean bean) {
        TextView textView = getView(R.id.text1);
        textView.setText(bean.name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "戳你--" + bean.name, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
