package com.teamkn.widget.adapter;

import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.Product;

public class ProductAdapter extends TeamknBaseAdapter<Product> {
	public ProductAdapter(TeamknBaseActivity activity) {
		super(activity);
	}
	
	@Override
	public View inflate_view() {
		return inflate(R.layout.tknlist_product_item, null);
	}
	
	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
		
	    view_holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
		
        view_holder.product_name = (TextView) view.findViewById(R.id.product_name);
        view_holder.product_kind = (TextView) view.findViewById(R.id.product_kind);
        view_holder.product_unit = (TextView) view.findViewById(R.id.product_unit);
        view_holder.product_origin = (TextView) view.findViewById(R.id.product_origin);
        view_holder.product_vendor = (TextView) view.findViewById(R.id.product_vendor);
        return view_holder;
	}
	
	@Override
	public void fill_with_data(BaseViewHolder holder,Product item, int position) {		
		 ViewHolder view_holder = (ViewHolder) holder;
		 view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
		 
	     view_holder.product_name.setText(item.name);
	     view_holder.product_kind.setText(item.kind);
	     view_holder.product_unit.setText(item.unit);
	     view_holder.product_origin.setText(item.origin);
	     view_holder.product_vendor.setText(item.vendor);
	}
	
	private class ViewHolder implements BaseViewHolder {
		TextView info_tv;
		
		TextView product_name;
		TextView product_kind;
		TextView product_unit;
		TextView product_origin;
		TextView product_vendor;
    }
}
