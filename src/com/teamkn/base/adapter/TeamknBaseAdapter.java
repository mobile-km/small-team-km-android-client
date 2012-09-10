package com.teamkn.base.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.DataItem;

import java.util.ArrayList;
import java.util.List;

public abstract class TeamknBaseAdapter<M> extends BaseAdapter {
    public TeamknBaseActivity activity;
    private List<M> items;

    public TeamknBaseAdapter(TeamknBaseActivity activity) {
        this.activity = activity;
        items = new ArrayList<M>();
    }

    public View inflate(int resource, ViewGroup root) {
        return activity.getLayoutInflater().inflate(resource, root);
    }

    public M fetch_item(int position) {
        return this.items.get(position);
    }

    public void remove_item(M item) {
        this.items.remove(item);
        this.notifyDataSetChanged();
    }
    public void insert_item(M item , int to){
    	List<M> items_insert = new ArrayList<M>();
    	if(to==items.size()+1){
    		for(int i = 0 ; i <items.size(); i ++){
    			items_insert.add(items.get(i));
    		}
    		items_insert.add(item);
    	}else if (to==items.size()){
    		for(int i = 0 ; i <items.size() ; i ++){
    			if( i == to-1){
    				items_insert.add(item);
    				items_insert.add(items.get(i));	
    			}else{
    				items_insert.add(items.get(i));
    			}
    	   }
    	}else{
    		for(int i = 0 ; i <items.size(); i ++){
    			if( i == to-1){
    				items_insert.add(item);
    				if(to-1 != items.size()){
    					items_insert.add(items.get(i));
    				}	
    			}else{
    				items_insert.add(items.get(i));
    			}
    		}
    	} 
//    	this.items.add(to, item);
    	this.items = items_insert;
    	this.notifyDataSetChanged();
    }
    public void remove_item(int position) {
        this.items.remove(position);
        this.notifyDataSetChanged();
    }

    public void add_item(M item) {
        this.items.add(item);
        this.notifyDataSetChanged();
    }

    public void add_items(List<M> input_items) {
        for (M item : input_items) {
            this.items.add(item);
        }
        this.notifyDataSetChanged();
    }


    //------------

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        M item = fetch_item(position);
        convertView = _generate_view_holder(convertView);

        BaseViewHolder view_holder = (BaseViewHolder) convertView.getTag();
        fill_with_data(view_holder, item, position);

        return convertView;
    }

    private View _generate_view_holder(View view) {
        if (null == view) {
            view = inflate_view();
            view.setTag(build_view_holder(view));
        }
        return view;
    }

    public abstract View inflate_view();

    public abstract BaseViewHolder build_view_holder(View view);

    // 用户实现这个方法时，需要自己进行Object -> ViewHolder的类型转换
    public abstract void fill_with_data(BaseViewHolder holder, M item, int position);

    //----------------------

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //-----------

    abstract public interface BaseViewHolder {
    }

    ;
}
