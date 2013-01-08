package com.teamkn.activity.qrcode_result;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.Product;
import com.teamkn.model.QRCodeResult;
import com.teamkn.widget.adapter.ProductAdapter;

public class QRCodeResultActivity extends TeamknBaseActivity{
	static class RequestCode{
		final static int QUERY = 1; 
		final static int SAVE = 2; 
	}
	ListView list_view;
	List<Product> products;
	DataItem data_item;
	DataList data_list;
	String data_list_public;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_result);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		QRCodeResult code_result = (QRCodeResult) bundle.get("code_result");
		data_item = (DataItem)bundle.get("data_item");
		
		data_list = (DataList) bundle.get("data_list");
		data_list_public = bundle.getString("data_list_public");
		
		list_view = (ListView) findViewById(R.id.list_view);
		
		http_api(RequestCode.QUERY,code_result.code,null);
		
	}
	private void http_api(final int type , final String code ,final DataItem dataitem) {
		if(BaseUtils.is_str_blank(code) && type == RequestCode.QUERY){
			return ;
		}
    	if (!BaseUtils.is_wifi_active(this)) {
    		BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
    		return ;
    	}
        new TeamknAsyncTask<String, Void, List<Product>>(this, R.string.now_loading) {
            @Override
            public List<Product> do_in_background(String... params) throws Exception {
            	switch (type) {
				case RequestCode.QUERY:
					products = HttpApi.get_qrcode_search(code);
					break;
				case RequestCode.SAVE:
					HttpApi.DataItem.create(dataitem);
					break;
				default:
					break;
				}
				return products;
            }
            @Override
            public void on_success(List<Product> result) {
            	switch (type) {
				case RequestCode.QUERY:
	            	System.out.println(result.size());
	            	if(result.size()>0){
	            		load_list();
	            	}
					break;
				case RequestCode.SAVE:
					Intent intent = new Intent(QRCodeResultActivity.this,DataItemListActivity.class);
		    		intent.putExtra("data_list", data_list);
		    		intent.putExtra("data_list_public", data_list_public);
		    		startActivity(intent);
		    		finish();
					break;
				default:
					break;
				}
            }
        }.execute();
    }
	
	private void load_list() {
		ProductAdapter adapter = new ProductAdapter(this);
		adapter.add_items(products);
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				TextView info_tv = (TextView) list_item.findViewById(R.id.info_tv);
				Product item = (Product) info_tv.getTag(R.id.tag_note_uuid);
				alert_dialog(item);
			}
		});
	}
	private void alert_dialog(final Product product){
		AlertDialog.Builder builder = new Builder(QRCodeResultActivity.this);
		builder.setTitle("请确定要添加选中的条目");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				data_item.setProduct(product);
				data_item.setKind(DataItem.Kind.PRODUCT);
				data_item.setData_list_id(data_list.server_data_list_id);
				http_api(RequestCode.SAVE,null,data_item);
			}
		});
		builder.show();
	}
	
}	
