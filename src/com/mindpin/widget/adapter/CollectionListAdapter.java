package com.mindpin.widget.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.adapter.MindpinBaseAdapter;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.model.Collection;

public class CollectionListAdapter extends MindpinBaseAdapter<Collection> {
	
	private boolean edit_mode;
	
	public CollectionListAdapter(MindpinBaseActivity activity) {
		super(activity);
		this.edit_mode = false;
	}
	
	@Override
	public View inflate_view() {
		return inflate(R.layout.collection_list_item, null);
	}
	
	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
		
		view_holder.id_textview 	 		   = (TextView) view.findViewById(R.id.collection_id);
		view_holder.title_textview  		   = (TextView) view.findViewById(R.id.collection_title);
		view_holder.edit_collection_button 	   = (Button)   view.findViewById(R.id.edit_collection);
		view_holder.destroy_collection_button  = (Button)   view.findViewById(R.id.destroy_collection);
		
		return view_holder;
	}
	
	@Override
	public void fill_with_data(BaseViewHolder holder, Collection collection, int position) {
		ViewHolder view_holder = (ViewHolder) holder;
		
		view_holder.id_textview.setText(collection.collection_id+"");
		view_holder.title_textview.setText(collection.title);
		
		bind_button_event(position, view_holder, collection);
	}

	private void bind_button_event(final int position, ViewHolder view_holder, Collection collection) {
		if (this.edit_mode) {
			view_holder.edit_collection_button.setVisibility(View.VISIBLE);
			view_holder.destroy_collection_button.setVisibility(View.VISIBLE);

			final String collection_id = collection.collection_id + "";
			final String title = collection.title;
			
			view_holder.edit_collection_button
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							show_change_collection_name_dialog(position,
									collection_id, title);
						}
					});
			view_holder.destroy_collection_button
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							show_destroy_collection_dialog(position,
									collection_id);
						}
					});
		} else {
			view_holder.edit_collection_button.setVisibility(View.GONE);
			view_holder.destroy_collection_button.setVisibility(View.GONE);
		}
	}

	public void start_edit_mode() {
		this.edit_mode = true;
		this.notifyDataSetChanged();
	}

	public void end_edit_mode() {
		this.edit_mode = false;
		this.notifyDataSetChanged();
	}

	public boolean is_edit_mode() {
		return this.edit_mode;
	}

	private void show_change_collection_name_dialog(final int position,
			final String id, final String old_title) {
		final View view = inflate(R.layout.change_collection_name_dialog, null);
		EditText ctet = (EditText) view.findViewById(R.id.collection_title_et);
		ctet.setText(old_title);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("修改标题");
		builder.setView(view);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText ctet = (EditText) view
						.findViewById(R.id.collection_title_et);
				final String title = ctet.getText().toString();
				if (title == null || "".equals(title)) {
					BaseUtils.toast("请输入标题");
					return;
				}

				new MindpinAsyncTask<String, Void, Boolean>(activity, R.string.now_updating) {
					@Override
					public Boolean do_in_background(String... params)
							throws Exception {
						String id_str = params[0];
						int id1 = Integer.parseInt(id_str);
						String title = params[1];
						return HttpApi.change_collection_name(id1, title);
					}

					@Override
					public void on_success(Boolean result) {
						if (result) {
							BaseUtils.toast("操作成功");
							change_collection_name(position, title);
						} else {
							BaseUtils.toast("操作失败");
						}
					}
				}.execute(id, title);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}

	private void change_collection_name(int position, String title) {
		Collection collection = fetch_item(position);
		
		collection.title = title;
		this.notifyDataSetChanged();
	}

	private void show_destroy_collection_dialog(final int position,
			final String id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("确定删除这个收集册吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				new MindpinAsyncTask<String, Void, Boolean>(activity, R.string.now_deleting) {
					@Override
					public Boolean do_in_background(String... params)
							throws Exception {
						int id = Integer.parseInt(params[0]);
						return HttpApi.destroy_collection(id);
					}

					@Override
					public void on_success(Boolean result) {
						if (result) {
							BaseUtils.toast("操作成功");
							remove_item(position);
						} else {
							BaseUtils.toast("操作失败");
						}
					}
				}.execute(id);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}

	private class ViewHolder implements BaseViewHolder {
		public Button destroy_collection_button;
		public Button edit_collection_button;
		public TextView id_textview;
		public TextView title_textview;
	}
}
