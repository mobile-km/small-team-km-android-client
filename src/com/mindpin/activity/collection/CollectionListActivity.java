package com.mindpin.activity.collection;

import java.util.List;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.activity.feed.FeedListActivity;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.model.Collection;
import com.mindpin.model.cache.CollectionsCache;
import com.mindpin.widget.adapter.CollectionListAdapter;

public class CollectionListActivity extends MindpinBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_list);

		build_new_collection_logic();

		build_collection_list();
	}

	private void build_new_collection_logic() {
		Button new_collection = (Button) findViewById(R.id.new_collection);
		new_collection.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				show_new_collection_dialog();
			}
		});
	}

	private void show_new_collection_dialog() {
		LayoutInflater factory = LayoutInflater
				.from(CollectionListActivity.this);
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
					Toast.makeText(getApplicationContext(), "请输入标题",
							Toast.LENGTH_SHORT).show();
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

	private void build_collection_list() {
		final List<Collection> collections = CollectionsCache.get_current_user_collection_list();
		ListView collection_list = (ListView) findViewById(R.id.collection_list);

		final CollectionListAdapter sa = new CollectionListAdapter(this);
		sa.add_items(collections);
		collection_list.setAdapter(sa);
		final Button toggle_list_mode_bn = (Button) findViewById(R.id.toggle_list_mode);
		toggle_list_mode_bn.setText("编辑");

		toggle_list_mode_bn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!sa.is_edit_mode()) {
					toggle_list_mode_bn.setText("完成");
					sa.start_edit_mode();
				} else {
					toggle_list_mode_bn.setText("编辑");
					sa.end_edit_mode();
				}
			}
		});

		collection_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Collection collection = collections.get(arg2);

				Intent intent = new Intent(CollectionListActivity.this, FeedListActivity.class);
				intent.putExtra(FeedListActivity.EXTRA_COLLECTION_ID, collection.collection_id);
				intent.putExtra(FeedListActivity.EXTRA_COLLECTION_TITLE, collection.title);
				startActivity(intent);
			}
		});
	}

	private void create_collection(String title) {
		new MindpinAsyncTask<String, Void, Boolean>(this, R.string.now_creating) {
			@Override
			public Boolean do_in_background(String... params) throws Exception {
				String title1 = params[0];
				return HttpApi.create_collection(title1);
			}

			@Override
			public void on_success(Boolean result) {
				if (result) {
					build_collection_list();
					BaseUtils.toast("操作成功");
				} else {
					BaseUtils.toast("创建失败");
				}
			}
		}.execute(title);
	}

}
