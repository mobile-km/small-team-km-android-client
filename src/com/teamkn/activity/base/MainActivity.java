package com.teamkn.activity.base;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.slidingmenu.ClickListenerForScrolling;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.activity.datalist.CreateDataListActivity;
import com.teamkn.activity.datalist.SearchDataActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.ShowHelp;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataListHelper;
import com.teamkn.model.database.UserDBHelper;
import com.teamkn.widget.adapter.DataListAdapter;
import com.teamkn.widget.adapter.GroupAdapter;

public class MainActivity extends TeamknBaseActivity {
	LayoutInflater inflater;
	public static MyHorizontalScrollView scrollView;
	public static View foot_view;  //底层  图层 隐形部分
    View show_view;  //显示的View
    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;
	
	static boolean first_create = true;
//	View view_show;
//	LinearLayout layout;

	public static class RequestCode {
		public  static boolean  IS_ON_PAUSE= false;
		
		//显示帮助 顺序
		
		public static char SHOW_HELP = RequestCode.SHOW_STEP_HELP;
		public final static char SHOW_NOT_HELP = 'a';
		public final static char SHOW_STEP_HELP = 'b';
		public final static char SHOW_COLLECTION_HELP = 'c';
		public final static char SHOW_CREATE_HELP = 'd';
		public final static char SHOW_PUBLIC_HELP = 'e';
		
		public static int SHOW_NEXT = 0;
		public final static int SHOW_NOT_HELP_CASE = 0;
		public final static int SHOW_STEP_HELP_CASE = 1;
		public final static int SHOW_COLLECTION_HELP_CASE = 2;
		public final static int SHOW_CREATE_HELP_CASE = 3;
		public final static int SHOW_CREATE_NEXT_HELP_CASE = 4;
		public final static int SHOW_PUBLIC_HELP_CASE = 5;
		
		public final static int CREATE_DATA_LIST = 0;
		public final static int SHOW_BACK = 9;

		public final static String COLLECTION = "COLLECTION";
		public final static String STEP = "STEP";
		public final static String ALL = "ALL";

		public static String data_list_type = ALL;

		public static String data_list_public = RequestCode.我的首页;
		public static final String 我的列表 = "false";
		public static final String 公开的列表 = "true";
		public static final String 我的首页 = "follow";
		public static final String 协作列表 = "fork";
		public static final String 被协作列表 = "forked";
		public static final String 我的书签 = "watch";
		
		
		public static final String 个人的公开的列表 = "user_public_data_list";  
		//RequestCode.data_list_public = "fork"; watch  true  false	 follow	

		static int account_page = 20;
		static int now_page = 1;
	}
	LinearLayout user_name_rl ;
	ImageView main_user_name_iv;
	// node_listView_show 数据
	ListView data_list;
	public static DataListAdapter dataListAdapter;
	public static List<DataList> datalists;
	public static List<DataList> record_datalists;
	/*
	 * 收集，步骤，所有
	 */
//	Button click_collection_button, click_step_button, click_all_button;
    /*
     * cursor imageview 页卡头标
     * */
	private static ImageView cursor;// 动画图片
	private static int offset = 0;// 动画图片偏移量
	private static int currIndex = 0;// 当前页卡编号
	private static int bmpW;// 动画图片宽度
	/*
     * popupwindow title public
     * */
	PopupWindow popupWindow; 
	private ListView lv_group;   
    private View view; 
    private List<String> groups; 
    TextView user_name_tv;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String data_list_public = intent.getStringExtra("data_list_public");
//		String data_list_type = intent.getStringExtra("data_list_type");
		if (data_list_public != null) {
			RequestCode.data_list_public = data_list_public;
		}
		
		inflater= LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));

        scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
        foot_view = findViewById(R.id.menu);
        
        setView(); 	
		
	}
	private  void setView(){
    	show_view = inflater.inflate(R.layout.base_main, null);
    	ViewGroup head_view = (ViewGroup) show_view.findViewById(R.id.head);
        Button btnSlide = (Button) head_view.findViewById(R.id.iv_foot_view);
        
        btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, foot_view));
     
        View transparent = new TextView(MainActivity.this);
        final View[] children = new View[] { transparent, show_view };
        int scrollToViewIdx = 1;

        scrollView.initViews(children, scrollToViewIdx, btnSlide);
        
        // 加载node_listview
     	InitImageView(); //初始化 cursor中的收集，步骤，所有 滑动标
     	load_data_list_or_watch(RequestCode.data_list_public);
    }
	
	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) show_view.findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.line)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}


	private void judge(){
		System.out.println("current_user().is_show_tip " + current_user().is_show_tip);
		if(current_user().is_show_tip && RequestCode.data_list_public.equals(RequestCode.我的列表)){
			switch (RequestCode.SHOW_HELP) {
			case RequestCode.SHOW_STEP_HELP:
				ShowHelp.showHelp(ShowHelp.Params.SHOW_STEP_HELP,this, data_list, getResources().getString(R.string.show_step_help));
//				RequestCode.SHOW_HELP = RequestCode.SHOW_COLLECTION_HELP;
				break;
			case RequestCode.SHOW_COLLECTION_HELP:
//				RequestCode.SHOW_HELP = RequestCode.SHOW_CREATE_HELP;
				ShowHelp.showHelp(ShowHelp.Params.SHOW_COLLECTION_HELP,this, data_list, getResources().getString(R.string.show_collection_help));
				break;
			case RequestCode.SHOW_CREATE_HELP:
				ImageButton mi_data_list_add = (ImageButton)findViewById(R.id.mi_data_list_add);
				ShowHelp.showHelp(ShowHelp.Params.SHOW_CREATE_HELP,this, mi_data_list_add, getResources().getString(R.string.show_create_helop_msg));
				
				break;
			case RequestCode.SHOW_PUBLIC_HELP:
				showWindow(user_name_rl);
				ShowHelp.showHelp(ShowHelp.Params.SHOW_PUBLIC_HELP,MainActivity.this, user_name_rl, getResources().getString(R.string.sea_onther_data_list));
				RequestCode.SHOW_NEXT = RequestCode.SHOW_NOT_HELP_CASE;
				RequestCode.SHOW_HELP = RequestCode.SHOW_NOT_HELP;
				break;
			case RequestCode.SHOW_NOT_HELP:
				break;
			default:
				break;
			}
		}
		
	}
	@Override
	protected void onResume() {

		// 设置用户头像和名字
		AccountUser user = current_user();
		byte[] avatar = user.avatar;
		String name = current_user().name;
		RelativeLayout rl = (RelativeLayout) show_view.
				findViewById(R.id.main_user_avatar);
		if (avatar != null) {
			Bitmap bitmap = BitmapFactory
					.decodeStream(new ByteArrayInputStream(avatar));
			Drawable drawable = new BitmapDrawable(bitmap);
			rl.setBackgroundDrawable(drawable);
		} else {
			rl.setBackgroundResource(R.drawable.user_default_avatar_normal);
		}
		user_name_tv= (TextView) show_view.findViewById(R.id.main_user_name);
		user_name_tv.setText(name+"的列表");
		main_user_name_iv = (ImageView)findViewById(R.id.main_user_name_iv);
		if(RequestCode.data_list_public .equals(RequestCode.我的列表) 
				|| RequestCode.data_list_public .equals(RequestCode.公开的列表)
				|| RequestCode.data_list_public .equals(RequestCode.被协作列表)
				|| RequestCode.data_list_public .equals(RequestCode.协作列表)
				|| RequestCode.data_list_public .equals(RequestCode.我的书签)
				){
			user_name_rl= (LinearLayout)findViewById(R.id.main_user_name_rl);
			user_name_rl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					showWindow(v);
				}
			}); 
        }else{
        	main_user_name_iv.setVisibility(View.GONE);
        }
		
		set_title();
		
		super.onResume();
	}
    private  void set_title(){
    	if(RequestCode.data_list_public.equals(RequestCode.我的首页)){
    		user_name_tv.setText("我的首页");
    	}else if(RequestCode.data_list_public.equals(RequestCode.公开的列表)){
    		user_name_tv.setText("列表广场");
    	}else if(RequestCode.data_list_public.equals(RequestCode.我的列表)){
    		String user_name_sub = current_user().name;
    		if(user_name_sub.length()>14){
    			user_name_sub = user_name_sub.substring(0, 14) + "..";
    		}
    		user_name_tv.setText("原创"+user_name_sub + "的列表");
    	}else if(RequestCode.data_list_public.equals(RequestCode.协作列表)){
    		user_name_tv.setText("我Fork的列表");
    	}else if(RequestCode.data_list_public.equals(RequestCode.我的书签)){
    		user_name_tv.setText("我的书签");
    	}else if(RequestCode.data_list_public.equals(RequestCode.被协作列表)){
    		user_name_tv.setText("被Fork的列表");
    	}
    }
    private void load_data_list_or_watch(String watch_or_public){
    	if (BaseUtils.is_wifi_active(MainActivity.this)) {
	    	new TeamknAsyncTask<Void, Void, List<DataList>>(MainActivity.this,"内容加载中") {
				@Override
				public List<DataList> do_in_background(Void... params)
						throws Exception {
						if(RequestCode.data_list_public.equals(RequestCode.我的首页)){
							record_datalists = HttpApi.DataList.follows_list(RequestCode.now_page, 100);
						}else if(RequestCode.data_list_public.equals(RequestCode.公开的列表)){
							record_datalists = HttpApi.DataList.public_timeline(RequestCode.now_page, 100);	
						}else if(RequestCode.data_list_public.equals(RequestCode.我的列表)){
							record_datalists = HttpApi.DataList.pull(RequestCode.data_list_type,RequestCode.now_page, 100);
						}else if(RequestCode.data_list_public.equals(RequestCode.我的书签)){
							record_datalists = HttpApi.WatchList.watch_public_timeline(RequestCode.now_page, 100);
						}else if(RequestCode.data_list_public.equals(RequestCode.协作列表)){
							record_datalists = HttpApi.DataList.forked_list(RequestCode.now_page, 100);
						}else if(RequestCode.data_list_public.equals(RequestCode.被协作列表)){
							record_datalists = HttpApi.DataList.be_forked_list(RequestCode.now_page, 100);
				    	}
					return null;
				}
				@Override
				public void on_success(List<DataList> datalists) {
					load_list();
					judge();
				}
			}.execute();
    	}else{
			BaseUtils.toast("无法连接到网络，请检查网络配置");
		}
    }
	// 加载node_listview
	private void load_list() {
		
		datalists = DataListHelper.by_type(record_datalists, RequestCode.data_list_type);
		
		request_pageselected();
		data_list = (ListView) show_view.findViewById(R.id.data_list);	
		dataListAdapter = new DataListAdapter(MainActivity.this);
		dataListAdapter.add_items(datalists);
		data_list.setAdapter(dataListAdapter);
		dataListAdapter.notifyDataSetChanged();
		//注册上下文菜单
	    registerForContextMenu(data_list);
	    
		data_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				TextView info_tv = (TextView) list_item.findViewById(R.id.info_tv);
				DataList item = (DataList) info_tv.getTag(R.id.tag_note_uuid);
				Intent intent = new Intent(MainActivity.this,DataItemListActivity.class);
				intent.putExtra("data_list",item);
				intent.putExtra("data_list_public", RequestCode.data_list_public);
				System.out.println(RequestCode.data_list_public + " mainactivity setonclick  = " +item.toString());
				
				if(item.is_removed.equals("true")){
					showDialog(item,item_id);
				}else{
					startActivityForResult(intent, RequestCode.SHOW_BACK);
				}
			}
		});
	}
	private void showDialog(final DataList dataList,final int id ){
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setTitle("注意");
		builder.setMessage("此列表已经被原作者删除，是否删除该记录？");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				DataListDBHelper.remove_by_server_id(dataList);
//				dataListAdapter.remove_item(dataList);
//				dataListAdapter.notifyDataSetChanged();
				remove_data_list(dataList,id,true);
			}
		});
		builder.show();
	}
	// 处理其他activity界面的回调 有要改进的地方 如 记忆从别的地方回来，还要回到上次加载的地方
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println(requestCode+ " : " + resultCode );
		if (resultCode == Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case RequestCode.CREATE_DATA_LIST:
			load_list();
			break;
		case RequestCode.SHOW_BACK:
				if(current_user().is_show_tip  && RequestCode.data_list_public.equals(RequestCode.我的列表)){
					switch (RequestCode.SHOW_NEXT) {
					case RequestCode.SHOW_COLLECTION_HELP_CASE:
						RequestCode.SHOW_HELP = RequestCode.SHOW_COLLECTION_HELP;
						break;
					case RequestCode.SHOW_CREATE_HELP_CASE:
						RequestCode.SHOW_HELP = RequestCode.SHOW_CREATE_HELP;
						break;
					case RequestCode.SHOW_CREATE_NEXT_HELP_CASE:
						
						RequestCode.SHOW_NEXT = RequestCode.SHOW_PUBLIC_HELP_CASE;
						RequestCode.SHOW_HELP = RequestCode.SHOW_PUBLIC_HELP;
						break;
						
					case RequestCode.SHOW_PUBLIC_HELP_CASE:
						RequestCode.SHOW_NEXT = RequestCode.SHOW_NOT_HELP_CASE;
						RequestCode.SHOW_HELP = RequestCode.SHOW_NOT_HELP;
						break;
						
					case RequestCode.SHOW_NOT_HELP_CASE:
						RequestCode.SHOW_HELP = RequestCode.SHOW_NOT_HELP;
						break;
					
					default:
						break;
					}
				}
				
//				if(data.getStringExtra("data_list_public")!=null){
					RequestCode.data_list_public = data.getStringExtra("data_list_public");
//				}
				set_title();
				load_data_list_or_watch(RequestCode.data_list_public);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void click_search_ib(View view) {
		EditText search_box = (EditText)findViewById(R.id.search_box);
		String search_str = search_box.getText().toString();
		if(!BaseUtils.is_str_blank(search_str)){
			Intent intent = new Intent(MainActivity.this,SearchDataActivity.class);
			intent.putExtra("search_str", search_str);
			intent.putExtra("data_list_type", RequestCode.data_list_type);
			intent.putExtra("data_list_public", RequestCode.data_list_public);
			startActivity(intent);
		}
	}

	public void click_add_data_list_iv(View view) {
			Intent intent = new Intent(MainActivity.this,CreateDataListActivity.class);
			startActivityForResult(intent, RequestCode.CREATE_DATA_LIST);
	}

	public void click_collection_button(View view) {
		RequestCode.data_list_type = RequestCode.COLLECTION;
		load_list();
	}

	public void click_step_button(View view) {
		RequestCode.data_list_type = RequestCode.STEP;
		load_list();
	}

	public void click_all_button(View view) {
		RequestCode.data_list_type = RequestCode.ALL;
		load_list();
	}
	
	
	//页卡 逻辑
	private void request_pageselected(){
		int index = 0;
		if(RequestCode.data_list_type.equals(RequestCode.COLLECTION)){
			index = 1 ;
		}else if(RequestCode.data_list_type.equals(RequestCode.STEP)){
			index = 2 ;
		}else if(RequestCode.data_list_type.equals(RequestCode.ALL)) {
			index = 0 ;
		}
		MyOnPageChangeListener.onPageSelected(index);
	}
	private void showWindow(View parent) { 
		main_user_name_iv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
		LayoutInflater layoutInflater = null;
		if (popupWindow == null) {  
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
  
            view = layoutInflater.inflate(R.layout.group_list, null);  
  
            lv_group = (ListView) view.findViewById(R.id.lvGroup);  
            // 加载数据  
            if(RequestCode.data_list_public .equals(RequestCode.我的列表)
            		|| RequestCode.data_list_public .equals(RequestCode.被协作列表)){
            	groups = new ArrayList<String>();  
            	groups.add("原创列表");  
                groups.add("被协作列表");  
            }else if(RequestCode.data_list_public .equals(RequestCode.公开的列表)
            		|| RequestCode.data_list_public .equals(RequestCode.协作列表)
            		|| RequestCode.data_list_public .equals(RequestCode.我的书签)){
            	groups = new ArrayList<String>();  
            	groups.add("列表广场");  
                groups.add("我Fork的协作");  
                groups.add("我的书签");
            }
            
            GroupAdapter groupAdapter = new GroupAdapter(this); 
            groupAdapter.add_items(groups);
            lv_group.setAdapter(groupAdapter);  
            // 创建一个PopuWidow对象  
            popupWindow = new PopupWindow(view, 200, 300);  
        }    
        // 使其聚集  
        popupWindow.setFocusable(true);  
        // 设置允许在外点击消失  
        popupWindow.setOutsideTouchable(true);  
//        popupWindow.set
        popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
//				System.out.println(popupWindow.);
				if(popupWindow == null){
					System.out.println("popupWindow  null");
				}else{
					System.out.println("popupWindow  yes  " + popupWindow );
				}
			}
		});
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);  
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半  
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2  
                - popupWindow.getWidth() / 2;  
        Log.i("coder", "xPos:" + xPos);  
  
        popupWindow.showAsDropDown(parent, xPos-60, 0);  
        
        if(RequestCode.data_list_public .equals(RequestCode.我的列表)
        		|| RequestCode.data_list_public .equals(RequestCode.被协作列表)){
        	lv_group.setOnItemClickListener(new OnItemClickListener() {  
        		  
                @Override  
                public void onItemClick(AdapterView<?> adapterView, View view,  
                        int position, long id) {  
                	System.out.println("我的列表 " + RequestCode.data_list_public + " : " + position);
                	switch (position) {
        			case 0:
        				RequestCode.data_list_public = RequestCode.我的列表;
        				set_title();
        				load_data_list_or_watch(RequestCode.data_list_public);
        				break;
        			case 1:
        				RequestCode.data_list_public = RequestCode.被协作列表;
        				set_title();
        				load_data_list_or_watch(RequestCode.data_list_public);
        				break;
        			default:
        				break;
        			}
                	if (popupWindow != null) {  
                        popupWindow.dismiss();  
                        popupWindow = null;
                        main_user_name_iv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    }
                	//RequestCode.data_list_public = "fork"; watch  true  false  
                }  
            });
        }else if(RequestCode.data_list_public .equals(RequestCode.公开的列表)
        		|| RequestCode.data_list_public .equals(RequestCode.协作列表)
        		|| RequestCode.data_list_public .equals(RequestCode.我的书签)){
        	lv_group.setOnItemClickListener(new OnItemClickListener() {  
      		  
                @Override  
                public void onItemClick(AdapterView<?> adapterView, View view,  
                        int position, long id) {
                	switch (position) {
        			case 0:
        				RequestCode.data_list_public = RequestCode.公开的列表;
        				set_title();
        				load_data_list_or_watch(RequestCode.data_list_public);
        				break;
        			case 1:
        				RequestCode.data_list_public = RequestCode.协作列表;
        				set_title();
        				load_data_list_or_watch(RequestCode.data_list_public);
        				break;
        			case 2:
        				RequestCode.data_list_public = RequestCode.我的书签;
        				set_title();
        				load_data_list_or_watch(RequestCode.data_list_public);
        				break;
        			default:
        				break;
        			}
                   	if (popupWindow != null) {  
                        popupWindow.dismiss();  
                        popupWindow = null;
                        main_user_name_iv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    }
                }
        	});
        }
    }  
	/**
	 * 页卡切换监听
	 */
	static class MyOnPageChangeListener{
		static int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		static int two = one * 2;// 页卡1 -> 页卡3 偏移量
		public static void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			try {
				animation.setFillAfter(true);// True:图片停在动画结束位置
				animation.setDuration(300);
				cursor.startAnimation(animation);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		if(info.id!=-1){
			menu.setHeaderTitle("弹出菜单");
			menu.add(1,1,1,"查看");
			final Integer id = (int) info.id;
			if(datalists.get(id).user_id == UserDBHelper.find_by_server_user_id(current_user().user_id).id){
				menu.add(1,2,2,"删除");
			}
		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case 1:
			break;
		case 2:
			final Integer id = (int) info.id;
			System.out.println("onContextItemSelected = "+datalists.get(id).toString());
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("确定要删除吗？");
			builder.setPositiveButton(getResources().getString(R.string.dialog_ok), new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					remove_data_list(datalists.get(id),id,false);
				}
			});
			builder.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
			builder.show();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
    private void remove_data_list(final DataList dataList,final int data_list_id,final boolean is_delete_watch){
    	if(BaseUtils.is_wifi_active(this)){
    		new TeamknAsyncTask<Void, Void, Boolean>(MainActivity.this,getResources().getString(R.string.now_deleting)) {
				@Override
				public Boolean do_in_background(Void... params)
						throws Exception {
					if(is_delete_watch){
						HttpApi.WatchList.watch(dataList, false);
					}else{
						HttpApi.DataList.remove(dataList);
					}
					return true;
				}
				@Override
				public void on_success(Boolean result) {
					datalists.remove(data_list_id);
					dataListAdapter.remove_item(dataList);
					dataListAdapter.notifyDataSetChanged();
					BaseUtils.toast("删除成功");
				}
			}.execute();
    	}else{
    		BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
    	}
    }
	@Override
	protected void onPause() {
		RequestCode.IS_ON_PAUSE = true;
		super.onPause();
	}	
}
