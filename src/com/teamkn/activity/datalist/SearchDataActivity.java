package com.teamkn.activity.datalist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.widget.adapter.DataListAdapter;
import com.teamkn.widget.adapter.GroupAdapter;

public class SearchDataActivity extends TeamknBaseActivity{
	public static class RequestCode {
		public final static String COLLECTION = "COLLECTION";
		public final static String STEP = "STEP";
		public final static String ALL = "ALL";

		public static String data_list_type = ALL;

		public static String data_list_public = "false";
	}
	/*
     * cursor imageview 页卡头标
     * */
	private static ImageView cursor;// 动画图片
	private static int offset = 0;// 动画图片偏移量
	private static int currIndex = 0;// 当前页卡编号
	private static int bmpW;// 动画图片宽度
	/*
	 * 
	 * */
	PopupWindow popupWindow; 
	private ListView lv_group;   
    private View view; 
    private List<String> groups; 
    TextView user_name_tv;
	
    EditText search_box;
	ListView search_result_list;
	LinearLayout list_no_data_show;
	DataListAdapter dataListAdapter ;	
	List<DataList> search_datalists;
	List<DataList> datalists;
	String search_box_str;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_data);
		// 加载node_listview
		InitImageView();
		
		search_result_list = (ListView)findViewById(R.id.search_result_list);
		list_no_data_show = (LinearLayout)findViewById(R.id.list_no_data_show);
		search_box  = (EditText) findViewById(R.id.search_box);
        ImageButton   search_submit = (ImageButton)findViewById(R.id.search_submit);
        search_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				search_box_str = search_box.getText().toString();
				search_list(search_box_str);
			}
		});   
        
        Intent intent = getIntent();
		String search_str = intent.getStringExtra("search_str");
		String search_type = intent.getStringExtra("data_list_type");
		String search_public = intent.getStringExtra("data_list_public");
		
		if(!BaseUtils.is_str_blank(search_type) 
				&& !BaseUtils.is_str_blank(search_public)){
			RequestCode.data_list_public = search_public;
			RequestCode.data_list_type = search_type;
			System.out.println(RequestCode.data_list_public+  "  :  " + RequestCode.data_list_type);
		}
		if(!BaseUtils.is_str_blank(search_str)){
			search_box_str = search_str;
			search_list(search_str);
		}
	}
	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}
	@Override
	protected void onResume() {
		// 设置用户头像和名字
		user_name_tv= (TextView) findViewById(R.id.main_user_name);
		set_title();
		user_name_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				showWindow(v);
			}
		});
		super.onResume();
	}
	private void search_list(final String search_str){
		System.out.println("search_str  " + search_str + " : " + RequestCode.data_list_public + " : " + RequestCode.data_list_type);
			new TeamknAsyncTask<Void, Void, List<DataList>>(SearchDataActivity.this,"正在搜索") {
				@Override
				public List<DataList> do_in_background(Void... params) throws Exception {
					search_datalists = new ArrayList<DataList>();
					if(!BaseUtils.is_str_blank(search_str)){
//						search_box.setText(search_str);
						if (BaseUtils.is_wifi_active(SearchDataActivity.this)) {
							try {
								if(RequestCode.data_list_public.equals("true")){
									search_datalists = HttpApi.DataList.search_public_timeline(search_str);
								}else if(RequestCode.data_list_public.equals("false")){
									search_datalists = HttpApi.DataList.search_mine(search_str);
								}else if(RequestCode.data_list_public.equals("watch")){
									search_datalists = HttpApi.DataList.search_mine_watch(search_str);
								}
							}catch (Exception e){
								e.printStackTrace();
							}
					    }else{
							BaseUtils.toast("无法连接到网络，请检查网络配置");
						}
					}
					return datalists;
				}
				@Override
				public void on_success(List<DataList> datalists) {
					load_list();
				}
			}.execute();	
	}
	public List<DataList> screen_data_list(List<DataList> search_datalists,String data_list_type){
		List<DataList> screen_datalist = new ArrayList<DataList>() ;
		for(DataList dataList : search_datalists){
			if(data_list_type.equals(RequestCode.ALL)){
				screen_datalist.add(dataList);
			}else if(data_list_type.equals(dataList.kind)){
				screen_datalist.add(dataList);
			}
		}
		return screen_datalist;
	}
	public void load_list(){
		datalists = screen_data_list(search_datalists,RequestCode.data_list_type) ;
		request_pageselected();
		dataListAdapter = new DataListAdapter(SearchDataActivity.this);
		dataListAdapter.add_items(datalists);
		search_result_list.setAdapter(dataListAdapter);
		dataListAdapter.notifyDataSetChanged();
		if(datalists.size()==0){
			list_no_data_show.setVisibility(View.VISIBLE);
		}else{
			list_no_data_show.setVisibility(View.GONE);
		}
		search_result_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				System.out.println(item_id + " : " + position);
				TextView info_tv = (TextView) list_item
						.findViewById(R.id.note_info_tv);
				final DataList item = (DataList) info_tv
						.getTag(R.id.tag_note_uuid);
				Intent intent = new Intent(SearchDataActivity.this,DataItemListActivity.class);
				intent.putExtra("data_list_id",item.id);
				startActivity(intent);
			}
	  });
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
	private void showWindow(View parent) {  
        if (popupWindow == null) {  
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
  
            view = layoutInflater.inflate(R.layout.group_list, null);  
  
            lv_group = (ListView) view.findViewById(R.id.lvGroup);  
            // 加载数据  
            groups = new ArrayList<String>();  
            groups.add("全部");  
            groups.add("我的列表");  
            groups.add("公共列表");
            groups.add("我的书签");
  
            GroupAdapter groupAdapter = new GroupAdapter(this); 
            groupAdapter.add_items(groups);
            lv_group.setAdapter(groupAdapter);  
            // 创建一个PopuWidow对象  
            popupWindow = new PopupWindow(view, 180, 218);  
        }  
  
        // 使其聚集  
        popupWindow.setFocusable(true);  
        // 设置允许在外点击消失  
        popupWindow.setOutsideTouchable(true);  
  
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);  
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半  
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2  
                - popupWindow.getWidth() / 2;  
        Log.i("coder", "xPos:" + xPos);  
  
        popupWindow.showAsDropDown(parent, xPos-50, 0);  
  
        lv_group.setOnItemClickListener(new OnItemClickListener() {  
            @Override  
            public void onItemClick(AdapterView<?> adapterView, View view,  
                    int position, long id) {   
                switch (position) {
				case 0:
					break;
				case 1:
					RequestCode.data_list_public = "false";
					set_title();
					search_list(search_box_str);
					break;
				case 2:
					RequestCode.data_list_public = "true";
					set_title();
					search_list(search_box_str);
					break;
				case 3:
					RequestCode.data_list_public = "watch";
					set_title();
					search_list(search_box_str);
				default:
					break;
				}
                if (popupWindow != null) {  
                    popupWindow.dismiss();  
                }  
            }  
        });  
    }  
	private void set_title(){
    	if(RequestCode.data_list_public.equals("true")){
    		user_name_tv.setText("公共列表的搜索结果");
    	}else if(RequestCode.data_list_public.equals("false")){
    		user_name_tv.setText(current_user().name + "的列表搜索结果");
    	}else if(RequestCode.data_list_public.equals("watch")){
    		user_name_tv.setText("书签列表搜索结果");
    	}
    }
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
}
