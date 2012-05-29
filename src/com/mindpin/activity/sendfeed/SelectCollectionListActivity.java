package com.mindpin.activity.sendfeed;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.model.Collection;
import com.mindpin.model.cache.CollectionsCache;
import com.mindpin.widget.adapter.SelectCollectionListAdapter;

public class SelectCollectionListActivity extends MindpinBaseActivity {
	public static final String EXTRA_NAME_KIND = "kind";
	public static final String EXTRA_VALUE_SELECT_FOR_SEND = "select_for_send";
	public static final String EXTRA_VALUE_SELECT_FOR_RESULT = "select_for_result";
	public static final String EXTRA_NAME_SELECT_COLLECTION_IDS = "select_collection_ids";
	public static final String EXTRA_NAME_SEND_TSINA = "send_tsina";
	
	private ListView collection_list_lv;
	private ArrayList<Integer> select_collection_ids;
	private SelectCollectionListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_collection_list);
		
		set_tsina_checkbox();
		set_select_collections_checkbox();
		
		build_collection_list();
		bind_new_collection_event();
	}
	
	private void set_tsina_checkbox() {
		CheckBox send_tsina_cb = (CheckBox)findViewById(R.id.send_tsina_cb);
		boolean send_tsina = getIntent().getBooleanExtra(EXTRA_NAME_SEND_TSINA,false);
		send_tsina_cb.setChecked(send_tsina);		
	}

	private void set_select_collections_checkbox() {
		select_collection_ids = new ArrayList<Integer>();
		String kind = getIntent().getStringExtra(EXTRA_NAME_KIND);
		if(kind.equals(EXTRA_VALUE_SELECT_FOR_RESULT)){
			init_button_event_for_result();
		}else if(kind.equals(EXTRA_VALUE_SELECT_FOR_SEND)){
			init_button_event_for_send();
		}
	}

	private void build_collection_list() {
		collection_list_lv = (ListView) findViewById(R.id.select_collection_list);
		
		List<Collection> collections = CollectionsCache.get_current_user_collection_list();
		adapter = new SelectCollectionListAdapter(collections, select_collection_ids);
		collection_list_lv.setAdapter(adapter);
		
		collection_list_lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View current_item, int position,
					long arg3) {
				adapter.select_item(current_item,position);
			}
		});
	}
	
	private void bind_new_collection_event() {
		Button new_collection_bn = (Button) findViewById(R.id.new_collection_bn);
		new_collection_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				show_new_collection_dialog();
			}
		});
	}

	private void show_new_collection_dialog() {
		LayoutInflater factory = LayoutInflater
				.from(SelectCollectionListActivity.this);
		final View view = factory.inflate(R.layout.new_collection_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("新建收集册");
		builder.setView(view);
		builder.setPositiveButton("创建", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText ctet = (EditText) view
						.findViewById(R.id.collection_title_et);
				String title = ctet.getText().toString();
				if (title == null || "".equals(title)) {
					BaseUtils.toast("请输入标题");
					return;
				}
				create_collection(title);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}
	
	private void init_button_event_for_send() {
		Button cancel_bn = (Button)findViewById(R.id.cancel_bn);
		cancel_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(Activity.RESULT_CANCELED,intent);
				finish();
			}
		});
		
		final CheckBox send_tsina_cb = (CheckBox)findViewById(R.id.send_tsina_cb);
		Button send_bn = (Button) findViewById(R.id.send_bn);
		send_bn.setVisibility(View.VISIBLE);
		send_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(select_collection_ids!=null && select_collection_ids.size()!=0){
					Intent intent = new Intent();
					intent.putIntegerArrayListExtra(EXTRA_NAME_SELECT_COLLECTION_IDS, select_collection_ids);
					if(send_tsina_cb.isChecked()){
						intent.putExtra(EXTRA_NAME_SEND_TSINA,
								true);
					}else{
						intent.putExtra(EXTRA_NAME_SEND_TSINA,
								false);
					}
					setResult(Activity.RESULT_OK,intent);
					finish();
				}else{
					BaseUtils.toast("至少选择一个收集册");
				}
			}
		});
	}

	private void init_button_event_for_result() {
		ArrayList<Integer> ids = getIntent().getIntegerArrayListExtra(EXTRA_NAME_SELECT_COLLECTION_IDS);
		if(ids !=null){
			select_collection_ids = ids;
		}
		
		final CheckBox send_tsina_cb = (CheckBox)findViewById(R.id.send_tsina_cb);
		Button submit_bn = (Button) findViewById(R.id.submit_bn);
		submit_bn.setVisibility(View.VISIBLE);
		submit_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putIntegerArrayListExtra(EXTRA_NAME_SELECT_COLLECTION_IDS,
						select_collection_ids);
				if(send_tsina_cb.isChecked()){
					intent.putExtra(EXTRA_NAME_SEND_TSINA,
							true);
				}else{
					intent.putExtra(EXTRA_NAME_SEND_TSINA,
							false);
				}
				setResult(Activity.RESULT_OK,intent);
				finish();
			}
		});
		
		Button cancel_bn = (Button)findViewById(R.id.cancel_bn);
		cancel_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(Activity.RESULT_CANCELED,intent);
				finish();
			}
		});
	}
	
	private void create_collection(String title){
		new MindpinAsyncTask<String, Void, Boolean>(this,R.string.now_creating) {
			@Override
			public Boolean do_in_background(String... params)
					throws Exception {
				String title1 = params[0];
				return HttpApi.create_collection(title1);
			}

			@Override
			public void on_success(Boolean result) {
				if(result){
					List<Collection> collections = CollectionsCache.get_current_user_collection_list();
					Collection collection = collections
							.get(collections.size() - 1);
					adapter.add_item(collection);
					collection_list_lv
							.setSelection(collection_list_lv.getCount() - 1);
					BaseUtils.toast("创建成功");
				}else{
					BaseUtils.toast("创建失败");
				}
			}
		}.execute(title);
	}
	
}
