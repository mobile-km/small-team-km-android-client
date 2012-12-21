package com.teamkn.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.database.AccountUserDBHelper;
import com.teamkn.widget.adapter.AccountListAdapter;

public class AccountManagerActivity extends TeamknBaseActivity{
    private ListView list_view;
    private AccountListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.base_account_manager);
        
        list_view = (ListView) findViewById(R.id.account_list);
        bind_add_account_event();
        fill_list();

    }

    // 设置 增加账号按钮事件
    private void bind_add_account_event() {
        View footer_view = getLayoutInflater().inflate(R.layout.list_account_footer, null);
        list_view.addFooterView(footer_view);

        View button = footer_view.findViewById(R.id.add_account);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                open_activity(LoginActivity.class);
            }
        });
    }

    // 填充账号列表信息，并给列表绑定点击事件
    private void fill_list() {
        try {
            adapter = new AccountListAdapter(this);
            adapter.add_items(AccountUserDBHelper.all());
            list_view.setAdapter(adapter);

            list_view.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AccountManager.switch_account(adapter.fetch_item(position));
                    startActivity(new Intent(AccountManagerActivity.this, MainActivity.class));
                    AccountManagerActivity.this.finish();
                }
            });

        } catch (Exception e) {
            Log.e("AccountManagerActivity", "fill_list", e);
            BaseUtils.toast("账号数据加载错误");
        }
    }

    // 设置 账号列表的编辑模式
    public void on_edit_account_button_click(View view) {
        Button button = (Button) view;

        if (adapter.is_edit_mode()) {
            adapter.close_edit_mode();
            button.setText(R.string.account_edit_button);
        } else {
            adapter.open_edit_mode();
            button.setText(R.string.account_edit_button_close);
        }
    }
}
