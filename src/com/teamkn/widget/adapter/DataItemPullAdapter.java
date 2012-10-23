package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.User;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.model.database.UserDBHelper;

public class DataItemPullAdapter extends TeamknBaseAdapter<User> {
    public DataItemPullAdapter(TeamknBaseActivity activity) {
        super(activity);
    }
    @Override
    public View inflate_view() {
        return inflate(R.layout.list_data_item_pull_list_item, null);
    }

    @Override
    public BaseViewHolder build_view_holder(View view) {
        ViewHolder view_holder      = new ViewHolder();
        view_holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
        
        view_holder.user_avatar_iv= (ImageView)view.findViewById(R.id.user_avatar_iv);
        view_holder.user_name_tv = (TextView)view.findViewById(R.id.user_name_tv);
        view_holder.data_item_title_tv = (TextView)view.findViewById(R.id.data_item_title_tv);
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final User item,
                               int position) {
        ViewHolder view_holder = (ViewHolder) holder;
        view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
        
        view_holder.user_name_tv.setText(item.user_name);
    	if (item.user_avatar!=null) {
    		Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(item.user_avatar));
        	Drawable drawable = new BitmapDrawable(bitmap);
    		view_holder.user_avatar_iv.setBackgroundDrawable(drawable);
        } else {
        	view_holder.user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
        }
        view_holder.data_item_title_tv.setText(item.count + "项修改建议");
    }
    
    private class ViewHolder implements BaseViewHolder {
    	TextView info_tv;
        // 公共列表子项显示
        ImageView user_avatar_iv;
        TextView user_name_tv;
        TextView data_item_title_tv;
    }
}
