package com.mindpin.base.activity;

import java.util.List;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mindpin.R;
import com.mindpin.base.adapter.MindpinBaseAdapter;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.widget.view.ListMoreButton;

abstract public class MindpinSimpleDataList<M, A extends MindpinBaseAdapter<M>> {

	private ListView list_view;
	private A adapter;
	
	//private int per_page = 20;
	
	public MindpinSimpleDataList(ListView list_view, A adapter){
		this.list_view = list_view;
		this.adapter   = adapter;
	}
	
	public A get_adapter(){
		return adapter;
	}
	
//	public void set_per_page(int per_page){
//		this.per_page = 20;
//	}
	

	public void load() {
		new MindpinAsyncTask<String, Void, List<M>>(adapter.activity, R.string.now_loading) {
			@Override
			public List<M> do_in_background(String... params)
					throws Exception {
				return load_list_data();
			}

			@Override
			public void on_success(List<M> items) {
				adapter.add_items(items);
				bind_load_more_button_event();
				
//				if(items.size() < per_page){
//					load_more_view.setVisibility(View.GONE);
//				}
				
				list_view.setAdapter(adapter);
				list_view.setOnItemClickListener(list_item_click_listener());
			}
		}.execute();
	}
	
	public ListMoreButton<M> bind_load_more_button_event(){
		final ListMoreButton<M> load_more_view = new ListMoreButton<M>(adapter){
			@Override
			public List<M> load() throws Exception{
				return load_list_more_data();
			}
			
		};
		
		list_view.addFooterView(load_more_view);
		return load_more_view;
	}
	
	abstract public List<M> load_list_data() throws Exception;
	
	abstract public List<M> load_list_more_data() throws Exception;
	
	abstract public OnItemClickListener list_item_click_listener();
}
