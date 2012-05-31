package com.teamkn.widget.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.base.activity.MindpinBaseActivity;
import com.teamkn.base.adapter.MindpinBaseAdapter;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.AccountUser;
import com.teamkn.model.database.AccountUserDBHelper;

public class AccountListAdapter extends MindpinBaseAdapter<AccountUser> {
	
	private boolean is_edit_mode = false;

	public AccountListAdapter(MindpinBaseActivity activity) {
		super(activity);
	}

	@Override
	public View inflate_view() {
		return inflate(R.layout.list_account_item, null);
	}

	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
		
		view_holder.current_account_icon_imageview = (ImageView) view.findViewById(R.id.current_account_icon);
		view_holder.account_avatar_imageview       = (ImageView) view.findViewById(R.id.account_avatar);
		view_holder.account_name_textview          = (TextView)  view.findViewById(R.id.account_name);
		view_holder.delete_button                  = (Button)    view.findViewById(R.id.account_delete);
		
		return view_holder;
	}

	@Override
	public void fill_with_data(BaseViewHolder holder, final AccountUser account_user, int position) {
		ViewHolder view_holder = (ViewHolder) holder;
		
		if(is_edit_mode){
			view_holder.delete_button.setVisibility(View.VISIBLE);
		}else{
			view_holder.delete_button.setVisibility(View.GONE);
		}
		view_holder.delete_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AccountUserDBHelper.destroy(account_user);
				remove_account(account_user);
			}
		});
		
		
		if(is_current_user_icon_showing(account_user, position)){
			view_holder.current_account_icon_imageview.setVisibility(View.VISIBLE);
		}else{
			view_holder.current_account_icon_imageview.setVisibility(View.GONE);
		}
		
		view_holder.account_name_textview.setText(account_user.name);
		ImageCache.load_cached_image(account_user.avatar_url, view_holder.account_avatar_imageview);
	}
	
	private boolean is_current_user_icon_showing(AccountUser account_user, int position){
		AccountUser current_user = AccountManager.current_user();
		
		boolean c1 = current_user.user_id == account_user.user_id;
		boolean c2 = current_user.is_nil() && 0 == position;
		
		return c1 || c2;
	}
	
	private void remove_account(AccountUser account_user){
		remove_item(account_user);
		if(0 == getCount()){
			this.activity.restart_to_login();
		}
	}
	
	public void open_edit_mode(){
		is_edit_mode = true;
		this.notifyDataSetChanged();
	}
	
	public void close_edit_mode(){
		is_edit_mode = false;
		this.notifyDataSetChanged();
	}
	
	public boolean is_edit_mode(){
		return is_edit_mode;
	}
	
	private class ViewHolder implements BaseViewHolder{
		ImageView current_account_icon_imageview;
		ImageView account_avatar_imageview;
		TextView  account_name_textview;
		Button delete_button;
	}
}
