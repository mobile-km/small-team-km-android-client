package com.teamkn.widget.adapter;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Chat;
import com.teamkn.model.User;

public class ChatListAdapter extends TeamknBaseAdapter<Chat> {

  public ChatListAdapter(TeamknBaseActivity activity) {
    super(activity);
  }

  @Override
  public View inflate_view() {
    return inflate(R.layout.list_chat_item, null);
  }

  @Override
  public BaseViewHolder build_view_holder(
      View view) {
    
    ViewHolder view_holder      = new ViewHolder();
    
    view_holder.chat_id_tv = (TextView)  view.findViewById(R.id.chat_id_tv);
    view_holder.chat_title_tv = (TextView)  view.findViewById(R.id.chat_title_tv);
    view_holder.chat_data_tv = (TextView) view.findViewById(R.id.chat_data_tv);
    return view_holder;
  }

  @Override
  public void fill_with_data(BaseViewHolder holder, Chat item, int position) {
    
    ViewHolder view_holder = (ViewHolder) holder;
    List<String> name_list = new ArrayList<String>();
    for(User user : item.members){
      name_list.add(user.user_name);
    }
    String names_str = BaseUtils.string_list_to_string(name_list);
    
    view_holder.chat_title_tv.setText(BaseUtils.date_string(item.server_updated_time)+"   "+names_str);
    view_holder.chat_data_tv.setVisibility(View.VISIBLE);
//    view_holder.chat_data_tv.setText(item.server_updated_time+"   ");
    view_holder.chat_id_tv.setTag(item.id);
  }
  
  private class ViewHolder implements BaseViewHolder {
    TextView chat_id_tv;
    TextView chat_title_tv;
    TextView chat_data_tv;
  }
}
