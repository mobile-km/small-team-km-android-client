package com.teamkn.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.Logic.AccountManager;
import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.HorzScrollWithListMenu;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.database.AccountUserDBHelper;
import com.teamkn.widget.adapter.AccountListAdapter;

public class AccountManagerActivity extends TeamknBaseActivity  implements OnGestureListener  {
	
	private GestureDetector detector;
	 //menu菜单
	 MyHorizontalScrollView scrollView;
	 View base_account_manager;
	 View foot_view;  //底层  图层 隐形部分
	 ImageView iv_foot_view;
	 
	 boolean menuOut = false;
	//

    private ListView list_view;
    private AccountListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new GestureDetector(this);
        
        // <<
		LayoutInflater inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));

        scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
        foot_view = findViewById(R.id.menu);    
        RelativeLayout foot_rl_account_manage = (RelativeLayout)findViewById(R.id.foot_rl_account_manage);

        base_account_manager = inflater.inflate(R.layout.base_account_manager, null);
        
        
        iv_foot_view = (ImageView) base_account_manager.findViewById(R.id.iv_foot_view);
        iv_foot_view.setOnClickListener(new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view));
        foot_rl_account_manage.setOnClickListener(new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view));
        
        View transparent = new TextView(this);
        transparent.setBackgroundColor(android.R.color.transparent);
        final View[] children = new View[] { transparent, base_account_manager };
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new HorzScrollWithListMenu.SizeCallbackForMenu(iv_foot_view));    
       
        
        //>>

        list_view = (ListView)base_account_manager. findViewById(R.id.account_list);
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
//
//    @Override
//    // 硬返回按钮
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            on_account_manager_activity_go_back();
//            this.finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    // 软返回回调
//    @Override
//    public void on_go_back() {
//        super.on_go_back();
//        on_account_manager_activity_go_back();
//    }
//
//    private void on_account_manager_activity_go_back() {
//        // 由于可能在删除用户时，删除了当前正登录的用户，所以 is_logged_in()会返回false
//        try {
//            if (!is_logged_in()) {
//                if (AccountUserDBHelper.count() > 0) {
//                    // 如果还有用户，则选择所有用户中的第一个，切换之
//                    AccountManager.switch_account(AccountUserDBHelper.all().get(0));
//                    // open main_activity 堆栈会被TeamknBaseActivity自动清理
//                    open_activity(MainActivity.class);
//                } else {
//                    // 如果没有用户了，则关闭所有已经打开的界面，再打开登录界面
//                    restart_to_login();
//                }
//            }
//        } catch (Exception e) {
//            Log.e("AccountManagerActivity", "on_account_manager_activity_go_back", e);
//            BaseUtils.toast("账号数据加载错误");
//            restart_to_login();
//        }
//    }
//    
    /** 
     * 监听滑动 
     */
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	// // 滑动一段距离，up时触发，e1为down时的MotionEvent，e2为up时的MotionEvent  
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
        return false;  
	}
	@Override
	public void onLongPress(MotionEvent e) {	
	}
	boolean is_out = false;
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (e1.getX() - e2.getX() > 120 && is_out) {  //向左滑动 
			is_out = !is_out;
            HorzScrollWithListMenu.MyOnGestureListener.flag_show_menu_move(scrollView, foot_view);
            System.out.println(" ----- " + (e1.getX() - e2.getX()));
        }else if(e1.getX() - e2.getX() < -120  && !is_out){
            HorzScrollWithListMenu.MyOnGestureListener.flag_show_menu_move(scrollView, foot_view);
            is_out = !is_out;
        }
		return true;
	}
	@Override
	public void onShowPress(MotionEvent e) {	
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	@Override 
	public boolean onTouchEvent(MotionEvent event) { 
		return this.detector.onTouchEvent(event); 
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	   this.detector.onTouchEvent(ev);
	   return super.dispatchTouchEvent(ev);
	}
}
