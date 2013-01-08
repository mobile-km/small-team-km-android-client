package com.teamkn.activity.dataitem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.Product;

public class ShowProductDataItem extends TeamknBaseActivity{
	TextView data_list_title_tv,data_item_title,
			 data_item_name,data_item_kind,
			 data_item_unit,data_item_origin,data_item_vendor;
	String barcode_format  ;
	String text  ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_product_dataitem);
		
		Intent intent = getIntent();
		DataItem dataItem = (DataItem) intent.getSerializableExtra("data_item");
		DataList data_list = (DataList)intent.getSerializableExtra("data_list");
		Product product = (Product)intent.getSerializableExtra("product");
		load_ui();

		show_ui(data_list,dataItem,product);
	}
	private void load_ui() {
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_item_title = (TextView)findViewById(R.id.product_title);
		data_item_name = (TextView)findViewById(R.id.product_name);
		data_item_kind = (TextView)findViewById(R.id.product_kind);
		data_item_unit = (TextView)findViewById(R.id.product_unit);
		data_item_origin = (TextView)findViewById(R.id.product_origin);
		data_item_vendor = (TextView)findViewById(R.id.product_vendor);
	}
	private void show_ui(DataList data_list,DataItem data_item,Product product) {
		if(data_item == null || data_list == null){
			return ;
		}
		data_list_title_tv.setText(data_list.title);
		data_item_title.setText(data_item.title);
		data_item_name.setText(product.name);
		data_item_kind.setText(product.kind);
		data_item_unit.setText(product.unit);
		data_item_origin.setText(product.origin);
		data_item_vendor.setText(product.vendor);
		
	}
	
}

