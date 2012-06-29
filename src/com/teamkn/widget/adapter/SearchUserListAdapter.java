package com.teamkn.widget.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.Logic.SearchUser;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.cache.image.ImageCache;

public class SearchUserListAdapter extends TeamknBaseAdapter<SearchUser> {

  public SearchUserListAdapter(TeamknBaseActivity activity) {
    super(activity);
  }

  @Override
  public View inflate_view() {
    return inflate(R.layout.list_search_user_item, null);
  }

  @Override
  public ViewHolder build_view_holder(
      View view) {
    
    ViewHolder view_holder      = new ViewHolder();
    view_holder.user_avatar_iv   = (ImageView) view.findViewById(R.id.search_user_avatar_iv);
    view_holder.user_name_tv = (TextView)  view.findViewById(R.id.search_user_name_tv);
    return view_holder;
    
  }

  @Override
  public void fill_with_data(
      com.teamkn.base.adapter.TeamknBaseAdapter.BaseViewHolder holder,
      SearchUser item, int position) {
    
    ViewHolder view_holder = (ViewHolder) holder;
    view_holder.user_name_tv.setText(item.user_name);
    ImageCache.load_cached_image(item.user_avator_url, view_holder.user_avatar_iv);
  }
  
  private class ViewHolder implements BaseViewHolder {
    ImageView user_avatar_iv;
    TextView  user_name_tv;
  }

}
